import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Activity;
import model.Model;
import model.Resource;
import solver.AbstractORASlover;
import solver.mincost.pricewithoutprotime.ORASolverByIteratingExchange;

public class ParallelScheduling {
	
	
	private Map<String,Activity> unassignedActivities;
	private Model model;
	private List<Activity> processingActivities=new ArrayList<Activity>();
	private long timeWindow=3;
	
	
	public ParallelScheduling(Model model) {
		this.model=model;
		unassignedActivities=new HashMap<String,Activity>(model.getActivityMap());
	}
	
	public List<Activity> getStartableActivities(long time){
		List<Activity> startableActivities=new ArrayList<Activity>();
		for(Activity act:unassignedActivities.values()) {
			if(act.startable(time)) {
				startableActivities.add(act);
			}
		}
		return startableActivities;
	}
	
	private Map<String, Map<String, Map<String, Integer>>> subsolve(Map<String,Resource> resources,
			List<Activity> startableActivities) {
		AbstractORASlover solver;
		Model submodel=new Model(resources,startableActivities,model.getQualifications(),model.getQualificationResourceRelation());
		//submodel.print();
		solver=new ORASolverByIteratingExchange(submodel);
		solver.solve();
		//solver.print();
		return solver.getResults();
	}
	
	public void run() {
		start(0);
	}
	
	private void start(long time) {
		if(unassignedActivities.isEmpty()) {
			return;
		}
		Map<String, Map<String, Map<String, Integer>>> results;
		List<Activity> startableActivities=getStartableActivities(time);
		System.out.println(time+": startable activities:"+startableActivities);
		System.out.println(time+": proecssing activities:"+processingActivities);
		results=subsolve(model.getResources(),startableActivities);
		if(results!=null&&!results.isEmpty()) {
			
			
			for(String actName:results.keySet()) {
				
				Map<String, Map<String, Integer>> qua=results.get(actName);
				Activity act=unassignedActivities.get(actName);
				act.setStarted(true);
				act.setStartTime(time);
				act.setAssignment(qua);
				unassignedActivities.remove(actName);
				processingActivities.add(act);
			
				
				
				
				for(Map<String, Integer> ress:qua.values()) {
					for(String res:ress.keySet() ) {
						model.getResources().get(res).seize(ress.get(res));
					}
				}
			}
			//time=earliestEndTime+timeWindow;
		}else {
			//time=time+timeWindow;
		}
		
		
		long earliestEndTime=Long.MAX_VALUE;
		for(Activity act:processingActivities) {
			if(act.getEndTime()<earliestEndTime) {
				earliestEndTime=act.getEndTime();
			}
		}
		System.out.println(time+": earliest finished time "+earliestEndTime);
		time=earliestEndTime+timeWindow;
		
		List<Activity> finishedActivities=new ArrayList<Activity>();
		for(Activity act:processingActivities) {
			if(act.getEndTime()<=time) {
				for(Map<String, Integer> ress:act.getAssignment().values()) {
					for(String res:ress.keySet() ) {
						model.getResources().get(res).release(ress.get(res));
					}
				}
				finishedActivities.add(act);
			}
		}
		
		processingActivities.removeAll(finishedActivities);

		start(time);
		
	}



}
