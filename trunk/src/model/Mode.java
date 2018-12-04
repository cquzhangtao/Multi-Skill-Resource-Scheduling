package model;

import java.util.HashMap;
import java.util.Map;

public class Mode {
	private Map<String,Integer> qualificationAmountMap=new HashMap<String,Integer>() ;
	
	private long processingTime=0;
	
	public Map<String,Integer> getQualificationAmountMap() {

		return qualificationAmountMap;
	}



	public void setQualificationAmountMap(Map<String,Integer> qualificationAmountMap) {
		this.qualificationAmountMap = qualificationAmountMap;
	}
	
	public void add(Qualification qua,int amount) {
		qualificationAmountMap.put(qua.getId(), amount);
	}

	public long getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}

}
