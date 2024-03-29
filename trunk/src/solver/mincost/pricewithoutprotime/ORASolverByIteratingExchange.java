
package solver.mincost.pricewithoutprotime;

import model.Model;

/**
 * Call methods solveByReplaceOverlapping() and solveByExchangeResource() repeat
 * till no improvement of results we can get.
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class ORASolverByIteratingExchange extends ORASolverByReplacingSharingResources {
	
	public ORASolverByIteratingExchange(Model problem) {
	
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
		//solveByReplaceOverlapping();
		//solveByExchangeResource();
		int iterate=0;
		while (solveByReplaceOverlapping()) {
			track();
			solveByExchangeResource();
			track();
			iterate++;
			if(iterate>10) {
				break;
			}
		}
		//System.out.println("Objective = "+getTotalCost());
	}
	
}
