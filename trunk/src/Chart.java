import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JComponent;

import model.Activity;

public class Chart extends JComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Activity> activities;
	public Chart(List<Activity> activities) {
		super();
		this.activities=activities;
	}

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    //for (Line line : lines) {
	      //  g.setColor(Color.RED);
	      //  g.drawLine(0,0,100,100);
	    //}
	        
	        for(Activity act:activities) {
	        	
	        }
	}

}
