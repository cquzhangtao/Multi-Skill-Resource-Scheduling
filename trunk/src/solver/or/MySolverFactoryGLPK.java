package solver.or;

import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactoryGLPK;
import net.sf.javailp.SolverGLPK;

public class MySolverFactoryGLPK extends SolverFactoryGLPK{
	@Override
	protected Solver getInternal() {
		return new MySolverGLPK();
	}
}
