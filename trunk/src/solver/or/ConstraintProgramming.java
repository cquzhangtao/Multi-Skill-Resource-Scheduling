package solver.or;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

/** Solves an optimization problem and displays all intermediate solutions. */
public class ConstraintProgramming {
  static {
    System.loadLibrary("jniortools");
  }

  public static void main(String[] args) throws Exception {
    // Create the model.
    CpModel model = new CpModel();

    // Create the variables.
    int numVals = 3;

    IntVar x = model.newIntVar(0, numVals - 1, "x");
    IntVar y = model.newIntVar(0, numVals - 1, "y");
    IntVar z = model.newIntVar(0, numVals - 1, "z");

    // Create the constraint.
    //model.addDifferent(x, y);
    model.addAllDifferent(new IntVar[] {x,y});
    // Maximize a linear combination of variables.
   // model.maximize(LinearExpr.scalProd(new IntVar[] {x, y, z}, new int[] {1, 2, 3}));

    // Create a solver and solve the model.
    CpSolver solver = new CpSolver();
    VarArraySolutionPrinterWithObjective cb =
        new VarArraySolutionPrinterWithObjective(new IntVar[] {x, y, z});
    //solver.solveWithSolutionCallback(model, cb);
    solver.searchAllSolutions(model, cb);
   
    System.out.println(cb.getSolutionCount() + " solutions found.");
  }
  
  
}

class VarArraySolutionPrinterWithObjective extends CpSolverSolutionCallback {
	
    private int solutionCount;
    private final IntVar[] variableArray;
    
    public VarArraySolutionPrinterWithObjective(IntVar[] variables) {
      variableArray = variables;
    }

    @Override
    public void onSolutionCallback() {
     /* System.out.printf("Solution #%d: time = %.02f s%n", solutionCount, wallTime());
      System.out.printf("  objective value = %f%n", objectiveValue());
      for (IntVar v : variableArray) {
        System.out.printf("  %s = %d%n", v.getName(), value(v));
      }*/
      solutionCount++;
    }
    
//    public long value(IntVar v) {
//    	return super.value(v);
//    }

    public int getSolutionCount() {
      return solutionCount;
    }


  }