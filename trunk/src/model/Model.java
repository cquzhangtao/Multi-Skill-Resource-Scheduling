
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Problem data structure
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class Model {
	
	/**
	 * Resource map in the simulation model
	 */
	private Map<String, Resource> resources=new HashMap<String,Resource>();
	
	/**
	 * Qualification map
	 */
	private Map<String, Qualification> qualifications=new HashMap<String,Qualification>();;
	

	
	/**
	 * The relation between resources and qualification
	 */
	private Map<String, List<String>> qualificationResourceRelation=new HashMap<String, List<String>>();
	
	/**
	 * The activity list we need to allocate resources to. The resources are in
	 * certain sequence in the list.
	 */
	private List<Activity> activities=new ArrayList<Activity>();
	
	private Map<String,Activity> activityMap=new HashMap<String,Activity>();
	
	private double skillLevel=1;
	
	//private Map<String, Set<Integer>> availableResAmount;
	
	/**
	 * Constructor
	 * 
	 * @param resMap
	 * @param quaResRelationMap
	 * @param actList
	 * @param quaMap
	 * @param resCostMap
	 */
	public Model(Map<String, Resource> resMap,List<Activity> actList, Map<String, Qualification> quaMap,Map<String, List<String>> quaResRelationMap 
			) {
	
		this.resources = resMap;
		this.qualifications = quaMap;
		this.qualificationResourceRelation = quaResRelationMap;
		this.activities = new ArrayList<Activity>(actList);
		//this.availableResAmount = availableResID;
		
	}
	
	public Model() {
		
	}
	
	public Model clone() {
		
		Model model=new Model();
		
		model.resources = resources;
		model.qualifications = qualifications;
		model.qualificationResourceRelation = qualificationResourceRelation;
		model.activities = new ArrayList<Activity>(activities);
		model.activityMap=new HashMap<String,Activity>(activityMap);
		
		return model;
	}
	
	public void addQualification(Qualification qua) {
		qualifications.put(qua.getId(),qua);
	}

	/**
	 * Please keep this function in case we need to debug
	 */
	private void printQuaResRelationMap() {
	
		String str = "";
		str += "Resource            ";
		for (String qua : qualificationResourceRelation.keySet()) {
			if (qua.equals("DUMMY")) {
				continue;
			}
			str += qua;
			for (int i = 0; i < 10 - qua.length(); i++) {
				str += " ";
			}
		}
		for (int i = 0; i < str.length(); i++) {
			System.out.print("=");
		}
		System.out.println();
		System.out.println("Relations between Qualifications and Resources");
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.println(str);
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		for (String res : resources.keySet()) {
			if (resources.get(res).getDummy()) {
				continue;
			}
			System.out.print(res);
			for (int i = 0; i < 20 - res.length(); i++) {
				System.out.print(" ");
			}
			for (String qua : qualificationResourceRelation.keySet()) {
				String s = "";
				if (qualificationResourceRelation.get(qua).contains(res)) {
					s = "Y";
				}
				else {
					s = " ";
				}
				System.out.print(s + "         ");
			}
			System.out.println();
			for (int i = 0; i < str.length(); i++) {
				System.out.print("-");
			}
			System.out.println();
		}
	}
	
	private void printResource() {
	
		String str = "Resource    ";
		for (Resource res : resources.values()) {
			if (res.getDummy()) {
				continue;
			}
			str += res.getId();
			for (int i = 0; i < 12 - res.getId().length(); i++) {
				str += " ";
			}
		}
		for (int i = 0; i < str.length(); i++) {
			System.out.print("=");
		}
		System.out.println();
		System.out.println("Resources, Amount, and Price");
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.println(str);
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.print("Amount      ");
		for (Resource res : resources.values()) {
			if (res.getDummy()) {
				continue;
			}
			System.out.print(res.getAvailableAmount());
			for (int i = 0; i < 12 - String.valueOf(res.getAvailableAmount()).length(); i++) {
				System.out.print(" ");
			}
		}
		System.out.println();
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.print("Price       ");
		for (Resource res : resources.values()) {
			if (res.getDummy()) {
				continue;
			}
			double cost = 0l;
			
			cost = res.getCost();
			
			String coststr=String.format("%.2f", cost);
			System.out.print(coststr);
			for (int i = 0; i < 12 - coststr.length(); i++) {
				System.out.print(" ");
			}
		}
		
		System.out.println();
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		
	}
	
	private void printActivity() {
	
		String str = "";
		str += "Activity                                ";
		for (String qua : qualificationResourceRelation.keySet()) {
			if (qua.equals("DUMMY")) {
				continue;
			}
			str += qua;
			for (int i = 0; i < 10 - qua.length(); i++) {
				str += " ";
			}
		}
		for (int i = 0; i < str.length(); i++) {
			System.out.print("=");
		}
		System.out.println();
		System.out.println("Activities and Requirement");
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.println(str);
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		int n = 0;
		for (Activity act : activities) {
			n++;
			if (n % 20 == 0) {
				System.out.println(str);
				for (int i = 0; i < str.length(); i++) {
					System.out.print("-");
				}
				System.out.println();
			}
			
			System.out.print(act.getId());
			for (int i = 0; i < 40 - act.getId().length(); i++) {
				System.out.print(" ");
			}
			for (String qua : qualificationResourceRelation.keySet()) {
				Integer amount = act.getMode().getQualificationAmountMap().get(qua);
				if (amount == null) {
					for (int i = 0; i < 10; i++) {
						System.out.print(" ");
					}
				}
				else {
					System.out.print(amount);
					
					for (int i = 0; i < 10 - amount.toString().length(); i++) {
						System.out.print(" ");
					}
				}
			}
			System.out.println();
			for (int i = 0; i < str.length(); i++) {
				System.out.print("-");
			}
			System.out.println();
			
		}
	}
	
	
	private void printQuaNum() {
		
		String str = "";
		str += "Qualification            ";
		for (String qua : qualificationResourceRelation.keySet()) {
			if (qua.equals("DUMMY")) {
				continue;
			}
			str += qua;
			for (int i = 0; i < 10 - qua.length(); i++) {
				str += " ";
			}
		}
		for (int i = 0; i < str.length(); i++) {
			System.out.print("=");
		}
		System.out.println();
		System.out.println("Qualifications");
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.println(str);
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		
		System.out.print("Amount                   ");
		for (String qua : qualificationResourceRelation.keySet()) {
			List<String> ress = qualificationResourceRelation.get(qua);
			int sum=0;
			for(String res:ress){
				sum+=resources.get(res).getAvailableAmount();
			}
			System.out.print(sum);
			for (int i = 0; i < 10 - String.valueOf(sum).length(); i++) {
				System.out.print(" ");
			}

			
		}
		System.out.println();
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		
		

	}
	
	public void print() {
		printQuaNum();
		printQuaResRelationMap();
		printResource();
		printActivity();
	}
	

	
	//public Map<String, Qualification> getQuaMap() {
	
	//	return qualifications;
	//}
	
	//public void setQuaMap(Map<String, Qualification> quaMap) {
	
	//	this.qualifications = quaMap;
	//}
	
	//public Map<String, Resource> getResMap() {
	
	//	return resources;
	//}
	
	//public void setResMap(Map<String, Resource> res) {
	
	//	this.resources = res;
	//}
	
	//public Map<String, List<String>> getQuaResRelationMap() {
	
	//	return qualificationResourceRelation;
	//}
	
	//public void setQuaResRelationMap(Map<String, List<String>> qua) {
	
	//	this.qualificationResourceRelation = qua;
	//}
	
	public List<Activity> getActivities() {
	
		return activities;
	}
	
	public Map<String,Activity> getActivityMap(){
		return activityMap;
	}
	
	//public void setActivities(List<Activity> actMap) {
	//
	//	this.activities = actMap;
	//}
	
	public void addActivity(Activity act) {
		activities.add(act);
		activityMap.put(act.getId(), act);
	}
	
	//public Map<String, Set<Integer>> getAvailableResAmount() {
	
	//	return availableResAmount;
	//}
	
	//public void setAvailableResAmount(Map<String, Set<Integer>> availableResID) {
	
	//	this.availableResAmount = availableResID;
	//}

	public Map<String, Resource> getResources() {
		return resources;
	}

	//public void setResources(Map<String, Resource> resources) {
	//	this.resources = resources;
	//}
	
	public void addResource(Resource res) {
		res.setIndex(resources.size());
		resources.put(res.getId(),res);
	}

	public Map<String, Qualification> getQualifications() {
		return qualifications;
	}

	public void setQualifications(Map<String, Qualification> qualifications) {
		this.qualifications = qualifications;
	}

	public Map<String, List<String>> getQualificationResourceRelation() {
		return qualificationResourceRelation;
	}

	public void setQualificationResourceRelation(Map<String, List<String>> qualificationResourceRelation) {
		this.qualificationResourceRelation = qualificationResourceRelation;
	}

	public double getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(double skillLevel) {
		this.skillLevel = skillLevel;
	}
	
	
	
}
