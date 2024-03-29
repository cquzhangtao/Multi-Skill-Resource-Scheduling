

import java.util.ArrayList;
import java.util.Collection;
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
	
	public static List<Model> makeTestExamples()
	{
		
		
	int[] resourceNumLevel = new int[] { 10 };
	int[] activityNumLevel = new int[] { 10};
		int[] skillNumLevel=new int[] {12};
	
	double[] skillMasterLevel = new double[] {  0.25};
	double[] skillRequireLevel = new double[] {  0};
	
//		int[] resourceNumLevel = new int[] { 10  };
//		int[] activityNumLevel = new int[] { 20};
//		int[] skillNumLevel=new int[] {5};
//		
//		double[] skillMasterLevel = new double[] {0.5};
//		double[] skillRequireLevel = new double[] { 0.5};
//		
		int replication=1;
		Random rnd=new Random(0);
		List<Model> models=new ArrayList<Model>();
		for(int resNum:resourceNumLevel) {
		for(int actNum:activityNumLevel) {
			for(int skillNum:skillNumLevel) {
			for(double skillMaster:skillMasterLevel) {
				for(double skillRequire:skillRequireLevel) {
					for(int i=1;i<=replication;i++) {
						models.add(generateModel(rnd,resNum,actNum,skillNum,skillMaster,skillRequire,i));
					}
				}
			}
		
				
			}
		}
		}
		return models;
		
	}
	public static List<Model> makeRandomExamples()
	{
		
		
	int[] resourceNumLevel = new int[] { 10 ,30,50 };
	int[] activityNumLevel = new int[] { 10,20,50,100,150,200/*,300,400 */};
		int[] skillNumLevel=new int[] {12};
	
	double[] skillMasterLevel = new double[] {  0, 0.25,0.5,0.75,1};
	double[] skillRequireLevel = new double[] {  0,0.25,0.5,0.75,1};
	
//		int[] resourceNumLevel = new int[] { 10  };
//		int[] activityNumLevel = new int[] { 20};
//		int[] skillNumLevel=new int[] {5};
//		
//		double[] skillMasterLevel = new double[] {0.5};
//		double[] skillRequireLevel = new double[] { 0.5};
//		
		int replication=1;
		Random rnd=new Random(0);
		List<Model> models=new ArrayList<Model>();
		for(int resNum:resourceNumLevel) {
		for(int actNum:activityNumLevel) {
			for(int skillNum:skillNumLevel) {
			for(double skillMaster:skillMasterLevel) {
				for(double skillRequire:skillRequireLevel) {
					for(int i=1;i<=replication;i++) {
						models.add(generateModel(rnd,resNum,actNum,skillNum,skillMaster,skillRequire,i));
					}
				}
			}
		
				
			}
		}
		}
		return models;
		
	}
	public static Model generateModel(Random rnd,int resNum,int actNum,int skillNum,double rsf,double asf,int rep) {
		/*int resNum=50;
		int actNum=1000;
		int skillNum=20;
		
		double rsf=0.5;
		double asf=0.2;*/
		
		int maxNumOfResPerSkillRequiredByAct=20;		
		double finishedActRatio=0.9;
		
		
		Model model=new Model();
		
		rnd=new Random(0);
		
		model.setId(resNum+"\t"+actNum+"\t"+skillNum+"\t"+rsf+"\t"+asf+"\t"+rep+"\t|");
		//System.out.println(model.getId());
		for(int i=0;i<skillNum;i++) {
			Qualification qua=new Qualification("Skill"+i);
			model.addQualification(qua);
		}
		
		for(int i=0;i<resNum;i++) {
			Resource res=new Resource("Res"+i);
			model.addResource(res);
			
			List<String> quas = getRandomSubList(rnd,model.getQualifications().keySet(),(int)(rsf*skillNum));
			res.setQualifications(quas);
			
			res.setCost((0.1+rnd.nextDouble())/1000);
			
			for(String qua:quas) {
				model.getQualifications().get(qua).getResources().add(res.getId());
			}
			
			
		}
		
		for(Qualification qua:model.getQualifications().values()) {
			if(qua.getResources().isEmpty()) {
				Resource res=model.getResources().get("Res"+rnd.nextInt(resNum));
				res.getQualifications().add(qua.getId());				
				qua.getResources().add(res.getId());
			}
		}
		
	
		
		
		Map<Qualification,Integer> resSkill=new HashMap<Qualification,Integer>();
		
		for(int i=0;i<actNum;i++) {
			Activity act=new Activity("Act"+i);
			model.addActivity(act);
			
			List<Qualification> quas = getRandomSubList(rnd,model.getQualifications().values(),(int)(asf*skillNum));
			
			for(Qualification qua:quas) {
				qua.getActivities().add(act.getId());
				int amount=1+rnd.nextInt(maxNumOfResPerSkillRequiredByAct);
				act.getMode().add(qua.getId(), amount);
				
				if(!resSkill.containsKey(qua)) {
					resSkill.put(qua, amount);
				}else {
					resSkill.put(qua, resSkill.get(qua)+amount);
				}
			}
			
		}
		
		
		for(Qualification qua:model.getQualifications().values()) {
			if(qua.getActivities().isEmpty()) {
				Activity act=model.getActivityMap().get("Act"+rnd.nextInt(actNum));
				qua.getActivities().add(act.getId());
				int amount=1+rnd.nextInt(maxNumOfResPerSkillRequiredByAct);
				//if(act.getMode().getQualificationAmountMap().containsKey(qua.getId())) {
				//	act.getMode().add(qua.getId(), act.getMode().getQualificationAmountMap().get(qua.getId())+amount);
				//}else {
					act.getMode().add(qua.getId(), amount);
				//}
				if(!resSkill.containsKey(qua)) {
					resSkill.put(qua, amount);
				}else {
					resSkill.put(qua, resSkill.get(qua)+amount);
				}			
				
			}
		}
		
		
		for(Qualification qua:model.getQualifications().values()) {
			model.getQualificationResourceRelation().put(qua.getId(), qua.getResources());
			int sum=0;
			for(String resStr:qua.getResources()) {
				Resource res=model.getResources().get(resStr);
				double amount=finishedActRatio*resSkill.get(qua)/qua.getResources().size();
				if(amount-(int)amount>0) {
					amount++;
				}
				res.setTotalAmount(Math.max(1,res.getAmount()+(int)amount));
				sum+=(int)amount;
			}
			if(sum-finishedActRatio*resSkill.get(qua)<0) {
				//System.out.println("Generate cases wrong,"+sum+","+finishedActRatio*resSkill.get(qua));
			}
		}
		
		//model.print();
		return model;
	}
	
	public static <T> List<T> getRandomSubList(Random r,Collection<T> inputc, int subsetSize)
	{
		if(subsetSize==0) {
			subsetSize=1;
		}
	   
	    ArrayList<T> input = new ArrayList<T>(inputc);
	    int inputSize = input.size();
	    for (int i = 0; i < subsetSize; i++)
	    {
	        int indexToSwap = i + r.nextInt(inputSize - i);
	        T temp = input.get(i);
	        input.set(i, input.get(indexToSwap));
	        input.set(indexToSwap, temp);
	    }
	    return input.subList(0, subsetSize);
	}

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
	
	public static List<Model> makeRandomExamplesOld()
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
		model.setId(actNumber+"\t"+skillLevel);
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
			res.setCost((0.1+random.nextInt(100))/1000);
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
					res.getQualifications().add(qua.getId());
				}
			}
			
			qualificationAmount.put(qua, sum);
			quaResRelationMap.put(qua.getId(), list);
		}
	
	
		Qualification[] quas = model.getQualifications().values().toArray(new Qualification[0] );
		for(Resource res:model.getResources().values()) {
			if(res.getQualifications().isEmpty()) {
				Qualification qua = quas[random.nextInt(quas.length)];
				res.getQualifications().add(qua.getId());
				quaResRelationMap.get(qua.getId()).add(res.getId());
				qualificationAmount.put(qua, qualificationAmount.get(qua)+res.getAmount());
			}
		}
		
		Resource[] resources = model.getResources().values().toArray(new Resource[0] );
		for(Qualification qua:quas) {
			if(quaResRelationMap.get(qua.getId()).isEmpty()) {
				Resource res = resources[random.nextInt(resources.length)];
				quaResRelationMap.get(qua.getId()).add(res.getId());
				res.getQualifications().add(qua.getId());
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
