package solver.or;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolverParameters;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

import model.Activity;
import model.Model;
import model.Qualification;
import model.Resource;
import solver.AbstractORASlover;

public class ORASolverByCP extends AbstractORASlover {

	static {
		System.loadLibrary("jniortools");
	}
	
	
	
	public ORASolverByCP(Model problem) {
		super(problem);
		setNoObjective(false);
	}

	@Override
	protected boolean start(boolean tracking) {

		return solveByReducingActivity(tracking);
	}

	protected boolean solveWithAllActivities(boolean tracking) {
		 CpModel model = new CpModel();

		Map<String, IntVar> variables = new HashMap<String, IntVar>();
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
					variables.put(key, model.newIntVar(0, res.getAmount(), key));
				}
			}
		}

		for (Activity act : problem.getActivities()) {

			for (Qualification qua : problem.getQualifications().values()) {
				Integer needed = act.getMode().getQualificationAmountMap().get(qua.getId());
				if (needed == null) {
					continue;
				}

				List<IntVar> vars=new ArrayList<IntVar>();
				for (Resource res : problem.getResources().values()) {
					if (!problem.getQualificationResourceRelation().get(qua.getId()).contains(res.getId())) {
						continue;
					}
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					IntVar var = variables.get(key);
					vars.add(var);
				}
				
				model.addLinearConstraint(LinearExpr.sum(vars.toArray(new IntVar[0])), needed, needed);
			}

		}

		for (Resource res : problem.getResources().values()) {
			int have = res.getAmount();
			List<IntVar> vars=new ArrayList<IntVar>();
			for (Activity act : problem.getActivities()) {
				for (Qualification qua : problem.getQualifications().values()) {
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					IntVar var = variables.get(key);
					if (var == null) {
						continue;
					}
					vars.add(var);
				}
			}
			model.addLinearConstraint(LinearExpr.sum(vars.toArray(new IntVar[0])), 0, have);
			
		}

		// System.out.println("Number of constraints = " + solver.numConstraints());

		List<IntVar> vars=new ArrayList<IntVar>();
		List<Integer> costs=new ArrayList<Integer>();
		int scale=1000000;
		for (Activity act : problem.getActivities()) {
			for (Qualification qua : problem.getQualifications().values()) {
				for (Resource res : problem.getResources().values()) {
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					IntVar var = variables.get(key);
					if (var == null) {
						continue;
					}
					vars.add(var);
					costs.add( (int) (res.getCost()*scale));
					
				}
			}

		}
		
		int[] coefficients=new int[costs.size()];
		int idx=0;
		for(Integer cost:costs) {			
			coefficients[idx]=cost;
			idx++;
		}
		
		if(!isNoObjective()) {
			model.minimize(LinearExpr.scalProd(vars.toArray(new IntVar[0]),coefficients));
		}
		 CpSolver solver = new CpSolver();
		 solver.getParameters().setMaxTimeInSeconds(10);
		    VarArraySolutionPrinterWithObjective cb =
		        new VarArraySolutionPrinterWithObjective(variables.values().toArray(new IntVar[0]),isNoObjective()?1:100000);
		   CpSolverStatus status;
		    if(!isNoObjective()) {
		    	status=solver.searchAllSolutions(model, cb);
		    }else {
		    	status=solver.solveWithSolutionCallback(model, cb);
		    }
	
		
		   
		if (cb.getSolutionCount() ==0) {
			// System.err.println("The problem does not have an optimal solution!");
			return false;
		}
		  if(!isNoObjective()&&status==CpSolverStatus.OPTIMAL) {
			  setOptimal(true);
		  }

		// System.out.println("Solution:");
		//System.out.println("Objective value = " + solver.objectiveValue()/scale);
	
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
					IntVar var = variables.get(key);
					if (var == null) {
						continue;
					}
					long num=cb.value(var);
					if(num==0) {
						continue;
					}
					ress.put(res.getId(), (int) num);
					
					
					
					// System.out.println(act.getMode().getQualificationAmountMap().get(qua.getId())+",
					// "+key+" = " + var.solutionValue()+", "+res.getAmount());

				}
			}

		}

		return true;
	}

	protected boolean solveByReducingActivity(boolean tracking) {

		while (!solveWithAllActivities(tracking)) {

			//System.out.println("Activity number: "+problem.getActivities().size());
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
