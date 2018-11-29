package problem;

public class Activity extends Entity {

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

}
