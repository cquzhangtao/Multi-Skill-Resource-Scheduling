import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import model.Activity;
import model.Model;
import solver.AbstractORASlover;
import solver.ORASolverWithAllActivities;
import solver.maxactamount.ORASolverByReducingActivities;
import solver.mincost.pricewithoutprotime.ORASolverByIteratingExchange;

public class Start {

	public static void main(String[] args) {
		/*Model problem = ModelFactory.makeRandomExample();
		problem.print();
		AbstractORASlover solver;//=new ORASolverWithAllActivities(problem);		
		//solver=new ORASolverByReducingActivities(problem);
		solver=new ORASolverByIteratingExchange(problem);
		solver.solve(true,true);
		//solver.print();*/
		
		Model model=ModelFactory.makeFullModel();
		ParallelScheduling scheduling=new ParallelScheduling(model);
		scheduling.run();
		
		//show(model.getActivities());
		
		for(Activity act:model.getActivities()) {
			System.out.println(act.getId()+": "+act.getStartTime()+","+act.getEndTime());
		}

	}
	
	
	public static void show(List<Activity> activities) {
		JFrame testFrame = new JFrame();
	    testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    final Chart comp = new Chart(activities);
	    comp.setPreferredSize(new Dimension(800, 600));
	    testFrame.getContentPane().add(comp, BorderLayout.CENTER);
	    testFrame.pack();
	    testFrame.setVisible(true);
	}

}
