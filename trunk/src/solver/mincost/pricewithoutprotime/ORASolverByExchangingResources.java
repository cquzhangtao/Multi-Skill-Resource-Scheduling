
package solver.mincost.pricewithoutprotime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import problem.OverlappingResAllocProblem;
import solver.mincost.ORAsolverToMinCost;



/**
 * Replaces the more expensive used resources with the cheaper available one
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class ORASolverByExchangingResources extends ORAsolverToMinCost {
	
	/**
	 * This procedure is the second. In order to compare the result from
	 * different procedures, we save all of them here.
	 */
	protected List<Map<String, Map<String, Integer>>> unitedResultTracking = new ArrayList<Map<String, Map<String, Integer>>>();
	
	/**
	 * Constructor
	 * 
	 * @param problem
	 */
	public ORASolverByExchangingResources(OverlappingResAllocProblem problem) {
	
		super(problem);
		
	}
	
	@Override
	protected boolean start(boolean tracking) {
	
		this.tracking = tracking;
		if (!solveByReducingActivity()) {
			
			return false;
		}
		track();
		solveByExchangeResource();
		return true;
		
	}
	
	/**
	 * After we get a solution by the function solve(), this function is called.
	 * the function replaces the more expensive used resources with the cheaper
	 * available one.
	 */
	protected void solveByExchangeResource() {
	
		for (String quaId : unitedResults.keySet()) {
			for (String resID : unitedResults.get(quaId).keySet()) {
			
				int index = sortedResList.indexOf(resID);
				for (int i = 0; i < index; i++) {
					if (unitedResults.get(quaId).get(resID) == null) {
						break;
					}
					int allocatedNum = unitedResults.get(quaId).get(resID);
					if (allocatedNum == 0) {
						break;
					}
					int restNum = totalResNumMap.get(sortedResList.get(i)) - usedResNumMap.get(sortedResList.get(i));
					if (restNum < 1 || !problem.getQuaResRelationMap().get(quaId).contains(sortedResList.get(i))) {
						continue;
					}
					
					if (restNum >= allocatedNum) {
						int alreadyHave = 0;
						if (unitedResults.get(quaId).get(sortedResList.get(i)) != null) {
							alreadyHave = unitedResults.get(quaId).get(sortedResList.get(i));
						}
						unitedResults.get(quaId).put(sortedResList.get(i), allocatedNum + alreadyHave);
						unitedResults.get(quaId).remove(resID);
						usedResNumMap.put(resID, usedResNumMap.get(resID) - allocatedNum);
						usedResNumMap.put(sortedResList.get(i), usedResNumMap.get(sortedResList.get(i)) + allocatedNum);
						break;
					}
					else {
						int alreadyHave = 0;
						if (unitedResults.get(quaId).get(sortedResList.get(i)) != null) {
							alreadyHave = unitedResults.get(quaId).get(sortedResList.get(i));
						}
						unitedResults.get(quaId).put(sortedResList.get(i), restNum + alreadyHave);
						unitedResults.get(quaId).put(resID, unitedResults.get(quaId).get(resID) - restNum);
						usedResNumMap.put(resID, usedResNumMap.get(resID) - restNum);
						usedResNumMap.put(sortedResList.get(i), usedResNumMap.get(sortedResList.get(i)) + restNum);
					}
				}
				
			}
		}
		
	}
	
	/**
	 * 
	 * @return total cost of resources we allocated in the result
	 */
	protected double getTotalCost() {
	
		long cost = 0l;
		for (String id : usedResNumMap.keySet()) {
			cost += usedResNumMap.get(id) * sortedCostList.get(sortedResList.indexOf(id));
		}
		return cost;
	}
	
	/**
	 * Save the history results.
	 */
	protected void track() {
	
		if (!tracking) {
			return;
		}
		Map<String, Map<String, Integer>> unitedResultInter = new HashMap<String, Map<String, Integer>>();
		for (String quaID : unitedResults.keySet()) {
			Map<String, Integer> allo = new HashMap<String, Integer>(unitedResults.get(quaID));
			unitedResultInter.put(quaID, allo);
		}
		unitedResultTracking.add(unitedResultInter);
		costTracking.add(getTotalCost());
	}
	
	@Override
	protected void printTrackResults() {
	
		if (tracking) {
			System.out.println("===========================================");
			System.out.println("Tracking");
			int index = 1;
			for (Map<String, Map<String, Integer>> unitedResultInter : unitedResultTracking) {
				printUnitedResults(unitedResultInter, "Phase " + index + ",  Cost " + costTracking.get(index - 1));
				index++;
			}
			printUnitedResults(unitedResults, "Final " + ",  Cost " + getTotalCost());
		}
		
	}
	
}
