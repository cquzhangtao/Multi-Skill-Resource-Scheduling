
package solver;

import java.util.HashMap;
import java.util.Map;

import com.google.ortools.linearsolver.MPVariable;

import model.Activity;
import model.Model;
import model.Qualification;
import model.Resource;
import model.ResourcesForOneActivity;



/**
 * This is an abstract solver for the overlapping resource allocation (ORA)
 * problem. The class provides three types of solve() methods with different
 * parameters to control if we track the procedures and print the results out.
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public abstract class AbstractORASlover {
	
	/**
	 * This is the problem we will solve. The problem includes all information
	 * needed by the solver.
	 */
	protected Model problem;
	
	
	private Model originalProblem;
	
	/**
	 * The total available resources number before the solver starts. This
	 * variable will not change during the solving procedures. The Key is the
	 * resource ID and the value is the amount.
	 */
	protected Map<String, Integer> totalResNumMap = new HashMap<String, Integer>();
	
	/**
	 * The allocated resource number from the totalResNumMap. It is changed
	 * during every procedures. It is the most important field in all
	 * procedures. please do not try to remove it. if we try to resolve the
	 * problem, please reset it. The Key is the resource ID and the value is the
	 * amount.
	 */
	protected Map<String, Integer> usedResNumMap = new HashMap<String, Integer>();
	
	/**
	 * The final results are here. The key is the activity ID.
	 */
	protected Map<String, Map<String, Map<String, Integer>>> results = new HashMap<String, Map<String, Map<String, Integer>>>();
	
	private boolean optimal=false;
	
	private boolean noObjective=true;
	
	private long time=0;
	
	public boolean isNoObjective() {
		return noObjective;
	}

	public void setNoObjective(boolean noObjective) {
		this.noObjective = noObjective;
	} 
	
	/**
	 * Constructor. we pass the problem to the solver and initialize the fields:
	 * totalResNumMap and usedResNumMap. Now the available resource amounts are
	 * the maximal amounts of the resources.
	 * 
	 * @param problem
	 */
	public AbstractORASlover(Model problem) {
		setOriginalProblem(problem);
		this.problem = problem.clone();
		for (Resource res : problem.getResources().values()) {
			if (res.getDummy()) {
				continue;
			}
			totalResNumMap.put(res.getId(), problem.getResources().get(res.getId()).getAvailableAmount());
			usedResNumMap.put(res.getId(), 0);
		}
	}
	
	/**
	 * solve() without parameters. The tracking and printing both are disabled .
	 */
	public void solve() {
	
		solve(false, false);
	}
	
	/**
	 * 
	 * @param tracking
	 *            which indicates if we enable the tracking in procedures.
	 */
	public void solve(boolean tracking) {
	
		solve(false, tracking);
	}
	
	/**
	 * This is an abstract function and will be implemented according to
	 * different procedures. Call the implemented procedure to solve the problem
	 * 
	 * @param tracking
	 *            indicates if the tracking is enable
	 * @return if get the solution, return true; otherwise return false.
	 */
	protected abstract boolean start(boolean tracking);
	
	/**
	 * Get the final results
	 */
	protected abstract void generateResults();
	
	/**
	 * A common entry to the main procedure
	 * 
	 * @param print
	 *            indicates if the results will be printed out.
	 * @param tracking
	 */
	public void solve(boolean print, boolean tracking) {
	
		long start=System.currentTimeMillis();
		if (start(tracking)) {
			generateResults();
			validateResults();
			if (print) {
				print();
			}
		}
		else {
			System.out.println("No feasible solution found");
		}
		time=System.currentTimeMillis()-start;
	}
	
	protected abstract void printTrackResults();
	
	protected abstract void printTempData();
	
	/**
	 * Print out all information including the problem, the temporary data and
	 * the final results.
	 */
	public void print() {
	
		//problem.print();
		//printTempData();
		//printFinalResults();
		//printTrackResults();
		printFinal();
		
	}
	
	/**
	 * Print the final results out. Just for debugging and experiments.
	 */
	protected void printFinalResults() {
		System.out.println("=========================================");
		System.out.println("Results in detail");
		double cost=0;
		for (String act : results.keySet()) {
			System.out.println("----------------------------------------------------");
			System.out.println("Activity:" + act);
			for (String qua : results.get(act).keySet()) {
				System.out.println("        Qualificaiton:" + qua+", "+problem.getActivityMap().get(act).getMode().getQualificationAmountMap().get(qua));
				for (String res : results.get(act).get(qua).keySet()) {
					int num=results.get(act).get(qua).get(res);
					Resource reso = problem.getResources().get(res);
					cost+=num*reso.getCost();
					System.out.println("                        Resource:" + res+"("+reso.getAmount()+"), Amount: "+num);
					
				}
			}
			
		}
		System.out.println("----------------------------------------------------");
		System.out.println("Started activities: "+problem.getActivities().size()+", Objective "+cost);
	}
	
	protected void printFinal() {
		//System.out.println("=========================================");
		//System.out.println("Results in detail");
		double cost=0;
		for (String act : results.keySet()) {
			//System.out.println("----------------------------------------------------");
			//System.out.println("Activity:" + act);
			for (String qua : results.get(act).keySet()) {
				//System.out.println("        Qualificaiton:" + qua+", "+problem.getActivityMap().get(act).getMode().getQualificationAmountMap().get(qua));
				for (String res : results.get(act).get(qua).keySet()) {
					int num=results.get(act).get(qua).get(res);
					Resource reso = problem.getResources().get(res);
					cost+=num*reso.getCost();
					//System.out.println("                        Resource:" + res+"("+reso.getAmount()+"), Amount: "+num);
					
				}
			}
			
		}
		//System.out.println("----------------------------------------------------");
		//System.out.println(originalProblem.getActivities().size()+","+originalProblem.getSkillLevel()+","+this.getClass().getSimpleName()+", Started activities: "+problem.getActivities().size()+", Objective "+cost);
		System.out.println(originalProblem.getActivities().size()+","+originalProblem.getSkillLevel()+","+problem.getActivities().size()+", "+cost+ (noObjective?"":(", "+ optimal))+", "+time);

	}
	
	/**
	 * Please use this function to obtain the final results and go on further
	 * works.
	 * 
	 * @return the solution of the problem.
	 */
	public Map<String, Map<String, Map<String, Integer>>> getResults() {
	
		return results;
	}
	
	
	public void validateResults() {
		Map<Resource,Integer> count=new HashMap<Resource,Integer>();
		for(Activity act:problem.getActivities()) {			
			for(Qualification qua:problem.getQualifications().values()) {
				if(!act.getMode().getQualificationAmountMap().containsKey(qua.getId())) {
					continue;
				}
				int sum=0;
				for(Resource res:problem.getResources().values()) {
					if(!problem.getQualificationResourceRelation().get(qua.getId()).contains(res.getId())) {
						continue;
					}
					Integer num=results.get(act.getId()).get(qua.getId()).get(res.getId());
					if(num==null) {
						continue;
					}
					sum+=num;
					//System.out.println(act.getMode().getQualificationAmountMap().get(qua.getId())+", "+key+" = " + var.solutionValue()+", "+res.getAmount());
					
					if(!count.containsKey(res)) {
						count.put(res, 0);
					}
					int add=(int) (count.get(res)+num);
					if(add>res.getAmount()) {
						System.err.println(this.getClass().getSimpleName()+": Algorithm wrong R");
						//System.exit(0);
					}
					count.put(res, add);

				}
				int need = act.getMode().getQualificationAmountMap().get(qua.getId());
				if(sum!=need) {
					System.err.println(this.getClass().getSimpleName()+": Algorithm wrong Q, "+sum+","+need);
					//System.exit(0);
				}
			}
			
		}
	}

	public Model getOriginalProblem() {
		return originalProblem;
	}

	public void setOriginalProblem(Model originalProblem) {
		this.originalProblem = originalProblem;
	}

	public boolean isOptimal() {
		return optimal;
	}

	public void setOptimal(boolean optimal) {
		this.optimal = optimal;
	}

	
	
}
