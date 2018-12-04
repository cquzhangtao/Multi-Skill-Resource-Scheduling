

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import problem.Activity;
import problem.OverlappingResAllocProblem;
import problem.Qualification;
import problem.Resource;


public class ProblemFactory {

	public static OverlappingResAllocProblem makeExample() {
		OverlappingResAllocProblem model=new OverlappingResAllocProblem();
		Map<String, Resource>  resList=new HashMap<String, Resource> ();
		model.setResources(resList);
		Map<String, Qualification> qualifications=new HashMap<String, Qualification> ();
		model.setQualifications(qualifications);
		Map<String, List<String>> quaResRelationMap=new HashMap<String, List<String>>();;
		model.setQualificationResourceRelation(quaResRelationMap);
		
		Map<String, Set<Integer>> availableRes=new HashMap<String, Set<Integer>>();
		model.setAvailableResAmount(availableRes);
		
		List<Activity> actList = new ArrayList<Activity>();
		model.setActList(actList);
		
	
		
		
		// resource map
		
		Resource res1 = new Resource();
		res1.setResId("res1");
		res1.setTotalAmount(15);
		res1.setCost(2.8);
		resList.put(res1.getId(),res1);
		
	
		Resource res2 = new Resource();
		res2.setResId("res2");
		res2.setTotalAmount(22);
		res2.setCost(3.1);
		resList.put(res2.getId(),res2);
		Resource res3 = new Resource();
		res3.setResId("res3");
		res3.setTotalAmount(23);
		res3.setCost(3.5);
		resList.put(res3.getId(),res3);
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
		actList.add(act1);

		Activity act2 = new Activity();
		act2.addQuaandNum(qua3, 15);
		act2.addQuaandNum(qua4, 12);
		actList.add(act2);

		Activity act3 = new Activity();
		act3.addQuaandNum(qua1, 5);
		act3.addQuaandNum(qua2, 5);
		actList.add(act3);
		
		
		
		for(Resource res:resList.values()) {
			
			Set<Integer> amount=new HashSet<Integer>();
			for(int i=1;i<=res.getAvailableAmount();i++) {
				amount.add(i);
			}
			availableRes.put(res.getId(), amount);
		}
		
		return model;

	}
	
	
	public static OverlappingResAllocProblem makeRandomExample() {
		OverlappingResAllocProblem model=new OverlappingResAllocProblem();
		Map<String, Resource>  resList=new HashMap<String, Resource> ();
		model.setResources(resList);
		Map<String, Qualification> qualifications=new HashMap<String, Qualification> ();
		model.setQualifications(qualifications);
		Map<String, List<String>> quaResRelationMap=new HashMap<String, List<String>>();;
		model.setQualificationResourceRelation(quaResRelationMap);
		
		Map<String, Set<Integer>> availableRes=new HashMap<String, Set<Integer>>();
		model.setAvailableResAmount(availableRes);
		
		List<Activity> actList = new ArrayList<Activity>();
		model.setActList(actList);
		
	
		int resourceNum=50;
		int resourceAmount=100;
		int qualificationNum=20;
		int activityNum=200;
		
		double rnd1=0.3;
		double rnd2=0.2;
		
		Random random=new Random(100);
		// resource map
		
		for(int i=1;i<=resourceNum;i++){
			Resource res = new Resource();
			res.setResId("res"+i);
			res.setTotalAmount(1+random.nextInt(resourceAmount-1));
			res.setCost(0.1+random.nextDouble());
			resList.put(res.getId(),res);
		}
		
		Map<Qualification,Integer>qualificationAmount=new HashMap<Qualification,Integer>();
		
		// make the relation
		for(int i=1;i<=qualificationNum;i++){
			Qualification qua = new Qualification("qua"+i);
			qualifications.put(qua.getId(), qua);
			List<String> list = new ArrayList<String>();
			int sum=0;
			for(Resource res:resList.values()){
				
				if(random.nextDouble()<rnd1){
					list.add(res.getId());
					sum+=res.getAvailableAmount();
				}
			}
			
			qualificationAmount.put(qua, sum);
			quaResRelationMap.put(qua.getId(), list);
		}
	
	
	
		

		// act
		
		for(int i=1;i<=activityNum;i++){
			Activity act = new Activity();
			for(Qualification qua:qualifications.values()){
				if(random.nextDouble()<rnd2){
					
					act.addQuaandNum(qua, (int) (1+random.nextInt(qualificationAmount.get(qua)-1)/rnd2/activityNum));
				}
			}
			actList.add(act);
		}
	

		
		
		
		for(Resource res:resList.values()) {
			
			Set<Integer> amount=new HashSet<Integer>();
			for(int i=1;i<=res.getAvailableAmount();i++) {
				amount.add(i);
			}
			availableRes.put(res.getId(), amount);
		}
		
		return model;

	}

	
}
