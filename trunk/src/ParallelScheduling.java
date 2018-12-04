import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import problem.Activity;
import problem.Resource;

public class ParallelScheduling {
	
	
	private Map<String,Activity> unassignedActivities;
	private List<Activity> processingActivities;
	private Map<String,Resource> resources;
	Map<String, List<String>> quaResRelationMap=new HashMap<String, List<String>>();
	private long timeWindow=100;
	
	
	public ParallelScheduling(Map<String,Activity> activities,Map<String,Resource> resources) {
		unassignedActivities=activities;
		this.resources=resources;
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
		// TODO Auto-generated method stub
		return null;
	}
	
	public void start(long time) {
		Map<String, Map<String, Map<String, Integer>>> results;
		List<Activity> startableActivities=getStartableActivities(time);
		results=subsolve(resources,startableActivities);
		if(results!=null) {
			long earliestEndTime=Long.MAX_VALUE;
			for(String actName:results.keySet()) {
				
				Map<String, Map<String, Integer>> qua=results.get(actName);
				Activity act=unassignedActivities.get(actName);
				act.setStarted(true);
				act.setStartTime(time);
				act.setAssignment(qua);
				unassignedActivities.remove(actName);
				processingActivities.add(act);
				if(act.getEndTime()<earliestEndTime) {
					earliestEndTime=act.getEndTime();
				}
				
				
				
				for(Map<String, Integer> ress:qua.values()) {
					for(String res:ress.keySet() ) {
						resources.get(res).seize(ress.get(res));
					}
				}
			}
			time=earliestEndTime+timeWindow;
		}else {
			time=time+timeWindow;
		}
		
		List<Activity> finishedActivities=new ArrayList<Activity>();
		for(Activity act:processingActivities) {
			if(act.getEndTime()<=time) {
				for(Map<String, Integer> ress:act.getAssignment().values()) {
					for(String res:ress.keySet() ) {
						resources.get(res).release(ress.get(res));
					}
				}
				finishedActivities.add(act);
			}
		}
		
		processingActivities.removeAll(finishedActivities);
		start(time);
		
	}



}
