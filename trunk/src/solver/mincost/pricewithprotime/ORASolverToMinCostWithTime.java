
package solver.mincost.pricewithprotime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import problem.Activity;
import problem.ResourcesForOneActivity;
import problem.OverlappingResAllocProblem;
import solver.mincost.ORAsolverToMinCost;



/**
 * In the super class, we get an allocation without consideration on price. The
 * goal of this procedure is to revise the results so as to minimize the cost.
 * The cost is related to the processing time.
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class ORASolverToMinCostWithTime extends ORAsolverToMinCost {
	
		
	public ORASolverToMinCostWithTime(OverlappingResAllocProblem problem) {
	
		super(problem);
		
	}
	
	@Override
	public boolean start(boolean tracking) {
	
		return solveByReducingActivity();
	}
	
	/**
	 * Relation between Qualifications and activities
	 * 
	 * @return a map where the key is the qualification id and the value is a
	 *         list of activities which has the qualification.
	 */
	/*protected Map<String, List<Activity>> combinedQuaActRelation() {
	
		for (Activity act : problem.getActList()) {
			for (String quaId : act.getMode().getQualificationAmountMap().keySet()) {
				if (!combinedQuaActRelation.containsKey(quaId)) {
					
					combinedQuaActRelation.put(quaId, new ArrayList<Activity>());
				}
				combinedQuaActRelation.get(quaId).add(act);
			}
		}
		for (List<Activity> list : combinedQuaActRelation.values()) {
			sortActivities(list);
		}
		
		return combinedQuaActRelation;
		
	}
	
	/**
	 * Sort resources by the price from the highest to the lowest
	 * 
	 * @param map
	 * @return
	 */
	/*private List<String> sortResDescending(Map<String, Integer> map) {
	
		ArrayList<String> sortedResDes = new ArrayList<String>();
		for (int i = sortedResList.size() - 1; i >= 0; i--) {
			if (map.keySet().contains(sortedResList.get(i))) {
				sortedResDes.add(sortedResList.get(i));
			}
		}
		return sortedResDes;
		
	}*/
	
	/**
	 * Sort activities by the processing time from the shortest to the longest
	 * 
	 * @param actList
	 */
	/*private void sortActivities(List<Activity> actList) {
	
		for (int i = 0; i < actList.size() - 1; i++) {
			long costi = actList.get(i).getMode().getRawProcessingTime();
			for (int j = i + 1; j < actList.size(); j++) {
				long costj = actList.get(j).getMode().getRawProcessingTime();
				if (costj < costi) {
					Activity act1 = actList.get(i);
					Activity act2 = actList.get(j);
					actList.remove(i);
					actList.add(i, act2);
					actList.remove(j);
					actList.add(j, act1);
				}
			}
			
		}
		
	}
	
	/*@Override
	protected void generateResults() {
	
		Map<String, Set<Integer>> copy = new HashMap<String, Set<Integer>>(problem.getAvailableResAmount());
		for (String resId : copy.keySet()) {
			Set<Integer> set = new HashSet<Integer>(problem.getAvailableResAmount().get(resId));
			copy.put(resId, set);
		}
		for (String actid : resultByActivity.keySet()) {
			ResourcesForOneActivity cho = new ResourcesForOneActivity();
			results.put(actid, cho);
			for (String quId : resultByActivity.get(actid).keySet()) {
				for (String resid : resultByActivity.get(actid).get(quId).keySet()) {
					for (int i = 0; i < resultByActivity.get(actid).get(quId).get(resid); i++) {
						cho.addResources(problem.getQuaMap().get(quId), problem.getResMap().get(resid), copy.get(resid).iterator().next());
						copy.get(resid).remove(copy.get(resid).iterator().next());
					}
					
				}
			}
		}
	}
	
	/**
	 * Because we combine the activities before we solve it. Now we assign the
	 * allocated resources to each activity.
	 * 
	 */
	/*protected void assignResToAct() {
	
		Map<String, Map<String, Integer>> unitedResultsCopy = new HashMap<String, Map<String, Integer>>();
		for (String qua : unitedResults.keySet()) {
			Map<String, Integer> allo = new HashMap<String, Integer>(unitedResults.get(qua));
			unitedResultsCopy.put(qua, allo);
		}
		
		for (String quaId : unitedResults.keySet()) {
			List<String> sortRes = sortResDescending(unitedResults.get(quaId));
			List<Activity> actList = combinedQuaActRelation.get(quaId);
			for (Activity act : actList) {
				if (!resultByActivity.containsKey(act.getId())) {
					Map<String, Map<String, Integer>> a = new HashMap<String, Map<String, Integer>>();
					resultByActivity.put(act.getId(), a);
				}
				Map<String, Map<String, Integer>> a = resultByActivity.get(act.getId());
				Map<String, Integer> b = new HashMap<String, Integer>();
				a.put(quaId, b);
				int sum = 0;
				for (String resID : sortRes) {
					sum += unitedResultsCopy.get(quaId).get(resID);
					if (sum < act.getMode().getQualificationAmountMap().get(quaId)) {
						b.put(resID, unitedResultsCopy.get(quaId).get(resID));
						
						unitedResultsCopy.get(quaId).put(resID, 0);
					}
					else {
						b.put(resID, unitedResultsCopy.get(quaId).get(resID) - (sum - act.getMode().getQualificationAmountMap().get(quaId)));
						unitedResultsCopy.get(quaId).put(resID, sum - act.getMode().getQualificationAmountMap().get(quaId));
						break;
					}
				}
			}
			
		}
		
	}*/
	
	public void printTempResult(Map<String, Map<String, Map<String, Integer>>> resultByActivity, String s) {
	
		System.out.println("=========================================");
		System.out.println("Temp Results in detail, " + s);
		for (String act : resultByActivity.keySet()) {
			System.out.println("Activity:" + act);
			for (String qua : resultByActivity.get(act).keySet()) {
				System.out.println("        Qualificaiton:" + qua);
				for (String res : resultByActivity.get(act).get(qua).keySet()) {
					if (resultByActivity.get(act).get(qua).get(res) == 0) {
						continue;
					}
					String str = "                        ResID: " + res;
					int len = str.length();
					for (int i = 0; i < 40 - len; i++) {
						str += " ";
					}
					System.out.println(str + "      Amount:" + resultByActivity.get(act).get(qua).get(res));
					
				}
				
			}
		}
	}
}
