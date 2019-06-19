
package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import model.Model;

/**
 * This class is a list used to describe an overlapping graph which includes
 * many graphic elements.
 * 
 * @author Shufang Xie, Tao Zhang
 * 
 */
public class GraphicSpace extends HashMap<String, GraphElement> {
	
	private static final long serialVersionUID = 1L;
	
	private Model problem;
	
	
	public GraphicSpace(Model problem) {
		this.setProblem(problem);
	}
	
	/**
	 * To obtain an elements with the highest ratio in the graph.
	 * 
	 * @return An elements with the highest ratio in the graph
	 */
	
	
	
	public GraphElement getMostUrgentGraphElement() {
	
		GraphElement maxRQ = null;
		double max = Double.MIN_VALUE;
		for (GraphElement rq : this.values()) {
			if (!rq.isAssignedResource()) {
				double ratio = rq.getUrgentRatio();
				if (ratio > max) {
					max = ratio;
					maxRQ = rq;
				}
			}
			
		}
		return maxRQ;
		
	}
	public GraphElement getLeastResourceGraphElement() {
		GraphElement minRQ=null;
		double min=Double.MAX_VALUE;
		for(GraphElement rq:this.values()) {
			if(!rq.isAssignedResource()) {
				double ratio=problem.getQualifications().get(rq.getQualification()).getResources().size();
				if(ratio< min) {
					min=ratio;
					minRQ=rq;
				}
			}
		}
		return minRQ;
	}
	public GraphElement getLeastUrgentGraphElement() {
		GraphElement minRQ=null;
		double min=Double.MAX_VALUE;
		for(GraphElement rq:this.values()) {
			if(!rq.isAssignedResource()) {
				double ratio=rq.getUrgentRatio();
				if(ratio< min) {
					min=ratio;
					minRQ=rq;
				}
			}
		}
		return minRQ;
		
	}
	public GraphElement getRandomGraphElement() {
		Random ran=new Random();
		List<GraphElement> list=new ArrayList<GraphElement>();
		for(GraphElement ele:this.values()) {
			if(!ele.isAssignedResource()) {
				list.add(ele);
			}
		}
if(list.isEmpty()) {
			return null;
		}
		GraphElement rq = list.get(ran.nextInt(list.size()));
		
		return rq;
		
		
		
		
	}
	
	public GraphElement getMostUrgentGraphElement1() {
		
		GraphElement maxRQ = null;
		double max = Double.MIN_VALUE;
		for (GraphElement rq : this.values()) {
			if (!rq.isAssignedResource()) {
				double ratio = rq.getUrgentRatio();
				if (ratio > max) {
					max = ratio;
					maxRQ = rq;
				}
			}
			
		}
		return maxRQ;
		
	}
	
	public void updateRatio() {
	
		for (GraphElement rq : this.values()) {
			rq.updateRatio();
		}
		
	}
	
	/**
	 * The graph changes when we solve the problem. If we want to resolve the
	 * problem, we have to reset the graph. Attention: do not combine these two
	 * loops together.
	 */
	public void reset() {
	
		for (GraphElement rq : this.values()) {
			rq.reset();
		}
		for (GraphElement rq : this.values()) {
			rq.updateGraphElementRelation(this);
		}
	}
	
	public void updateRelation() {
	
		for (GraphElement rq : this.values()) {
			rq.updateGraphElementRelation(this);
		}
	}
	
	public void update() {
	
		for (GraphElement rq : this.values()) {
			rq.updateRatio();
			rq.updateGraphElementRelation(this);
		}
	}
	
	/**
	 * Please keep this function here in case we need to debug later.
	 */
	public void print() {
	
		String str = "            ";
		for (GraphElement rq : this.values()) {
			str += rq.getQualification();
			for (int i = 0; i < 10 - rq.getQualification().length(); i++) {
				str += " ";
			}
		}
		for (int i = 0; i < str.length(); i++) {
			System.out.print("=");
		}
		System.out.println();
		System.out.println("Combined activities");
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.println(str);
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.print("Required    ");
		for (GraphElement rq : this.values()) {
			
			System.out.print(rq.getRequiredResNum());
			for (int i = 0; i < 10 - String.valueOf(rq.getRequiredResNum()).length(); i++) {
				System.out.print(" ");
			}
			
		}
		System.out.println();
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.print("Total       ");
		
		for (GraphElement rq : this.values()) {
			
			System.out.print(rq.getTotalResNum());
			for (int i = 0; i < 10 - String.valueOf(rq.getTotalResNum()).length(); i++) {
				System.out.print(" ");
			}
			
		}
		System.out.println();
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.print("Ratio       ");
		for (GraphElement rq : this.values()) {
			double d = ((double) rq.getRequiredResNum() / (double) rq.getTotalResNum());
			System.out.print(String.format("%.2f", d));
			for (int i = 0; i < 10 - String.valueOf(String.format("%.2f", d)).length(); i++) {
				System.out.print(" ");
			}
			
		}
		System.out.println();
		for (int i = 0; i < str.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		
	}

	public Model getProblem() {
		return problem;
	}

	public void setProblem(Model problem) {
		this.problem = problem;
	}
	
}
