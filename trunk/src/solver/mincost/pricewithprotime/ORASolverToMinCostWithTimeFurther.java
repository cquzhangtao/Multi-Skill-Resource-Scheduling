
package solver.mincost.pricewithprotime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Activity;
import model.Model;



/**
 * Using available cheaper resources to replace the more expensive resources
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class ORASolverToMinCostWithTimeFurther extends ORASolverToMinCostWithTime {
	
	protected List<Map<String, Map<String, Map<String, Integer>>>> resultsTracking = new ArrayList<Map<String, Map<String, Map<String, Integer>>>>();
	
	public ORASolverToMinCostWithTimeFurther(Model problem) {
	
		super(problem);
	}
	
	@Override
	public boolean start(boolean tracking) {
	
		this.tracking = tracking;
		
		if (!solveByReducingActivity()) {
			return false;
		}
		
		combinedQuaActRelation();
		assignResToAct();
		track();
		exchange();
		return true;
		
	}
	
	/**
	 * Used to get a good example in which the results become better step by
	 * step
	 * 
	 * @return
	 */
	public boolean test() {
	
		this.tracking = true;
		
		if (!solveByReducingActivity()) {
			return false;
		}
		
		combinedQuaActRelation();
		assignResToAct();
		double cost1 = getTotalCost();
		track();
		exchange();
		double cost2 = getTotalCost();
		if (cost1 != cost2) {
			generateResults();
			return true;
		}
		return false;
	}
	
	/**
	 * Using available cheaper resources to replace the more expensive resources
	 */
	private void exchange() {
	
		for (Activity act : problem.getActivities()) {
			for (String qua : results.get(act.getId()).keySet()) {
				for (String resID : results.get(act.getId()).get(qua).keySet()) {
					
					int index = sortedResList.indexOf(resID);
					for (int i = 0; i < index; i++) {
						if (results.get(act.getId()).get(qua).get(resID) == null) {
							break;
						}
						int allocatedNum = results.get(act.getId()).get(qua).get(resID);
						if (allocatedNum == 0) {
							break;
						}
						int restNum = totalResNumMap.get(sortedResList.get(i)) - usedResNumMap.get(sortedResList.get(i));
						if (restNum < 1 || !problem.getQualificationResourceRelation().get(qua).contains(sortedResList.get(i))) {
							continue;
						}
						
						if (restNum >= allocatedNum) {
							int alreadyHave = 0;
							if (results.get(act.getId()).get(qua).get(sortedResList.get(i)) != null) {
								alreadyHave = results.get(act.getId()).get(qua).get(sortedResList.get(i));
							}
							results.get(act.getId()).get(qua).put(sortedResList.get(i), allocatedNum + alreadyHave);
							results.get(act.getId()).get(qua).remove(resID);
							usedResNumMap.put(resID, usedResNumMap.get(resID) - allocatedNum);
							usedResNumMap.put(sortedResList.get(i), usedResNumMap.get(sortedResList.get(i)) + allocatedNum);
							break;
						}
						else {
							int alreadyHave = 0;
							if (results.get(act.getId()).get(qua).get(sortedResList.get(i)) != null) {
								alreadyHave = results.get(act.getId()).get(qua).get(sortedResList.get(i));
							}
							results.get(act.getId()).get(qua).put(sortedResList.get(i), restNum + alreadyHave);
							results.get(act.getId()).get(qua).put(resID, results.get(act.getId()).get(qua).get(resID) - restNum);
							usedResNumMap.put(resID, usedResNumMap.get(resID) - restNum);
							usedResNumMap.put(sortedResList.get(i), usedResNumMap.get(sortedResList.get(i)) + restNum);
						}
					}
					
				}
			}
		}
	}
	
	protected double getTotalCost() {
	
		long cost = 0l;
		for (Activity act : problem.getActivities()) {
			for (String qua : results.get(act.getId()).keySet()) {
				for (String res : results.get(act.getId()).get(qua).keySet()) {
					int num = results.get(act.getId()).get(qua).get(res);
					double price = sortedCostList.get(sortedResList.indexOf(res));
					long time = act.getMode().getProcessingTime() / 3600;
					cost += num * price * time;
				}
			}
		}
		return cost;
	}
	
	/**
	 * Record the history results
	 */
	protected void track() {
	
		if (!tracking) {
			return;
		}
		Map<String, Map<String, Map<String, Integer>>> resultsTemp = new HashMap<String, Map<String, Map<String, Integer>>>();
		
		for (String actId : results.keySet()) {
			Map<String, Map<String, Integer>> allo = new HashMap<String, Map<String, Integer>>();
			for (String quaId : results.get(actId).keySet()) {
				allo.put(quaId, new HashMap<String, Integer>(results.get(actId).get(quaId)));
			}
			resultsTemp.put(actId, allo);
		}
		resultsTracking.add(resultsTemp);
		costTracking.add(getTotalCost());
	}
	
	@Override
	protected void printTrackResults() {
	
		if (tracking) {
			System.out.println("===========================================");
			System.out.println("Tracking");
			int index = 1;
			for (Map<String, Map<String, Map<String, Integer>>> unitedResultInter : resultsTracking) {
				printTempResult(unitedResultInter, "Phase " + index + ",  Cost " + costTracking.get(index - 1));
				index++;
			}
			printTempResult(results, "Final " + ",  Cost " + getTotalCost());
		}
		
	}
	
}
