import problem.OverlappingResAllocProblem;
import solver.AbstractORASlover;
import solver.ORASolverWithAllActivities;
import solver.maxactamount.ORASolverByReducingActivities;
import solver.mincost.pricewithoutprotime.ORASolverByIteratingExchange;

public class Start {

	public static void main(String[] args) {
		OverlappingResAllocProblem problem = ProblemFactory.makeRandomExample();
		problem.print();
		AbstractORASlover solver;//=new ORASolverWithAllActivities(problem);		
		//solver=new ORASolverByReducingActivities(problem);
		solver=new ORASolverByIteratingExchange(problem);
		solver.solve(true,true);
		//solver.print();

	}

}
