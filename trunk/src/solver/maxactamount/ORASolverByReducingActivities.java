
package solver.maxactamount;

import model.Activity;
import model.Model;
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
	
	public ORASolverByReducingActivities(Model problem) {
	
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
			if(problem.getActivities().size()==1) {
				//problem.print();
			}
			//System.out.println("Activity num "+problem.getActivities().size() );
			Activity lastAct = problem.getActivities().get(problem.getActivities().size() - 1);
			problem.getActivities().remove(lastAct);
			unitedResults.clear();
			results.clear();
			for (String resId : usedResNumMap.keySet()) {
				usedResNumMap.put(resId, 0);
			}
			if (problem.getActivities().size() == 0) {
				return false;
			}
		}
		//System.out.println("Started Activity num "+problem.getActivities().size() );
		return true;
		
	}
	
}
