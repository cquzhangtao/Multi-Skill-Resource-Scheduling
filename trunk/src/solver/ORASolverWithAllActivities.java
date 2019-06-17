
package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.GraphElement;
import core.GraphicSpace;
import model.Activity;
import model.Model;
import model.Qualification;
import model.Resource;
import model.ResourcesForOneActivity;



/**
 * This solver are used to solve the ORA problem directly. It unites all
 * activities together according to the qualification first and then use an
 * overlapping diagram to solve the problem. At last the allocated resources are
 * assigned to each activity. if the activities can not start at a time, the
 * procedure will end. and no solution is found.
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class ORASolverWithAllActivities extends AbstractORASlover {
	
	/**
	 * The overlapping diagram of resources.
	 */
	protected GraphicSpace graphicSpace = new GraphicSpace();
	
	/**
	 * An united results we get from the overlapping diagram
	 */
	protected Map<String, Map<String, Integer>> unitedResults = new HashMap<String, Map<String, Integer>>();
	
	
	protected Map<String, List<Activity>> combinedQuaActRelation = new HashMap<String, List<Activity>>();
	
	

	
	
	/**
	 * Constructor
	 * 
	 * @param problem
	 */
	public ORASolverWithAllActivities(Model problem) {
	
		super(problem);
	}
	
	@Override
	protected boolean start(boolean tracking) {
	
		return solveWithAllActivities();
	}
	
	/**
	 * Solve the problem without the cost factor
	 * 
	 * @return if activities in the problem can be started at the same time,
	 *         return true. Otherwise, return false.
	 */
	protected boolean solveWithAllActivities() {
	
		if (!uniteQuaNumInActivies()) {
			
			return false;
		}
		for (int finishedGraphicElementNum = 0; finishedGraphicElementNum < graphicSpace.size(); finishedGraphicElementNum++) {
			graphicSpace.update();
			
			GraphElement rq = graphicSpace.getMostUrgentGraphElement();
			
			//GraphElement rq=graphicSpace.getRandomGraphElement();
			
			
			if (rq == null) {
				continue;
			}
			Map<String, Integer> allocation = rq.assignResource();
			if (allocation == null) {
				return false;
			}
			unitedResults.put(rq.getQualification(), allocation);
		}
		
		return true;
	}
	
	/**
	 * We combine all activities together according to the qualification. For
	 * each qualification we give a total required amount of resources.
	 */
	private boolean uniteQuaNumInActivies() {
	
		graphicSpace.clear();
		
		for (String qua : problem.getQualificationResourceRelation().keySet()) {
			if (qua.equals("DUMMY")) {
				continue;
			}
			GraphElement element = new GraphElement(qua, problem.getActivities(), problem.getQualificationResourceRelation(), totalResNumMap, usedResNumMap,unitedResults);
			if (element.getRequiredResNum() > element.getCurrentTotalAvailableResNum()) {
				return false;
			}
			graphicSpace.put(qua, element);
		}
		return true;
	}
	
	@Override
	protected void printTrackResults() {
	
	}
	
	/**
	 * Relation between Qualifications and activities
	 * 
	 * @return a map where the key is the qualification id and the value is a
	 *         list of activities which has the qualification.
	 */
	protected Map<String, List<Activity>> combinedQuaActRelation() {
	
		for (Activity act : problem.getActivities()) {
			for (String quaId : act.getMode().getQualificationAmountMap().keySet()) {
				if (!combinedQuaActRelation.containsKey(quaId)) {
					
					combinedQuaActRelation.put(quaId, new ArrayList<Activity>());
				}
				combinedQuaActRelation.get(quaId).add(act);
			}
		}
		//for (List<Activity> list : combinedQuaActRelation.values()) {
		//	sortActivities(list);
		//}
		
		return combinedQuaActRelation;
		
	}
	
	/**
	 * Because we combine the activities before we solve it. Now we assign the
	 * allocated resources to each activity.
	 * 
	 */
	protected void assignResToAct() {
		
		Map<String, Map<String, Integer>> unitedResultsCopy = new HashMap<String, Map<String, Integer>>();
		for (String qua : unitedResults.keySet()) {
			Map<String, Integer> allo = new HashMap<String, Integer>(unitedResults.get(qua));
			unitedResultsCopy.put(qua, allo);
		}
		
		for (String quaId : unitedResults.keySet()) {
			//List<String> sortRes = sortResDescending(unitedResults.get(quaId));
			List<String> sortRes = new ArrayList<String>(unitedResults.get(quaId).keySet());
			List<Activity> actList = combinedQuaActRelation.get(quaId);
			for (Activity act : actList) {
				if (!results.containsKey(act.getId())) {
					Map<String, Map<String, Integer>> a = new HashMap<String, Map<String, Integer>>();
					results.put(act.getId(), a);
				}
				Map<String, Map<String, Integer>> a = results.get(act.getId());
				Map<String, Integer> b = new HashMap<String, Integer>();
				a.put(quaId, b);
				int sum = 0;
				for (String resID : sortRes) {
					int num=unitedResultsCopy.get(quaId).get(resID);
					if(num==0){
						continue;
					}
					sum += num;
					if (sum < act.getMode().getQualificationAmountMap().get(quaId)) {
						b.put(resID, num);
						
						unitedResultsCopy.get(quaId).put(resID, 0);
					}
					else {
						b.put(resID, num - (sum - act.getMode().getQualificationAmountMap().get(quaId)));
						unitedResultsCopy.get(quaId).put(resID, sum - act.getMode().getQualificationAmountMap().get(quaId));
						break;
					}
				}
			}
			
		}
		
	}
	protected void generateResults() {
		combinedQuaActRelation();
		assignResToAct();
		/*Map<String, Set<Integer>> copy = new HashMap<String, Set<Integer>>(problem.getAvailableResAmount());
		for (String resId : copy.keySet()) {
			Set<Integer> set = new HashSet<Integer>(problem.getAvailableResAmount().get(resId));
			copy.put(resId, set);
		}
		
		Map<String, Map<String, Integer>> unitedResultsCopy = new HashMap<String, Map<String, Integer>>();
		for (String qua : unitedResults.keySet()) {
			Map<String, Integer> allo = new HashMap<String, Integer>(unitedResults.get(qua));
			unitedResultsCopy.put(qua, allo);
		}
		
		for (Activity act : problem.getActList()) {
			ResourcesForOneActivity chosen = new ResourcesForOneActivity();
			for (String quaId : act.getMode().getQualificationAmountMap().keySet()) {
				if (quaId.equals("DUMMY")) {
					continue;
				}
				int sum = 0;
				for (String resID : unitedResultsCopy.get(quaId).keySet()) {
					sum += unitedResultsCopy.get(quaId).get(resID);
					if (sum < act.getMode().getQualificationAmountMap().get(quaId)) {
						for (int i = 0; i < unitedResultsCopy.get(quaId).get(resID); i++) {
							chosen.addResources(problem.getQuaMap().get(quaId), problem.getResMap().get(resID), copy.get(resID).iterator().next());
							copy.get(resID).remove(copy.get(resID).iterator().next());
						}
						
						unitedResultsCopy.get(quaId).put(resID, 0);
					}
					else {
						for (int i = 0; i < unitedResultsCopy.get(quaId).get(resID) - (sum - act.getMode().getQualificationAmountMap().get(quaId)); i++) {
							chosen.addResources(problem.getQuaMap().get(quaId), problem.getResMap().get(resID), copy.get(resID).iterator().next());
							copy.get(resID).remove(copy.get(resID).iterator().next());
						}
						unitedResultsCopy.get(quaId).put(resID, sum - act.getMode().getQualificationAmountMap().get(quaId));
						break;
					}
				}
			}
			results.put(act.getId(), chosen);
		}*/
		
	}
	
	protected void printUnitedResults(Map<String, Map<String, Integer>> unitedResults, String phase) {
	
		System.out.println("=========================================");
		System.out.println("United Results " + phase);
		for (String qua : unitedResults.keySet()) {
			System.out.println("----------------------------------------------------");
			System.out.println("Qualification " + qua);
			for (String res : unitedResults.get(qua).keySet()) {
				if (unitedResults.get(qua).get(res) == 0) {
					continue;
				}
				String str = "     Resource: " + res;
				int len = str.length();
				for (int i = 0; i < 30 - len; i++) {
					str += " ";
				}
				System.out.println(str + "num: " + unitedResults.get(qua).get(res));
			}
		}
		System.out.println("----------------------------------------------------");
	}
	
	@Override
	protected void printTempData() {
	
		graphicSpace.print();
		printUnitedResults(unitedResults, "");
		
	}
	


	
}
