package solver.or;

import model.Model;

public class ORASolverByMIPNonObjective extends ORASolverByMIPGLPK{

	public ORASolverByMIPNonObjective(Model problem) {
		super(problem);
		setNoObjective(true);
	}

}
