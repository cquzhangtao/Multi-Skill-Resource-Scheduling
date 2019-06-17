
package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Activity;



/**
 * The elements make up the graph. An element represents a relationship between
 * a qualification and resources. The overlapping resources are stored in the
 * field sharedResources. We can use the qualification Id to distinguish the
 * element.
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class GraphElement {
	
	private String quaId;
	
	private int requiredResNum;
	
	private int currentTotalAvailableResNum;
	
	private int totalResNum;
	
	private Map<String, Integer> resNumMap;
	
	private Map<String, Integer> usedResNumMap;
	
	private Map<GraphElement, List<String>> sharedResources = new HashMap<GraphElement, List<String>>();
	
	private List<String> exclusiveResources = new ArrayList<String>();
	
	private boolean assignedResource = false;
	
	private final Map<String, List<String>> quaResRelationMap;
	
	private List<GraphElement> handledRequiredNeighborQuaList = new ArrayList<GraphElement>();
	
	private Map<String, Map<String, Integer>> unitedResults;
	
	public GraphElement(String qualification, List<Activity> activityList, Map<String, List<String>> quaResRelationMap, Map<String, Integer> resNumMap,
			Map<String, Integer> usedResNumMap, Map<String, Map<String, Integer>> unitedResults) {
	
		this.quaId = qualification;
		this.quaResRelationMap = quaResRelationMap;
		this.resNumMap = resNumMap;
		this.usedResNumMap = usedResNumMap;
		setRequiredResourcesNum(activityList);
		setCurrentTotalAvailableResNum();
		totalResNum = currentTotalAvailableResNum;
		this.unitedResults=unitedResults;
	}
	
	/**
	 * Once we want to resolve the problem, this function must be called
	 */
	public void reset() {
	
		assignedResource = false;
		handledRequiredNeighborQuaList.clear();
		if (requiredResNum == 0) {
			assignedResource = true;
		}
		setCurrentTotalAvailableResNum();
		
	}
	
	/**
	 * We combine all activities together. This function are used to calculate
	 * the amount of resources required by the qualification.
	 * 
	 * @param All
	 *            activities in the problem
	 */
	private void setRequiredResourcesNum(List<Activity> actList) {
	
		requiredResNum = 0;
		for (Activity act : actList) {
			Integer num = act.getMode().getQualificationAmountMap().get(quaId);
			if (num == null) {
				num = 0;
			}
			requiredResNum += num;
		}
		if (requiredResNum == 0) {
			assignedResource = true;
		}
	}
	
	/**
	 * When we have assigned resources to one qualification, we use this
	 * function to update the fields sharedResources and exclusiveResources
	 * 
	 * @param All
	 *            graphic elements in a list
	 */
	public void updateGraphElementRelation(Map<String, GraphElement> graphElements) {
	
		sharedResources.clear();
		exclusiveResources.clear();
		Set<String> addSum = new HashSet<String>();
		List<String> curResourceList = quaResRelationMap.get(quaId);
		for (GraphElement qua : graphElements.values()) {
			if (qua.isAssignedResource()) {
				continue;
			}
			if (qua != this) {
				for (String res : quaResRelationMap.get(qua.getQualification())) {
					addSum.add(res);
				}
				List<String> compResourceList = quaResRelationMap.get(qua.getQualification());
				List<String> fin = new ArrayList<String>(curResourceList);
				fin.retainAll(compResourceList);
				if (fin.size() > 0) {
					sharedResources.put(qua, fin);
				}
			}
		}
		for (String s : curResourceList) {
			if (!addSum.contains(s)) {
				exclusiveResources.add(s);
			}
		}
	}
	
	/**
	 * Assign resources to the qualification
	 * 
	 * @return Resource allocation in a map. The key is the resource ID and the
	 *         value is the amount of allocated resource.
	 */
	public Map<String, Integer> assignResource() {
	
		int sum = 0;
		Map<String, Integer> allocation = new HashMap<String, Integer>();
		for (String res : exclusiveResources) {
			sum += getResAvailableNum(res);
			if (sum <= requiredResNum) {
				allocation.put(res, getResAvailableNum(res));
				setResUsedNum(res, getResUsedNum(res) + getResAvailableNum(res));
			}
			else {
				int usedNum = getResAvailableNum(res) - (sum - requiredResNum);
				allocation.put(res, usedNum);
				setResUsedNum(res, getResUsedNum(res) + usedNum);
				break;
			}
		}
		if (sum >= requiredResNum) {
			this.setAssignedResource(true);
			return allocation;
		}
		List<String> resourcesList = getOverlapResources();
		while (resourcesList != null) {
			for (String res : resourcesList) {
				sum += getResAvailableNum(res);
				if (sum <= requiredResNum) {
					allocation.put(res, getResAvailableNum(res));
					setResUsedNum(res, getResUsedNum(res) + getResAvailableNum(res));
				}
				else {
					int usedNum = getResAvailableNum(res) - (sum - requiredResNum);
					allocation.put(res, usedNum);
					setResUsedNum(res, getResUsedNum(res) + usedNum);
					break;
				}
			}
			if (sum >= requiredResNum) {
				this.setAssignedResource(true);
				return allocation;
			}
			resourcesList = getOverlapResources();
		}
		
		handledRequiredNeighborQuaList.clear();
		resourcesList = getOverlapResourcesNonExclusive();
		while (resourcesList != null) {
			for (String res : resourcesList) {
				sum += getResAvailableNum(res);
				if (sum <= requiredResNum) {
					if(allocation.containsKey(res)) {
						allocation.put(res,allocation.get(res) +getResAvailableNum(res));
					}else {
					allocation.put(res, getResAvailableNum(res));
					}
					setResUsedNum(res, getResUsedNum(res) + getResAvailableNum(res));
				}
				else {
					int usedNum = getResAvailableNum(res) - (sum - requiredResNum);
					if(allocation.containsKey(res)) {
						allocation.put(res,allocation.get(res) +usedNum);
					}else {
						allocation.put(res, usedNum);
					}
					setResUsedNum(res, getResUsedNum(res) + usedNum);
					break;
				}
			}
			if (sum >= requiredResNum) {
				this.setAssignedResource(true);
				return allocation;
			}
			resourcesList = getOverlapResourcesNonExclusive();
		}
		
		
	/*	for (GraphElement rq : sharedResources.keySet()) {
			//if(!rq.isAssignedResource()) {
			//	continue;
			//}
			List<String>resAssignedToRQFromOlverlappingArea=new ArrayList<String>();
			int num=0;
			for(String res:sharedResources.get(rq)) {
				if(unitedResults.get(rq.getQualification()).containsKey(res)) {
					resAssignedToRQFromOlverlappingArea.add(res);
					num+=unitedResults.get(rq.getQualification()).get(res);
				}
			}
			
			for(GraphElement rq1:rq.sharedResources.keySet()) {
				if(!rq1.isAssignedResource()) {
					continue;
				}
				for(String res:rq1.sharedResources.get(rq)) {
					int avgNum = getResAvailableNum(res);
					if(avgNum==0) {
						continue;
					}
					sum += avgNum;
					if (sum <= requiredResNum) {
						if(allocation.containsKey(res)) {
							allocation.put(res,allocation.get(res) +getResAvailableNum(res));
						}else {
						allocation.put(res, getResAvailableNum(res));
						}
						setResUsedNum(res, getResUsedNum(res) + getResAvailableNum(res));
					}
					else {
						int usedNum = getResAvailableNum(res) - (sum - requiredResNum);
						if(allocation.containsKey(res)) {
							allocation.put(res,allocation.get(res) +usedNum);
						}else {
							allocation.put(res, usedNum);
						}
						setResUsedNum(res, getResUsedNum(res) + usedNum);
						break;
					}
				}
				if (sum >= requiredResNum) {
					this.setAssignedResource(true);
					return allocation;
				}
				
			}
		}*/
		
		
		return null;
		
	}
	
	
	private List<String> getOverlapResources() {
		return getOverlapResources(true);
	}
	private List<String> getOverlapResourcesNonExclusive() {
		return getOverlapResources(false);
	}
	/**
	 * get the resources in the sharing area between the qualification and its
	 * neighbor with the lowest urgent ratio
	 * 
	 * @return Overlapping resources in a list
	 */
	private List<String> getOverlapResources(boolean exclusive) {
	
		double min = Double.MAX_VALUE;
		GraphElement minRQ = null;
		for (GraphElement rq : sharedResources.keySet()) {
			if(!rq.isAssignedResource()) {
				continue;
			}
			if (!handledRequiredNeighborQuaList.contains(rq)) {
				rq.updateRatio();
				double ratio = rq.getUrgentRatio();
				if (min > ratio) {
					min = ratio;
					minRQ = rq;
				}
			}
		}
		if (minRQ == null) {
			for (GraphElement rq : sharedResources.keySet()) {
				if(rq.isAssignedResource()) {
					continue;
				}
				if (!handledRequiredNeighborQuaList.contains(rq)) {
					rq.updateRatio();
					double ratio = rq.getUrgentRatio();
					if (min > ratio) {
						min = ratio;
						minRQ = rq;
					}
				}
			}
		}
		if (minRQ == null) {
			return null;
		}
		handledRequiredNeighborQuaList.add(minRQ);
	
		List<String> sum = new ArrayList<String>(sharedResources.get(minRQ));
		
		if(exclusive) {
			List<String> othersSum = new ArrayList<String>();
			for (GraphElement rq : sharedResources.keySet()) {
				//if (!handledRequiredNeighborQuaList.contains(rq)) {
				if(rq!=minRQ) {
					othersSum.addAll(sharedResources.get(rq));
				}
			}
			sum.removeAll(othersSum);
		}
		
	
		return sum;
	}
	

	
	private void setCurrentTotalAvailableResNum() {
	
		currentTotalAvailableResNum = 0;
		for (String res : quaResRelationMap.get(quaId)) {
			currentTotalAvailableResNum += getResAvailableNum(res);
		}
	}
	
	private int getResAvailableNum(String res) {
	
		return resNumMap.get(res) - usedResNumMap.get(res);
		
	}
	
	private int getResUsedNum(String res) {
	
		return usedResNumMap.get(res);
	}
	
	private void setResUsedNum(String res, int usedNum) {
	
		usedResNumMap.put(res, usedNum);
	}
	
	public String getQualification() {
	
		return quaId;
	}
	
	public boolean isAssignedResource() {
	
		return assignedResource;
	}
	
	private void setAssignedResource(boolean assignedResource) {
	
		this.assignedResource = assignedResource;
	}
	
	public double getUrgentRatio() {
	
		return 1.0 * requiredResNum / currentTotalAvailableResNum;
	}
	
	public int getRequiredResNum() {
	
		return requiredResNum;
	}
	
	public int getCurrentTotalAvailableResNum() {
	
		return currentTotalAvailableResNum;
	}
	
	public Map<GraphElement, List<String>> getSharedResources() {
	
		return sharedResources;
	}
	
	public void updateRatio() {
	
		setCurrentTotalAvailableResNum();
	}
	
	public int getTotalResNum() {
	
		return totalResNum;
	}
	
}
