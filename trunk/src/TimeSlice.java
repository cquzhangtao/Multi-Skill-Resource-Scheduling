import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Activity;

public class TimeSlice {
	
	private long start;
	private long end;
	private int amount=0;
	
	private List<Activity> activities=new ArrayList<Activity>();
	private Map<Activity,Integer> actAmount=new HashMap<Activity,Integer>();
	
	public TimeSlice(Long start, Long end) {
		this.start=start;
		this.end=end;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	
	public void addActivity(Activity act, int amount) {
		if(!activities.contains(act)) {
			activities.add(act);
			actAmount.put(act, amount);
		}else {
			actAmount.put(act, amount+actAmount.get(act));
		}
	}
	
	public int getActAmount(Activity act) {
		return actAmount.get(act);
	}
	
	public void addAmount(int inc) {
		amount+=inc;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public List<Activity> getActivities() {
		return activities;
	}

}
