package solver.or;

// Copyright 2010-2018 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// [START program]
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;

/** Minimal CP-SAT example to showcase calling the solver. */
public class SimpleSatProgram {
  static {
    System.loadLibrary("jniortools");
  }

  public static void main(String[] args) throws Exception {
    // Create the model.
    // [START model]
    CpModel model = new CpModel();
    
    // [END model]

    // Create the variables.
    // [START variables]
    int numVals = 3;

    IntVar x = model.newIntVar(0, numVals - 1, "x");
    IntVar y = model.newIntVar(0, numVals - 1, "y");
    IntVar z = model.newIntVar(0, numVals - 1, "z");
   // System.out.println( model.validate());
    // [END variables]

    // Create the constraints.
    // [START constraints]
   // model.addDifferent(x, y);
    //model.addEquality(x, y);
    model.addAllDifferent(new IntVar[] {x,y});
    // [END constraints]
    System.out.println( model.validate());
    // Create a solver and solve the model.
    // [START solve]
    CpSolver solver = new CpSolver();
    
    CpSolverStatus status = solver.solve(model);
    // [END solve]

    if (status == CpSolverStatus.FEASIBLE) {
      System.out.println("x = " + solver.value(x));
      System.out.println("y = " + solver.value(y));
      System.out.println("z = " + solver.value(z));
    }
  }
}
// [END program]
