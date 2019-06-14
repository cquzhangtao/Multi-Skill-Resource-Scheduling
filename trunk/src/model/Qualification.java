package model;

import java.util.ArrayList;
import java.util.List;

public class Qualification extends Entity{

	private List<String> resources=new ArrayList<String>();
	public Qualification(String string) {
		setId(string);
	}
	public List<String> getResources() {
		return resources;
	}
	public void setResources(List<String> resources) {
		this.resources = resources;
	}



}
