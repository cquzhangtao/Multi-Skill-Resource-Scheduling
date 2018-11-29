package problem;

public class Resource extends Entity{

	private int amount;
	
	public boolean getDummy() {
		return false;
	}



	public Integer getMaxAmount() {
		
		return amount;
	}





	public void setResId(String string) {
		super.setId(string);
		
	}



	public void setTotal(int amount) {
		this.amount=amount;
		
	}
	
	private double cost;

	public double getCost() {
		
		return cost;
	}


	public void setCost(double cost) {
		this.cost = cost;
	}


}
