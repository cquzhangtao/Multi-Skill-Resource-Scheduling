
package solver.mincost.pricewithoutprotime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.GraphElement;
import model.Model;



/**
 * Exchange unused resources with the used resources in the sharing area. and
 * then let a neighbor use these released resources to replace the expensive
 * resources in the neighbor.
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class ORASolverByReplacingSharingResources extends ORASolverByExchangingResources {
	
	public ORASolverByReplacingSharingResources(Model problem) {
	
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
		track();
		solveByReplaceOverlapping();
		return true;
		
	}
	
	/**
	 * @return if the solution is found, return true; otherwise, return false.
	 */
	protected boolean solveByReplaceOverlapping() {
	
		boolean changed = false;
		graphicSpace.reset();
		for (String quaId : unitedResults.keySet()) {
			for (GraphElement neighbor : graphicSpace.get(quaId).getSharedResources().keySet()) {
				List<String> resourceList = graphicSpace.get(quaId).getSharedResources().get(neighbor);
				int usedResNumInOverlappingArea = 0;
				Map<String, Integer> sharingResMap = new HashMap<String, Integer>();
				for (String resId : unitedResults.get(quaId).keySet()) {
					if (resourceList.contains(resId)) {
						usedResNumInOverlappingArea += unitedResults.get(quaId).get(resId);
						sharingResMap.put(resId, unitedResults.get(quaId).get(resId));
					}
					
				}
				
				
				if (usedResNumInOverlappingArea == 0) {
					continue;// all used resources are not from the overlapping area with the neighbor
				}
				
				Map<String, Integer> unUsedResInCurrentQua = new HashMap<String, Integer>();
				int unusedResNum = 0;
				for (String res : problem.getQualificationResourceRelation().get(quaId)) {
					unUsedResInCurrentQua.put(res, totalResNumMap.get(res) - usedResNumMap.get(res));
					unusedResNum += totalResNumMap.get(res) - usedResNumMap.get(res);
				}
				if (unusedResNum == 0) {
					continue;// all resources with the skill (quaId) are used.
				}
				List<String> sortedUnUsedResInCurrentQua = sortResAscending(unUsedResInCurrentQua);
				Map<String, Integer> usedResInNeighborQua = unitedResults.get(neighbor.getQualification());
				List<String> sortedUsedResInNeighborQua = sortResDescending(usedResInNeighborQua);
				int sumUsed = 0;
				for (String res2 : sortedUsedResInNeighborQua) {
					sumUsed += unitedResults.get(neighbor.getQualification()).get(res2);
				}
				if (sumUsed == 0) {
					continue;// The neighbor does not use any resources.
				}
				int pickUpNum = 0;
				if (usedResNumInOverlappingArea > unusedResNum) {
					pickUpNum = unusedResNum;
					
				}
				else {
					pickUpNum = usedResNumInOverlappingArea;
				}
				if (pickUpNum > sumUsed) {
					pickUpNum = sumUsed;
				}
				while (pickUpNum > 0) {
					Map<String, Integer> pickUpUnusedResFromCurrentQuaMap = new HashMap<String, Integer>();
					Map<String, Integer> pickUpUsedResFromNeighborQuaMap = new HashMap<String, Integer>();
					Long sumUsedCost = getSumCost(pickUpNum, sortedUsedResInNeighborQua, usedResInNeighborQua, pickUpUsedResFromNeighborQuaMap);
					Long sumUnusedCost = getSumCost(pickUpNum, sortedUnUsedResInCurrentQua, unUsedResInCurrentQua, pickUpUnusedResFromCurrentQuaMap);
					if (sumUsedCost <= sumUnusedCost) {
						pickUpNum--;
						continue;
					}
					changed = true;
					for (String id : pickUpUnusedResFromCurrentQuaMap.keySet()) {
						usedResNumMap.put(id, usedResNumMap.get(id) + pickUpUnusedResFromCurrentQuaMap.get(id));
						if (unitedResults.get(quaId).containsKey(id)) {
							unitedResults.get(quaId).put(id, pickUpUnusedResFromCurrentQuaMap.get(id) + unitedResults.get(quaId).get(id));
							
						}
						else {
							unitedResults.get(quaId).put(id, pickUpUnusedResFromCurrentQuaMap.get(id));
							
						}
						
					}
					for (String id : pickUpUsedResFromNeighborQuaMap.keySet()) {
						usedResNumMap.put(id, usedResNumMap.get(id) - pickUpUsedResFromNeighborQuaMap.get(id));
						if (unitedResults.get(neighbor.getQualification()).containsKey(id)) {
							unitedResults.get(neighbor.getQualification()).put(id,
									pickUpUsedResFromNeighborQuaMap.get(id) - unitedResults.get(neighbor.getQualification()).get(id));
							
						}
						else {
							System.out.println("ERROR");
							
						}
						
					}
					int currentNum = 0;
					for (String s : sharingResMap.keySet()) {
						Integer num = sharingResMap.get(s);
						currentNum += num;
						if (currentNum < pickUpNum) {
							unitedResults.get(quaId).remove(s);
							if (unitedResults.get(neighbor.getQualification()).containsKey(s)) {
								unitedResults.get(neighbor.getQualification()).put(s, unitedResults.get(neighbor.getQualification()).get(s) + num);
							}
							else {
								unitedResults.get(neighbor.getQualification()).put(s, num);
							}
							
						}
						else {
							unitedResults.get(quaId).put(s, unitedResults.get(quaId).get(s) - num + (currentNum - pickUpNum));
							if (unitedResults.get(neighbor.getQualification()).containsKey(s)) {
								unitedResults.get(neighbor.getQualification()).put(s,
										unitedResults.get(neighbor.getQualification()).get(s) + num - (currentNum - pickUpNum));
							}
							else {
								unitedResults.get(neighbor.getQualification()).put(s, num - (currentNum - pickUpNum));
							}
							break;
						}
					}
					
					break;
					
				}
				
			}
			
		}
		return changed;
		
	}
	
	/**
	 * Pick up several resources from a given list.
	 * 
	 * @param pickUpNum
	 * @param originalResList
	 * @param originalResMap
	 * @param pickUpResMap
	 * @return
	 */
	private long getSumCost(int pickUpNum, List<String> originalResList, Map<String, Integer> originalResMap, Map<String, Integer> pickUpResMap) {
	
		long sum = 0l;
		int currentNum = 0;
		for (String id : originalResList) {
			Integer num = originalResMap.get(id);
			currentNum += num;
			if (currentNum < pickUpNum) {
				sum += num * sortedCostList.get(sortedResList.indexOf(id));
				pickUpResMap.put(id, num);
				
			}
			else {
				sum += (num - (currentNum - pickUpNum)) * sortedCostList.get(sortedResList.indexOf(id));
				pickUpResMap.put(id, num - (currentNum - pickUpNum));
				break;
			}
		}
		return sum;
		
	}
	
	private List<String> sortResAscending(Map<String, Integer> map) {
	
		ArrayList<String> sortedResAsc = new ArrayList<String>();
		for (int i = 0; i < sortedResList.size(); i++) {
			if (map.keySet().contains(sortedResList.get(i))) {
				sortedResAsc.add(sortedResList.get(i));
			}
		}
		
		return sortedResAsc;
		
	}
	
	private List<String> sortResDescending(Map<String, Integer> map) {
	
		ArrayList<String> sortedResDes = new ArrayList<String>();
		for (int i = sortedResList.size() - 1; i >= 0; i--) {
			if (map.keySet().contains(sortedResList.get(i))) {
				sortedResDes.add(sortedResList.get(i));
			}
		}
		return sortedResDes;
		
	}
	
}
