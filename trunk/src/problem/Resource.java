package problem;

public class Resource extends Entity{

	private int amount;
	private int availableAmount;
	
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
		this.amount=amount;
		setAvailableAmount(amount);
		
	}
	
	public void seize(int num) {
		amount-=num;
	}
	
	public void release(int num) {
		amount+=num;
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


}
