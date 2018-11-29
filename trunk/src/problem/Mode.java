package problem;

import java.util.HashMap;
import java.util.Map;

public class Mode {
	private Map<String,Integer> qualificationAmountMap=new HashMap<String,Integer>() ;
	
	
	public Map<String,Integer> getQualificationAmountMap() {

		return qualificationAmountMap;
	}

	public long getRawProcessingTime() {
		
		return 0;
	}

	public void setQualificationAmountMap(Map<String,Integer> qualificationAmountMap) {
		this.qualificationAmountMap = qualificationAmountMap;
	}
	
	public void add(Qualification qua,int amount) {
		qualificationAmountMap.put(qua.getId(), amount);
	}

}
