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
import solver.AbstractORASlover;

public class ORASolverByMIP extends AbstractORASlover {

	public ORASolverByMIP(Model problem) {
		super(problem);
	}

	@Override
	protected boolean start(boolean tracking) {

		return solveByReducingActivity(tracking);
	}

	protected boolean solveWithAllActivities(boolean tracking) {
		
		MPSolver solver = new MPSolver("SimpleMipProgram", MPSolver.OptimizationProblemType.CLP_LINEAR_PROGRAMMING);
		//solver.setSolverSpecificParametersAsString(arg0)
		/*if(problem.getActivities().size()>62) {
			solver.setTimeLimit(1);
		}else {
			solver.setTimeLimit(10*1000);
		}*/
		// double infinity = java.lang.Double.POSITIVE_INFINITY;

		Map<String, MPVariable> variables = new HashMap<String, MPVariable>();
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
					variables.put(key, solver.makeIntVar(0.0, res.getAmount(), key));
					//variables.put(key, solver.makeNumVar(0.0, res.getAmount(), key));
				}
			}
		}

		for (Activity act : problem.getActivities()) {

			for (Qualification qua : problem.getQualifications().values()) {
				Integer needed = act.getMode().getQualificationAmountMap().get(qua.getId());
				if (needed == null) {
					continue;
				}

				MPConstraint constraint = solver.makeConstraint();
				constraint.setBounds(needed, needed);
				for (Resource res : problem.getResources().values()) {
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					MPVariable var = variables.get(key);
					if (var == null) {
						continue;
					}
					constraint.setCoefficient(var, 1);

				}
			}

		}

		for (Resource res : problem.getResources().values()) {
			int have = res.getAmount();
			MPConstraint constraint = solver.makeConstraint();
			constraint.setBounds(0, have);
			for (Activity act : problem.getActivities()) {
				for (Qualification qua : problem.getQualifications().values()) {
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					MPVariable var = variables.get(key);
					if (var == null) {
						continue;
					}
					constraint.setCoefficient(var, 1);
				}
			}
		}

		// System.out.println("Number of constraints = " + solver.numConstraints());

		MPObjective objective = solver.objective();

		for (Activity act : problem.getActivities()) {
			for (Qualification qua : problem.getQualifications().values()) {
				for (Resource res : problem.getResources().values()) {
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					MPVariable var = variables.get(key);
					if (var == null) {
						continue;
					}
					objective.setCoefficient(var, res.getCost());

				}
			}

		}
		objective.setMinimization();
	
		final MPSolver.ResultStatus resultStatus = solver.solve();
		// Check that the problem has an optimal solution.
		if (resultStatus != MPSolver.ResultStatus.FEASIBLE) {
			// System.err.println("The problem does not have an optimal solution!");
			return false;
		}
		// Verify that the solution satisfies all constraints (when using solvers
		// others than GLOP_LINEAR_PROGRAMMING, this is highly recommended!).
		if (!solver.verifySolution(/* tolerance= */1e-7, /* log_errors= */true)) {
			System.err.println(
					"The solution returned by the solver violated the" + " problem constraints by at least 1e-7");
			return false;
		}

		// System.out.println("Solution:");
		System.out.println("Objective value = " + objective.value());

		Map<Resource, Integer> count = new HashMap<Resource, Integer>();
		for (Activity act : problem.getActivities()) {
			Map<String, Map<String, Integer>> quas = new HashMap<String, Map<String, Integer>>();
			results.put(act.getId(), quas);
			for (Qualification qua : problem.getQualifications().values()) {
				if (!act.getMode().getQualificationAmountMap().containsKey(qua.getId())) {
					continue;
				}
				Map<String, Integer> ress = new HashMap<String, Integer>();
				quas.put(qua.getId(), ress);
				for (Resource res : problem.getResources().values()) {
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					MPVariable var = variables.get(key);
					if (var == null) {
						continue;
					}
					int num=(int) var.solutionValue();
					ress.put(res.getId(), num);
					
					if(Math.abs(num-var.solutionValue())>0.000001) {
						System.err.println("Algorithm wrong! not integer number");
						
					}
					
					// System.out.println(act.getMode().getQualificationAmountMap().get(qua.getId())+",
					// "+key+" = " + var.solutionValue()+", "+res.getAmount());

				}
			}

		}
		// System.out.println("x = " + x.solutionValue());
		// System.out.println("y = " + y.solutionValue());

		
//		  System.out.println("\nAdvanced usage:");
//		  System.out.println("Problem solved in " + solver.wallTime() +
//		  " milliseconds"); System.out.println("Problem solved in " +
//		  solver.iterations() + " iterations"); System.out.println("Problem solved in "
//		  + solver.nodes() + " branch-and-bound nodes");
//		 

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
		System.out.println("Started Activity num " + problem.getActivities().size());
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
