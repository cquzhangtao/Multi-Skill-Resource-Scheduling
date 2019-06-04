package solver.or;


import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class LinearExample {
  static { System.loadLibrary("jniortools"); }
  private static MPSolver createSolver (String solverType) {
    return new MPSolver("LinearExample",
                          MPSolver.OptimizationProblemType.valueOf(solverType));
  }
  private static void runLinearExample(String solverType) {
    MPSolver solver = createSolver(solverType);
    double infinity = MPSolver.infinity();
    // x and y are continuous non-negative variables.
    MPVariable x = solver.makeNumVar(0.0, infinity, "x");
    MPVariable y = solver.makeNumVar(0.0, infinity, "y");
    // Maximize 3 * x + 4 * y.
    MPObjective objective = solver.objective();
    objective.setCoefficient(x, 3);
    objective.setCoefficient(y, 4);
    objective.setMaximization();
    // x + 2y <= 14.
    MPConstraint c0 = solver.makeConstraint(-infinity, 14.0);
    c0.setCoefficient(x, 1);
    c0.setCoefficient(y, 2);

    // 3x - y >= 0.
    MPConstraint c1 = solver.makeConstraint(0.0, infinity);
    c1.setCoefficient(x, 3);
    c1.setCoefficient(y, -1);

    // x - y <= 2.
    MPConstraint c2 = solver.makeConstraint(-infinity, 2.0);
    c2.setCoefficient(x, 1);
    c2.setCoefficient(y, -1);
    System.out.println("Number of variables = " + solver.numVariables());
    System.out.println("Number of constraints = " + solver.numConstraints());
    solver.solve();

     // The value of each variable in the solution.
    System.out.println("Solution");
    System.out.println("x = " + x.solutionValue());
    System.out.println("y = " + y.solutionValue());

    // The objective value of the solution.
    System.out.println("Optimal objective value = " + solver.objective().value());
  }

  public static void main(String[] args) throws Exception {
    runLinearExample("GLOP_LINEAR_PROGRAMMING");
  }
}