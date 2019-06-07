

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import model.Activity;
import model.Model;
import model.Qualification;
import model.Resource;


public class ModelFactoryN {

	public static Model makeExample() {
		Model model=new Model();
		//Map<String, Resource>  resList=new HashMap<String, Resource> ();
		//model.setResources(resList);
		Map<String, Qualification> qualifications=new HashMap<String, Qualification> ();
		model.setQualifications(qualifications);
		Map<String, List<String>> quaResRelationMap=new HashMap<String, List<String>>();;
		model.setQualificationResourceRelation(quaResRelationMap);
		
		//Map<String, Set<Integer>> availableRes=new HashMap<String, Set<Integer>>();
		//model.setAvailableResAmount(availableRes);
		
		//List<Activity> actList = new ArrayList<Activity>();
		//model.setActivities(actList);
		
	
		
		
		// resource map
		
		Resource res1 = new Resource();
		res1.setResId("res1");
		res1.setTotalAmount(15);
		res1.setCost(2.8);
		model.addResource(res1);
		
	
		Resource res2 = new Resource();
		res2.setResId("res2");
		res2.setTotalAmount(22);
		res2.setCost(3.1);
		model.addResource(res2);
		Resource res3 = new Resource();
		res3.setResId("res3");
		res3.setTotalAmount(23);
		res3.setCost(3.5);
		model.addResource(res3);
		// make the relation
		
		List<String> list1 = new ArrayList<String>();
		list1.add(res1.getId());
		list1.add(res2.getId());
		list1.add(res3.getId());
		Qualification qua1 = new Qualification("qua1");
		quaResRelationMap.put(qua1.getId(), list1);
		List<String> list2 = new ArrayList<String>();
		list2.add(res2.getId());
		Qualification qua2 = new Qualification("qua2");
		quaResRelationMap.put(qua2.getId(), list2);
		List<String> list3 = new ArrayList<String>();
		list3.add(res2.getId());
		list3.add(res3.getId());
		Qualification qua3 = new Qualification("qua3");
		quaResRelationMap.put(qua3.getId(), list3);
		List<String> list4 = new ArrayList<String>();
		list4.add(res1.getId());
		list4.add(res2.getId());
		Qualification qua4 = new Qualification("qua4");
		quaResRelationMap.put(qua4.getId(), list4);
		// act
	
		Activity act1 = new Activity();
		act1.addQuaandNum(qua1, 9);
		act1.addQuaandNum(qua3, 5);
		act1.addQuaandNum(qua2, 5);
		model.addActivity(act1);

		Activity act2 = new Activity();
		act2.addQuaandNum(qua3, 15);
		act2.addQuaandNum(qua4, 12);
		model.addActivity(act2);

		Activity act3 = new Activity();
		act3.addQuaandNum(qua1, 5);
		act3.addQuaandNum(qua2, 5);
		model.addActivity(act3);
		
		
		
		/*for(Resource res:resList.values()) {
			
			Set<Integer> amount=new HashSet<Integer>();
			for(int i=1;i<=res.getAvailableAmount();i++) {
				amount.add(i);
			}
			availableRes.put(res.getId(), amount);
		}*/
		
		return model;

	}
	
	public static List<Model> makeRandomExamples()
	{
		int[] activityNum=new int[] {10,20,50,100,150,200,300,400};
		double[] skillMasterLevel=new double[] {0,0.25,0.5,0.75,1};
		
		
		List<Model> models=new ArrayList<Model>();
		
		for(int actNum:activityNum) {
			for(double skillLevel:skillMasterLevel) {
				models.add(makeRandomExample(actNum,skillLevel));
				
			}
		}
		return models;
		
	}
	
	public static Model makeRandomExample(int actNumber,double skillLevel ) {
		Model model=new Model();
		model.setSkillLevel(skillLevel);
		//Map<String, Resource>  resList=new HashMap<String, Resource> ();
		//model.setResources(resList);
		Map<String, Qualification> qualifications=new HashMap<String, Qualification> ();
		model.setQualifications(qualifications);
		Map<String, List<String>> quaResRelationMap=new HashMap<String, List<String>>();;
		model.setQualificationResourceRelation(quaResRelationMap);
		
		//Map<String, Set<Integer>> availableRes=new HashMap<String, Set<Integer>>();
		//model.setAvailableResAmount(availableRes);
		
		//List<Activity> actList = new ArrayList<Activity>();
		//model.setActivities(actList);
		
	
		int resourceNum=(int) (50+Math.abs(0.5-skillLevel)*10);
		int resourceAmount=100;
		int qualificationNum=20;
		int activityNum=actNumber;
		
		double rnd1=skillLevel;
		double rnd2=0.2;
		
		Random random=new Random(100);
		// resource map
		
		for(int i=1;i<=resourceNum;i++){
			Resource res = new Resource();
			res.setResId("res"+i);
			res.setTotalAmount(1+random.nextInt(resourceAmount-1));
			res.setCost(0.1+random.nextInt(100));
			model.addResource(res);
		}
		
		Map<Qualification,Integer>qualificationAmount=new HashMap<Qualification,Integer>();
		
		// make the relation
		for(int i=1;i<=qualificationNum;i++){
			Qualification qua = new Qualification("qua"+i);
			qualifications.put(qua.getId(), qua);
			List<String> list = new ArrayList<String>();
			int sum=0;
			for(Resource res:model.getResources().values()){
				
				if(random.nextDouble()<rnd1){
					list.add(res.getId());
					sum+=res.getAvailableAmount();
					res.getQualifications().add(qua);
				}
			}
			
			qualificationAmount.put(qua, sum);
			quaResRelationMap.put(qua.getId(), list);
		}
	
	
		Qualification[] quas = model.getQualifications().values().toArray(new Qualification[0] );
		for(Resource res:model.getResources().values()) {
			if(res.getQualifications().isEmpty()) {
				Qualification qua = quas[random.nextInt(quas.length)];
				res.getQualifications().add(qua);
				quaResRelationMap.get(qua.getId()).add(res.getId());
				qualificationAmount.put(qua, qualificationAmount.get(qua)+res.getAmount());
			}
		}
		
		Resource[] resources = model.getResources().values().toArray(new Resource[0] );
		for(Qualification qua:quas) {
			if(quaResRelationMap.get(qua.getId()).isEmpty()) {
				Resource res = resources[random.nextInt(resources.length)];
				quaResRelationMap.get(qua.getId()).add(res.getId());
				res.getQualifications().add(qua);
				qualificationAmount.put(qua, qualificationAmount.get(qua)+res.getAmount());
			}
		}
		

		// act
		
		for(int i=1;i<=activityNum;i++){
			Activity act = new Activity();
			act.getMode().setProcessingTime(5);
			for(Qualification qua:qualifications.values()){
				if(random.nextDouble()<rnd2){
					
					act.addQuaandNum(qua, (int) (1+random.nextInt(qualificationAmount.get(qua)-1)/activityNum));
				}
			}
			model.addActivity(act);
		}
	

		
		

		
		return model;

	}
	
	
	public static Model makeFullModel() {
		Model model=new Model();
		//Map<String, Resource>  resList=new HashMap<String, Resource> ();
		//model.setResources(resList);
		Map<String, Qualification> qualifications=new HashMap<String, Qualification> ();
		model.setQualifications(qualifications);
		Map<String, List<String>> quaResRelationMap=new HashMap<String, List<String>>();;
		model.setQualificationResourceRelation(quaResRelationMap);
				
		// resource map
		
		Resource res1 = new Resource();
		res1.setResId("res1");
		res1.setTotalAmount(15);
		res1.setCost(2.8);
		model.addResource(res1);
		
	
		Resource res2 = new Resource();
		res2.setResId("res2");
		res2.setTotalAmount(22);
		res2.setCost(3.1);
		model.addResource(res2);
		
		Resource res3 = new Resource();
		res3.setResId("res3");
		res3.setTotalAmount(23);
		res3.setCost(3.5);
		model.addResource(res3);
		
		// make the relation
		
		List<String> list1 = new ArrayList<String>();
		list1.add(res1.getId());
		list1.add(res2.getId());
		list1.add(res3.getId());
		Qualification qua1 = new Qualification("qua1");
		quaResRelationMap.put(qua1.getId(), list1);
		List<String> list2 = new ArrayList<String>();
		list2.add(res2.getId());
		Qualification qua2 = new Qualification("qua2");
		quaResRelationMap.put(qua2.getId(), list2);
		List<String> list3 = new ArrayList<String>();
		list3.add(res2.getId());
		list3.add(res3.getId());
		Qualification qua3 = new Qualification("qua3");
		quaResRelationMap.put(qua3.getId(), list3);
		List<String> list4 = new ArrayList<String>();
		list4.add(res1.getId());
		list4.add(res2.getId());
		Qualification qua4 = new Qualification("qua4");
		quaResRelationMap.put(qua4.getId(), list4);
		
		
		// act
	
		Activity act1 = new Activity();
		act1.getMode().setProcessingTime(5);
		act1.addQuaandNum(qua1, 9);
		act1.addQuaandNum(qua3, 5);
		act1.addQuaandNum(qua2, 5);
		model.addActivity(act1);

		Activity act2 = new Activity();
		act2.getMode().setProcessingTime(10);
		act2.addQuaandNum(qua3, 15);
		act2.addQuaandNum(qua4, 12);
		model.addActivity(act2);

		Activity act3 = new Activity();
		act3.getMode().setProcessingTime(8);
		act3.addQuaandNum(qua1, 5);
		act3.addQuaandNum(qua2, 5);
		model.addActivity(act3);
		
		
		Activity act4 = new Activity();
		act4.getMode().setProcessingTime(11);
		act4.addQuaandNum(qua1, 5);
		model.addActivity(act4);
		act4.getPredecessors().add(act1);
		act4.getPredecessors().add(act2);
		
		
		Activity act5 = new Activity();
		act5.getMode().setProcessingTime(3);
		act5.addQuaandNum(qua3, 5);
		model.addActivity(act5);
		act5.getPredecessors().add(act1);
		act5.getPredecessors().add(act2);
		
		
		Activity act6 = new Activity();
		act6.getMode().setProcessingTime(7);
		act6.addQuaandNum(qua2, 5);
		model.addActivity(act6);
		act6.getPredecessors().add(act4);

		Activity act7 = new Activity();
		act7.getMode().setProcessingTime(9);
		act7.addQuaandNum(qua2, 5);
		act7.addQuaandNum(qua3, 5);
		model.addActivity(act7);
		act7.getPredecessors().add(act3);
		
		Activity act8 = new Activity();
		act8.getMode().setProcessingTime(4);
		act8.addQuaandNum(qua2, 5);
		model.addActivity(act8);
		act8.getPredecessors().add(act5);
		act8.getPredecessors().add(act6);
		act8.getPredecessors().add(act7);
		
		
		Activity act9 = new Activity();
		act9.getMode().setProcessingTime(15);
		act9.addQuaandNum(qua1, 5);
		act9.addQuaandNum(qua3, 5);
		model.addActivity(act9);
		act9.getPredecessors().add(act3);
		
		return model;

	}
	
	public static Model makeSimpleModel() {
		Model model=new Model();
		//Map<String, Resource>  resList=new HashMap<String, Resource> ();
		//model.setResources(resList);
		Map<String, Qualification> qualifications=new HashMap<String, Qualification> ();
		model.setQualifications(qualifications);
		Map<String, List<String>> quaResRelationMap=new HashMap<String, List<String>>();;
		model.setQualificationResourceRelation(quaResRelationMap);
				
		// resource map
		
		Resource res1 = new Resource();
		res1.setResId("res1");
		res1.setTotalAmount(1);
		res1.setCost(2.8);
		model.addResource(res1);
		
	
		Resource res2 = new Resource();
		res2.setResId("res2");
		res2.setTotalAmount(1);
		res2.setCost(3.1);
		model.addResource(res2);
		
		
		// make the relation
		
		List<String> list1 = new ArrayList<String>();
		list1.add(res1.getId());
		list1.add(res2.getId());
		Qualification qua1 = new Qualification("qua1");		
		quaResRelationMap.put(qua1.getId(), list1);
		
		List<String> list2 = new ArrayList<String>();
		list2.add(res1.getId());
		Qualification qua2 = new Qualification("qua2");
		quaResRelationMap.put(qua2.getId(), list2);
		
		List<String> list3 = new ArrayList<String>();
		list3.add(res1.getId());
		list3.add(res2.getId());
		Qualification qua3 = new Qualification("qua3");
		quaResRelationMap.put(qua3.getId(), list3);
		
		
		
		// act
	
		Activity act1 = new Activity();
		act1.getMode().setProcessingTime(5);
		act1.addQuaandNum(qua1, 1);
		model.addActivity(act1);

		Activity act2 = new Activity();
		act2.getMode().setProcessingTime(6);
		act2.addQuaandNum(qua1, 1);
		model.addActivity(act2);

		Activity act3 = new Activity();
		act3.getMode().setProcessingTime(8);
		act3.addQuaandNum(qua3, 1);
		model.addActivity(act3);
		
		
		Activity act4 = new Activity();
		act4.getMode().setProcessingTime(11);
		act4.addQuaandNum(qua2, 1);
		model.addActivity(act4);

		
		act3.getPredecessors().add(act1);
		act4.getPredecessors().add(act2);
		
		
	
		
		return model;

	}

	
}
