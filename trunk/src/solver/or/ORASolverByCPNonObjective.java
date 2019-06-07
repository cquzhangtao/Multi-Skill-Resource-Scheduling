package solver.or;
import model.Model;

public class ORASolverByCPNonObjective extends ORASolverByCP {

	public ORASolverByCPNonObjective(Model problem) {
		super(problem);
		setNoObjective(true);
	}

	

}
