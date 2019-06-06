package model;

import java.util.ArrayList;
import java.util.List;

public class Resource extends Entity{

	private int amount;
	private int availableAmount;
	
	private List<Qualification> qualifications=new ArrayList<Qualification>();
	
	public boolean getDummy() {
		return false;
	}



	public int getAvailableAmount() {
		
		return availableAmount;
	}





	public void setResId(String string) {
		super.setId(string);
		
	}



	public void setTotalAmount(int amount) {
		this.setAmount(amount);
		setAvailableAmount(amount);
		
	}
	
	public void seize(int num) {
		availableAmount-= num;
	}
	
	public void release(int num) {
		availableAmount +=num;
	}
	
	private double cost;

	public double getCost() {
		
		return cost;
	}


	public void setCost(double cost) {
		this.cost = cost;
	}



	public void setAvailableAmount(int availableAmount) {
		this.availableAmount = availableAmount;
	}


	private int index;
	public void setIndex(int size) {
		index=size;
		
	}
	
	public int getIndex() {
		return index;
	}



	public int getAmount() {
		return amount;
	}



	public void setAmount(int amount) {
		this.amount = amount;
	}



	public List<Qualification> getQualifications() {
		return qualifications;
	}



	public void setQualifications(List<Qualification> qualifications) {
		this.qualifications = qualifications;
	}


}
