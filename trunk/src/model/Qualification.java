package model;

import java.util.ArrayList;
import java.util.List;

public class Qualification extends Entity{

	private List<String> resources=new ArrayList<String>();
	private List<String> activities=new ArrayList<String>();
	
	public Qualification(String string) {
		setId(string);
	}
	public List<String> getResources() {
		return resources;
	}
	public void setResources(List<String> resources) {
		this.resources = resources;
	}
	public List<String> getActivities() {
		return activities;
	}
	public void setActivities(List<String> activities) {
		this.activities = activities;
	}



}
