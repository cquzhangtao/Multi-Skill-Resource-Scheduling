package problem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResourcesForOneActivity {

	
	Map<Qualification, Map<Resource, Set<Integer>>> resourcePositionsByQualification=new HashMap<Qualification, Map<Resource, Set<Integer>>>();
	
	public Map<Qualification, Map<Resource, Set<Integer>>> getResources() {
	
		return resourcePositionsByQualification;
	}

	public void addResources(Qualification qualification, Resource resource,
			Integer position) {
		if (!resourcePositionsByQualification.containsKey(qualification)) {
			resourcePositionsByQualification.put(qualification, new HashMap<Resource, Set<Integer>>());
		}
	
		if (!resourcePositionsByQualification.get(qualification).containsKey(resource)) {
			resourcePositionsByQualification.get(qualification).put(resource, new HashSet<Integer>());
		}
		
		resourcePositionsByQualification.get(qualification).get(resource).add(position);
		
	}

}
