package solver.or;

import java.util.HashMap;
import java.util.Map;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolverParameters;
import com.google.ortools.linearsolver.MPVariable;

import model.Activity;
import model.Model;
import model.Qualification;
import model.Resource;
import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.Operator;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryGLPK;
import net.sf.javailp.SolverFactoryGurobi;
import net.sf.javailp.SolverGLPK;
import net.sf.javailp.SolverGLPK.Hook;
import solver.AbstractORASlover;

public class ORASolverByMIPGLPK extends AbstractORASlover {

	public ORASolverByMIPGLPK(Model problem) {
		super(problem);
		setNoObjective(false);
	}

	@Override
	protected boolean start(boolean tracking) {

		return solveByReducingActivity(tracking);
	}

	protected boolean solveWithAllActivities(boolean tracking) {
		
		SolverFactory factory = new MySolverFactoryGLPK(); // use lp_solve
		//SolverFactory factory = new MySolverFactoryGurobi(); 
		factory.setParameter(Solver.VERBOSE, 0); 
		factory.setParameter(Solver.TIMEOUT, 10); // set timeout to 100 seconds

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
				
				mathProblem.add(act.getId() + "_" + qua.getId(),linear, Operator.EQ, needed);
				
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
			
			
			mathProblem.add(res.getId()+"_less",linear, Operator.LE, have);
			mathProblem.add(res.getId()+"_greater", linear,Operator.GE, 0);
		}
		
		
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
		
		if(!isNoObjective()) {
			mathProblem.setObjective(linear, OptType.MIN);
		}
		
		Solver solver =  factory.get(); // you should use this solver only once for one problem
		
		Result result = solver.solve(mathProblem);
		if(result==null) {
			return false;
		}
		
		if(!isNoObjective()&&result.getObjective().doubleValue()>0) {
			setOptimal(true);
		}
		
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
					if (!problem.getQualificationResourceRelation().get(qua.getId()).contains(res.getId())) {
						continue;
					}
					String key = act.getId() + "_" + qua.getId() + "_" + res.getId();
					
					int num=result.get(key).intValue();
					if(num==0) {
						continue;
					}
					ress.put(res.getId(), num);
					
					if(Math.abs(num-result.get(key).doubleValue())>0.000001) {
						System.err.println("Algorithm wrong! not integer number");
						
					}
					
					// System.out.println(act.getMode().getQualificationAmountMap().get(qua.getId())+",
					// "+key+" = " + var.solutionValue()+", "+res.getAmount());

				}
			}

		}
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
