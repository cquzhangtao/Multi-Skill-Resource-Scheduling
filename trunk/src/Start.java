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
import solver.or.ORASolverByCP;
import solver.or.ORASolverByMIP;

public class Start {
	static {
		System.loadLibrary("jniortools");
	}
	public static void main(String[] args) {
		Model problem = ModelFactory.makeRandomExample();
		problem.print();
		AbstractORASlover solver;//
		//solver=new ORASolverWithAllActivities(problem);		
		//solver=new ORASolverByReducingActivities(problem);
		solver=new ORASolverByIteratingExchange(problem);
		//solver=new ORASolverByMIP(problem);
		//solver=new ORASolverByCP(problem);
		solver.solve(false,false);
		solver.print();

		
		/*Model model=ModelFactory.makeFullModel();
		model.print();
		
		
		ParallelScheduling scheduling=new ParallelScheduling(model);
		//SerialScheduling scheduling=new SerialScheduling(model);
		scheduling.run();
		
		show(model);
		
		for(Activity act:model.getActivities()) {
			System.out.println(act.getId()+": "+act.getStartTime()+","+act.getEndTime());
		}*/

	}
	
	
	public static void show(Model model) {
		JFrame testFrame = new JFrame();
	    testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    testFrame.setExtendedState( testFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
	    final Chart comp = new Chart(model);
	    //comp.setPreferredSize(new Dimension(800, 600));
	    testFrame.getContentPane().add(comp, BorderLayout.CENTER);
	    testFrame.pack();
	    testFrame.setVisible(true);
	    
	   
	}

}
