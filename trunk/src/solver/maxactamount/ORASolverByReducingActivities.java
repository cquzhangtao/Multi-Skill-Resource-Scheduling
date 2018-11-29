
package solver.maxactamount;

import problem.Activity;
import problem.OverlappingResAllocProblem;
import solver.ORASolverWithAllActivities;

/**
 * In practice, if the activities can not start at a time, we usually pick up
 * and carry out the activities with higher priorities first. This solver are
 * used to do such thing. The goal is to try start activities at a time as many
 * as possible.
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class ORASolverByReducingActivities extends ORASolverWithAllActivities {
	
	public ORASolverByReducingActivities(OverlappingResAllocProblem problem) {
	
		super(problem);
		
	}
	
	@Override
	protected boolean start(boolean tracking) {
	
		return solveByReducingActivity();
	}
	
	/**
	 * If the resources are not enough for all activities to start at the same
	 * time, we remove one activity with the lowest priority from the activity
	 * list and solve the new problem again.
	 */
	protected boolean solveByReducingActivity() {
	
		while (!solveWithAllActivities()) {
			Activity lastAct = problem.getActList().get(problem.getActList().size() - 1);
			problem.getActList().remove(lastAct);
			unitedResults.clear();
			results.clear();
			for (String resId : usedResNumMap.keySet()) {
				usedResNumMap.put(resId, 0);
			}
			if (problem.getActList().size() == 0) {
				return false;
			}
		}
		return true;
		
	}
	
}
