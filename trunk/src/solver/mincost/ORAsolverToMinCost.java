
package solver.mincost;

import java.util.ArrayList;
import java.util.List;


import problem.OverlappingResAllocProblem;
import problem.Resource;
import solver.maxactamount.ORASolverByReducingActivities;



/**
 * In the super class we didn't consider the cost of resources. In the results
 * the expensive resources maybe be allocated while the cheaper ones are still
 * free. So in order to save cost, we must deal with the results. The class is
 * only an abstract one which provides all data we need .
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public abstract class ORAsolverToMinCost extends ORASolverByReducingActivities {
	
	protected List<String> sortedResList = new ArrayList<String>();
	
	protected List<Double> sortedCostList = new ArrayList<Double>();
	
	protected List<Double> costTracking = new ArrayList<Double>();
	
	protected boolean tracking = true;
	
	public ORAsolverToMinCost(OverlappingResAllocProblem problem) {
	
		super(problem);
		sortResourceByCost();
	}
	
	/**
	 * we sort all resources by cost from the lowest to the highest.
	 * 
	 */
	private void sortResourceByCost() {
	
		double maxCost = 0l;
		
		for (Resource res : problem.getResMap().values()) {
			if (res.getDummy()) {
				continue;
			}
			double cost = 0l;
			
			cost = res.getCost();
			
			
			if (sortedResList.size() == 0) {
				sortedResList.add(res.getId());
				maxCost = cost;
				sortedCostList.add(cost);
				continue;
			}
			
			if (cost >= maxCost) {
				maxCost = cost;
				sortedResList.add(res.getId());
				sortedCostList.add(cost);
				continue;
			}
			for (int i = 0; i < sortedResList.size(); i++) {
				if (cost < sortedCostList.get(i)) {
					sortedCostList.add(i, cost);
					sortedResList.add(i, res.getId());
					break;
				}
			}
			
		}
		
	}
	
}
