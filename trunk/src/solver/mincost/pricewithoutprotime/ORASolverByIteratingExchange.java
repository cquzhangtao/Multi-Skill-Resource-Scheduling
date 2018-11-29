
package solver.mincost.pricewithoutprotime;

import problem.OverlappingResAllocProblem;

/**
 * Call methods solveByReplaceOverlapping() and solveByExchangeResource() repeat
 * till no improvement of results we can get.
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class ORASolverByIteratingExchange extends ORASolverByReplacingSharingResources {
	
	public ORASolverByIteratingExchange(OverlappingResAllocProblem problem) {
	
		super(problem);
		
	}
	
	@Override
	protected boolean start(boolean tracking) {
	
		this.tracking = tracking;
		if (!solveByReducingActivity()) {
			return false;
		}
		track();
		solveByIterater();
		return true;
	}
	
	private void solveByIterater() {
	
		solveByExchangeResource();
		track();
		while (solveByReplaceOverlapping()) {
			track();
			solveByExchangeResource();
			track();
		}
	}
	
}
