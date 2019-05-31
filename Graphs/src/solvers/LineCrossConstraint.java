package solvers;

import java.util.ArrayList;

import graph.Graph;
import graph.Line;
import graph.Shape;

public class LineCrossConstraint extends Constraint
{
	class DLine
	{
		public Line l;
		public double dx1, dy1, dx2, dy2;
		
		public DLine(Line l) {this.l=l; dx1=0; dy1=0; dx2=0; dy2=0;}
	}
	ArrayList<DLine> lines;
	double lambda;
	
	
	public LineCrossConstraint(Graph graph, double lambda)
	{
		lines = new ArrayList<>();
		this.lambda = lambda;
		
		for (Shape s : graph.shapes)
		{
			if (s instanceof Line)
				lines.add(new DLine((Line)s));
		}
	}

	
	@Override
	public double calc_grads()
	{
		for (DLine l : lines)
		{
			l.dx1 = 0; l.dx2 = 0;
			l.dy1 = 0; l.dy2 = 0;
		}
		
		double gradmax = 0;
		for (DLine l1 : lines)
		{
			for (DLine l2 : lines)
			{
				if (!l1.equals(l2))
				{
					Helper.Vec2 v = Helper.isCrossing(l1.l, l2.l);
					
					l1.dx1 += lambda*v.x;	l1.dx2 += lambda*v.x;
					l1.dy1 += lambda*v.y;	l1.dy2 += lambda*v.y;
					
					l2.dx1 -= lambda*v.x;	l2.dx2 -= lambda*v.x;
					l2.dy1 -= lambda*v.y;	l2.dy2 -= lambda*v.y;
					
					gradmax = Math.max(gradmax, v.x*v.x+v.y*v.y);
				}
			}
			
		}
		
		return lambda*Math.sqrt(gradmax);
	}

	
	@Override
	public void apply_grads(double coef)
	{
		for (DLine l : lines)
		{
			l.l.p1().set(l.l.p1().x() + coef*l.dx1, l.l.p1().y() + coef*l.dy1);
			l.l.p2().set(l.l.p2().x() + coef*l.dx2, l.l.p2().y() + coef*l.dy2);
		}
	}

}
