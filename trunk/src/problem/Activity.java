package problem;

import java.util.ArrayList;
import java.util.List;

public class Activity extends Entity {

	private List<Activity> predecessors=new ArrayList<Activity>();
	private List<Activity> sucessors=new ArrayList<Activity>();
	
	private Mode mode=new Mode();
	
	private static int id=1;
	public Activity() {
		setId("Act"+id++);
	}

	public Mode getMode() {
		
		return mode;
	}

	public boolean getDummy() {
		return false;
	}

	public void addQuaandNum(Qualification qua, int amount) {
		mode.add(qua, amount);
		
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public List<Activity> getSucessors() {
		return sucessors;
	}

	public void setSucessors(List<Activity> sucessors) {
		this.sucessors = sucessors;
	}

	public List<Activity> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<Activity> predecessors) {
		this.predecessors = predecessors;
	}

}
