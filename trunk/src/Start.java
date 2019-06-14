import java.awt.BorderLayout;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import model.Model;
import solver.AbstractORASlover;
import solver.maxactamount.ORASolverByReducingActivities;
import solver.mincost.pricewithoutprotime.ORASolverByIteratingExchange;
import solver.or.ORASolverByCP;
import solver.or.ORASolverByCPNonObjective;
import solver.or.ORASolverByMIP;
import solver.or.ORASolverByMIPGLPK;
import solver.or.ORASolverByMIPNonObjective;

public class Start {

	public static void main(String[] args) {
		
		
		
		
		
		AbstractORASlover solver = new ORASolverByIteratingExchange(ModelFactoryN.testModel());
		solver.solve(false,false);
		
		solver.print();
		
		
		
		
		
		List<Model> problems = ModelFactoryN.makeRandomExamples();
		DecimalFormat df=new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.UP);
		
		for(Model problem:problems) {
			//problem=ModelFactoryN.makeRandomExample(100,0.4);
			//AbstractORASlover solver;//
			int actNum = problem.getActivities().size();
			double skillLevel = problem.getSkillLevel();
			
//			solver=new ORASolverByReducingActivities(problem);
//			solver.solve(false,false);
//			solver.print();
//			
//			solver=new ORASolverByMIPNonObjective(problem);
//			solver.solve(false,false);
//			solver.print();
//			
			/*solver=new ORASolverByCPNonObjective(problem);
			solver.solve(false,false);
			solver.print();*/
			
			solver=new ORASolverByIteratingExchange(problem);
			solver.solve(false,false);
			//solver.print();			
					int exeAct1 = solver.getExecutedActivityNumber();
					double cost1=solver.getCost();
					double runTime1=solver.getTime();
			solver=new ORASolverByMIPGLPK(problem);
			solver.solve(false,false);
			//solver.print();
			int exeAct2 = solver.getExecutedActivityNumber();
			double cost2=solver.getCost();
			double runTime2=solver.getTime();
			
			solver=new ORASolverByCP(problem);
			
			  solver.solve(false,false); //solver.print();
			  
			  int exeAct3 = solver.getExecutedActivityNumber(); double
			  cost3=solver.getCost(); double runTime3=solver.getTime();
			 
			
			
			  System.out.println(actNum+"\t"+skillLevel+"\t"+exeAct1+"\t"+df.format(cost1)+
			  "\t"+runTime1+"\t"+exeAct2+"\t"+df.format(cost2)+"\t"+
			  runTime2+"\t"+exeAct3+"\t"+df.format(cost3)+"\t"+runTime3);
			 
			
			/*
			 * System.out.println(actNum+"\t"+skillLevel+"\t"+exeAct1+"\t"+df.format(cost1)+
			 * "\t"+runTime1+"\t"+exeAct2+"\t"+df.format(cost2)+"\t"+ runTime2);
			 */
			//System.out.println("----------------------------------------------------");
		}
		
		
		
		
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
