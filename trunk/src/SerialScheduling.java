import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import model.Activity;
import model.Model;
import model.Qualification;
import model.Resource;

public class SerialScheduling {

	private Map<String,Activity> assignedActivities=new HashMap<String,Activity>();
	private Model model;
	
	
	public SerialScheduling(Model model) {
		this.model=model;
	}
	
	public void run() {
		for(Activity act:model.getActivities()) {
			if(!schedule(act)) {
				break;
			}
		}
	}
	
	private boolean schedule(Activity activity) {
		Map<Resource, List<TimeSlice>> slices = generate();
		long earliestTime=Long.MIN_VALUE;
		for(Activity act:activity.getPredecessors()) {
			if(!assignedActivities.containsKey(act.getId())) {
				System.out.println("Activity "+activity.getId()+"'s predecessor "+act.getId()+" are not assigned with resources");
				return false;
			}
			if(earliestTime<act.getEndTime()) {
				earliestTime=act.getEndTime();
			}
		}
		
		Map<String, Integer> requirment=new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> assignments=new HashMap<String, Map<String, Integer>>();
		
		for(Resource res:model.getResources().values()) {
			res.setAvailableAmount(res.getAmount());
		}
		
		for(String qua:activity.getMode().getQualificationAmountMap().keySet()) {
			Map<String, Integer> resAmount=new HashMap<String,Integer>();
			int amountNeeded=activity.getMode().getQualificationAmountMap().get(qua);
			for(String resName:model.getQualificationResourceRelation().get(qua)) {
				if(amountNeeded==0) {
					break;
				}
				
				Resource res=model.getResources().get(resName);
				if(res.getAvailableAmount()>=amountNeeded) {
					resAmount.put(resName, amountNeeded);
					res.seize(amountNeeded);
					amountNeeded=0;
					
				}else {
					resAmount.put(resName, res.getAvailableAmount());
					res.seize(res.getAvailableAmount());
					amountNeeded-=res.getAvailableAmount();
					
				}
				
			}
			if(amountNeeded>0) {
				System.out.println("Activity "+activity.getId()+" has no enough resources with qualification "+qua);
				return false;
			}
			assignments.put(qua, resAmount);
		}
		activity.setAssignment(assignments);
		
		
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
		assignedActivities.put(activity.getId(),activity);
		return true;
	}
	
	private Map<Resource, List<TimeSlice>> generate() {
		Map<Resource, Set<Long>> timePoints = new HashMap<Resource, Set<Long>>();
		Map<Resource, List<TimeSlice>> slices = new HashMap<Resource, List<TimeSlice>>();

		for (Activity act : assignedActivities.values()) {
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

		for (Activity act : assignedActivities.values()) {
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
	
	private Map<Qualification, List<TimeSlice>> generate1() {
		Map<Qualification, Set<Long>> timePoints = new HashMap<Qualification, Set<Long>>();
		Map<Qualification, List<TimeSlice>> slices = new HashMap<Qualification, List<TimeSlice>>();

		for (Activity act : assignedActivities.values()) {
			Map<String, Map<String, Integer>> assignment = act.getAssignment();

			for (String qua : assignment.keySet()) {

					Set<Long> points = timePoints.get(model.getQualifications().get(qua));
					if (points == null) {
						points = new HashSet<Long>();
						timePoints.put(model.getQualifications().get(qua), points);
					}

					points.add(act.getStartTime());

					points.add(act.getEndTime());
			}
		}

		for (Qualification qua : timePoints.keySet()) {
			Set<Long> points = timePoints.get(qua);
			ArrayList<Long> sortedPoints = new ArrayList<Long>(new TreeSet<Long>(points));
			List<TimeSlice> slice = new ArrayList<TimeSlice>();
			slices.put(qua, slice);
			for (int i = 0; i < sortedPoints.size() - 1; i++) {
				TimeSlice ts = new TimeSlice(sortedPoints.get(i), sortedPoints.get(i + 1));
				slice.add(ts);
			}
		}

		for (Activity act : assignedActivities.values()) {
			Map<String, Map<String, Integer>> assignment = act.getAssignment();

			for (String qua : assignment.keySet()) {
						
					List<TimeSlice> slice = slices.get(model.getQualifications().get(qua));
					int amount=act.getMode().getQualificationAmountMap().get(qua);
					for (TimeSlice ts : slice) {

						if (act.getStartTime() <= ts.getStart() && act.getEndTime() >= ts.getEnd()) {
							ts.addActivity(act, amount);
							ts.addAmount(amount);

						}
					}
				

			}
		}
		return slices;

	}
}
