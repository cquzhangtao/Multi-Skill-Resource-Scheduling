package solver.or;

import java.util.HashMap;
import java.util.Map;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolverParameters;
import com.google.ortools.linearsolver.MPVariable;

import model.Activity;
import model.Model;
import model.Qualification;
import model.Resource;
import net.sf.javailp.Linear;
import net.sf.javailp.Operator;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryGLPK;
import solver.AbstractORASlover;

public class ORASolverByMIPGLPK extends AbstractORASlover {

	public ORASolverByMIPGLPK(Model problem) {
		super(problem);
	}

	@Override
	protected boolean start(boolean tracking) {

		return solveByReducingActivity(tracking);
	}

	protected boolean solveWithAllActivities(boolean tracking) {
		
		SolverFactory factory = new SolverFactoryGLPK(); // use lp_solve
		factory.setParameter(Solver.VERBOSE, 0); 
		factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds

		Problem mathProblem = new Problem();

		for (Activity act : problem.getActivities()) {
			for (Qualification qua : problem.getQualifications().values()) {
				if (!act.getMode().getQualificationAmountMap().containsKey(qua.getId())) {
					continue;
				}
				for (Resource res : problem.getResources().values()) {
					if (!problem.getQualificationResourceRelation().get(qua.getId()).contains(res.getId())) {
						continue;
					}
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					//variables.put(key, solver.makeIntVar(0.0, res.getAmount(), key));
					//variables.put(key, solver.makeNumVar(0.0, res.getAmount(), key));
					mathProblem.setVarType(key, Integer.class);
					mathProblem.setVarLowerBound(key, 0.0);
					mathProblem.setVarUpperBound(key, res.getAmount());
				}
			}
		}

		for (Activity act : problem.getActivities()) {

			for (Qualification qua : problem.getQualifications().values()) {
				Integer needed = act.getMode().getQualificationAmountMap().get(qua.getId());
				if (needed == null) {
					continue;
				}

				//MPConstraint constraint = solver.makeConstraint();
				//constraint.setBounds(needed, needed);
				Linear linear = new Linear();
				for (Resource res : problem.getResources().values()) {
					if (!problem.getQualificationResourceRelation().get(qua.getId()).contains(res.getId())) {
						continue;
					}
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					//MPVariable var = variables.get(key);
					//if (var == null) {
					//	continue;
					//}
					//constraint.setCoefficient(var, 1);
					linear.add(1, key);

				}
				
				mathProblem.add(linear, Operator.EQ, needed);
				
			}

		}

		for (Resource res : problem.getResources().values()) {
			int have = res.getAmount();
			Linear linear = new Linear();
			//MPConstraint constraint = solver.makeConstraint();
			//constraint.setBounds(0, have);
			for (Activity act : problem.getActivities()) {
				for (Qualification qua : problem.getQualifications().values()) {
					if (!act.getMode().getQualificationAmountMap().containsKey(qua.getId())) {
						continue;
					}
					if (!problem.getQualificationResourceRelation().get(qua.getId()).contains(res.getId())) {
						continue;
					}
					
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					
					//constraint.setCoefficient(var, 1);
					linear.add(1, key);
				}
			}
			
			mathProblem.add(linear, Operator.LE, have);
			mathProblem.add(linear, Operator.GE, 0);
		}

		// System.out.println("Number of constraints = " + solver.numConstraints());

		//MPObjective objective = solver.objective();
		
		
		Linear linear=new Linear();

		for (Activity act : problem.getActivities()) {
			for (Qualification qua : problem.getQualifications().values()) {
				if (!act.getMode().getQualificationAmountMap().containsKey(qua.getId())) {
					continue;
				}
				for (Resource res : problem.getResources().values()) {
					if (!problem.getQualificationResourceRelation().get(qua.getId()).contains(res.getId())) {
						continue;
					}
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					linear.add(res.getCost(), key);

				}
			}

		}
		
		mathProblem.setObjective(linear, OptType.MIN);
		
		Solver solver = factory.get(); // you should use this solver only once for one problem
		Result result = solver.solve(mathProblem);
		result.get("a");
		
//		result.
//
//		final MPSolver.ResultStatus resultStatus = solver.solve();
//		// Check that the problem has an optimal solution.
//		if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
//			// System.err.println("The problem does not have an optimal solution!");
//			return false;
//		}
//		// Verify that the solution satisfies all constraints (when using solvers
//		// others than GLOP_LINEAR_PROGRAMMING, this is highly recommended!).
//		if (!solver.verifySolution(/* tolerance= */1e-7, /* log_errors= */true)) {
//			System.err.println(
//					"The solution returned by the solver violated the" + " problem constraints by at least 1e-7");
//			return false;
//		}
//
//		// System.out.println("Solution:");
//		//System.out.println("Objective value = " + objective.value());
//
//		Map<Resource, Integer> count = new HashMap<Resource, Integer>();
//		for (Activity act : problem.getActivities()) {
//			Map<String, Map<String, Integer>> quas = new HashMap<String, Map<String, Integer>>();
//			results.put(act.getId(), quas);
//			for (Qualification qua : problem.getQualifications().values()) {
//				if (!act.getMode().getQualificationAmountMap().containsKey(qua.getId())) {
//					continue;
//				}
//				Map<String, Integer> ress = new HashMap<String, Integer>();
//				quas.put(qua.getId(), ress);
//				for (Resource res : problem.getResources().values()) {
//					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
//					MPVariable var = variables.get(key);
//					if (var == null) {
//						continue;
//					}
//					int num=(int) var.solutionValue();
//					if(num==0) {
//						continue;
//					}
//					ress.put(res.getId(), num);
//					
//					if(Math.abs(num-var.solutionValue())>0.000001) {
//						System.err.println("Algorithm wrong! not integer number");
//						
//					}
//					
//					// System.out.println(act.getMode().getQualificationAmountMap().get(qua.getId())+",
//					// "+key+" = " + var.solutionValue()+", "+res.getAmount());
//
//				}
//			}
//
//		}
//		// System.out.println("x = " + x.solutionValue());
//		// System.out.println("y = " + y.solutionValue());
//
//		
////		  System.out.println("\nAdvanced usage:");
////		  System.out.println("Problem solved in " + solver.wallTime() +
////		  " milliseconds"); System.out.println("Problem solved in " +
////		  solver.iterations() + " iterations"); System.out.println("Problem solved in "
////		  + solver.nodes() + " branch-and-bound nodes");
////		 

		return true;
	}

	protected boolean solveByReducingActivity(boolean tracking) {

		while (!solveWithAllActivities(tracking)) {

			System.out.println("Activity number: "+problem.getActivities().size());
			Activity lastAct = problem.getActivities().get(problem.getActivities().size() - 1);
			problem.getActivities().remove(lastAct);
			// unitedResults.clear();
			results.clear();
			for (String resId : usedResNumMap.keySet()) {
				usedResNumMap.put(resId, 0);
			}
			if (problem.getActivities().size() == 0) {
				return false;
			}
		}
		//System.out.println("Started Activity num " + problem.getActivities().size());
		return true;

	}

	@Override
	protected void generateResults() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void printTrackResults() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void printTempData() {
		// TODO Auto-generated method stub

	}

}
