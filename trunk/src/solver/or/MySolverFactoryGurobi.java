package solver.or;

import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactoryGLPK;
import net.sf.javailp.SolverFactoryGurobi;
import net.sf.javailp.SolverGLPK;

public class MySolverFactoryGurobi extends SolverFactoryGurobi{
	@Override
	protected Solver getInternal() {
		return new MySolverGurobi();
	}
}
