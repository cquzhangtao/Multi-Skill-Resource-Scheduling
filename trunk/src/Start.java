import problem.OverlappingResAllocProblem;
import solver.AbstractORASlover;
import solver.ORASolverWithAllActivities;
import solver.maxactamount.ORASolverByReducingActivities;
import solver.mincost.pricewithoutprotime.ORASolverByIteratingExchange;

public class Start {

	public static void main(String[] args) {
		OverlappingResAllocProblem problem = ProblemFactory.makeExample();
		//ORASolverWithAllActivities solver=new ORASolverWithAllActivities(problem);		
		//AbstractORASlover solver=new ORASolverByReducingActivities(problem);
		AbstractORASlover solver=new ORASolverByIteratingExchange(problem);
		solver.solve(true,true);
		//solver.print();

	}

}
