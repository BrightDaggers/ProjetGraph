package solvers;

import java.util.ArrayList;

import graph.Graph;
import graph.Line;
import graph.Shape;

public class LineLenConstraint extends Constraint
{
	class DLine
	{
		public Line l;
		public double dx1, dy1, dx2, dy2;
		
		public DLine(Line l) {this.l=l; dx1=0; dy1=0; dx2=0; dy2=0;}
	}
	ArrayList<DLine> lines;
	double lambda;
	
	
	public LineLenConstraint(Graph graph, double lambda)
	{
		lines = new ArrayList<>();
		
		for (Shape s : graph.shapes)
		{
			if (s instanceof Line)
				lines.add(new DLine((Line)s));
		}
		
		this.lambda = lambda;
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
		for (DLine l : lines)
		{
			l.dx1 = lambda*(l.l.p2().x() - l.l.p1().x())/2;
			l.dy1 = lambda*(l.l.p2().y() - l.l.p1().y())/2;
			l.dx2 = -l.dx1;
			l.dy2 = -l.dy1;
			gradmax = Math.max(gradmax, l.dx1*l.dx1 + l.dy1*l.dy1);
		}
		
		return Math.sqrt(gradmax);
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
