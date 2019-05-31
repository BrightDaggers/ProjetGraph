package solvers;

import java.util.ArrayList;

import graph.*;
import javafx.application.Platform;

public class GradientDescent extends Solver
{
	public class DRectangle
	{
		public Rectangle r;
		public double dx, dy, dtheta;
		
		public DRectangle(Rectangle r)
		{
			this.r = r;
			dx = 0;
			dy = 0;
			dtheta = 0;
		}
	}
	
	Graph m_graph;
	double m_s;
	ArrayList<Constraint> m_constraints;
	
	public GradientDescent(Graph graph, double step)
	{
		m_graph = graph;
		m_s = step;
		m_constraints = new ArrayList<>();
	}
	
	public void addConstraint(Constraint constraint)
	{
		m_constraints.add(constraint);
		
	}
	
	@Override
	protected void update(double dt)
	{		
		double max = 0;
		for (Constraint c : m_constraints)
		{
			max = Math.max(c.calc_grads(), max);
		}
		
		final double m = max;
		if (m!=0)
		{
			Platform.runLater(() ->
			{
				for (Constraint c : m_constraints)
				{
					c.apply_grads(m_s/m);
				}
			});
		}
		
	}
}
