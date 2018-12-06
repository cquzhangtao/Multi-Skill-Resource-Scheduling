import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import model.Activity;
import model.Model;
import model.Resource;
import solver.AbstractORASlover;
import solver.mincost.pricewithoutprotime.ORASolverByIteratingExchange;

public class ParallelScheduling {
	
	
	private Map<String,Activity> unassignedActivities;
	private List<Activity> assignedActivities=new ArrayList<Activity>();;
	private Model model;
	private List<Activity> processingActivities=new ArrayList<Activity>();
	private long timeWindow=1;
	
	
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
		submodel.print();
		solver=new ORASolverByIteratingExchange(submodel);
		solver.solve();
		solver.print();
		return solver.getResults();
	}
	
	public void run() {
		start(0);
	}
	
	
	private void pushBack(Activity activity) {
		Map<Resource, List<TimeSlice>> slices = generate();
		long earliestTime=Long.MIN_VALUE;
		for(Activity act:activity.getPredecessors()) {
			if(earliestTime<act.getEndTime()) {
				earliestTime=act.getEndTime();
			}
		}
		
		Map<String, Integer> requirment=new HashMap<String, Integer>();
		for(Map<String, Integer> assignment:activity.getAssignment().values()) {
			for(String res:assignment.keySet()) {
				if(!requirment.containsKey(res)) {
					requirment.put(res, 0);
				}
				requirment.put(res, requirment.get(res)+assignment.get(res));
			}
		}
		
		long earliestTimeRes=Long.MIN_VALUE;
		for(String resName:requirment.keySet()) {
			int amount=requirment.get(resName);
			Resource res=model.getResources().get(resName);
			List<TimeSlice> slice = slices.get(res);
			if(slice==null) {
				continue;
			}
			//long time=activity.getStartTime();
			long time=slice.get(slice.size()-1).getEnd();
			for(int i=slice.size()-1;i>=0;i--) {
				TimeSlice ts=slice.get(i);
				if(res.getAmount()-ts.getAmount()>=amount) {
					time=ts.getStart();
				}else {
					break;
				}
			}
			if(earliestTimeRes<time) {
				earliestTimeRes=time;
			}
		}
		
		activity.setStartTime(Long.max(0,Long.max(earliestTime, earliestTimeRes)));
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
				pushBack(act);
				unassignedActivities.remove(actName);
				assignedActivities.add(act);
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
	
	
	private Map<Resource, List<TimeSlice>> generate() {
		Map<Resource, Set<Long>> timePoints = new HashMap<Resource, Set<Long>>();
		Map<Resource, List<TimeSlice>> slices = new HashMap<Resource, List<TimeSlice>>();

		for (Activity act : assignedActivities) {
			Map<String, Map<String, Integer>> assignment = act.getAssignment();

			for (Map<String, Integer> ress : assignment.values()) {
				for (String res : ress.keySet()) {

					Set<Long> points = timePoints.get(model.getResources().get(res));
					if (points == null) {
						points = new HashSet<Long>();
						timePoints.put(model.getResources().get(res), points);
					}

					points.add(act.getStartTime());

					points.add(act.getEndTime());

				}
			}
		}

		for (Resource res : timePoints.keySet()) {
			Set<Long> points = timePoints.get(res);
			ArrayList<Long> sortedPoints = new ArrayList<Long>(new TreeSet<Long>(points));
			List<TimeSlice> slice = new ArrayList<TimeSlice>();
			slices.put(res, slice);
			for (int i = 0; i < sortedPoints.size() - 1; i++) {
				TimeSlice ts = new TimeSlice(sortedPoints.get(i), sortedPoints.get(i + 1));
				slice.add(ts);
			}
		}

		for (Activity act : assignedActivities) {
			Map<String, Map<String, Integer>> assignment = act.getAssignment();

			for (Map<String, Integer> ress : assignment.values()) {
				for (String res : ress.keySet()) {
					int amount = ress.get(res);
					List<TimeSlice> slice = slices.get(model.getResources().get(res));

					for (TimeSlice ts : slice) {

						if (act.getStartTime() <= ts.getStart() && act.getEndTime() >= ts.getEnd()) {
							ts.addActivity(act, amount);
							ts.addAmount(amount);

						}
					}
				}

			}
		}
		return slices;

	}



}
