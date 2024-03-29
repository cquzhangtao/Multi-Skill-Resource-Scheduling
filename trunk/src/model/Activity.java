package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity extends Entity {

	private List<Activity> predecessors=new ArrayList<Activity>();
	private List<Activity> sucessors=new ArrayList<Activity>();
	
	private Map<String, Map<String, Integer>> assignment;
	
	private Mode mode=new Mode();
	
	private static int id=0;
	
	
	private boolean started=false;
	private long startTime=0;
	
	
	public Activity(String id) {
		setId(id);
		
	}
	public Activity() {
		setId("Act"+id++);
		
	}
	
	public boolean startable(long time) {
		
		for(Activity act:predecessors) {
			if(!act.started||act.startTime+act.mode.getProcessingTime()>time) {
				return false;
			}
		}
		return true;
	}

	public Mode getMode() {
		
		return mode;
	}
	
	

	public boolean getDummy() {
		return false;
	}

	public void addQuaandNum(Qualification qua, int amount) {
		mode.add(qua.getId(), amount);
		
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

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getEndTime() {
		return startTime+mode.getProcessingTime();
	}

	public Map<String, Map<String, Integer>> getAssignment() {
		return assignment;
	}

	public void setAssignment(Map<String, Map<String, Integer>> assignment) {
		this.assignment = assignment;
	}
	
	public String toString() {
		return super.getId();
	}

}
