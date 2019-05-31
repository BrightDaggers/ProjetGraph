package solvers;

import java.util.ArrayList;

import graph.Graph;
import graph.Rectangle;
import graph.Shape;

public class RectPenConstraint extends Constraint
{
	
	class DRect
	{
		public Rectangle r;
		public double dx, dy;
		
		public DRect(Rectangle r) {this.r=r; dx=0; dy=0;}
	}
	ArrayList<DRect> rects;
	double lambda;
	
	
	public RectPenConstraint (Graph graph, double lambda)
	{
		rects = new ArrayList<>();
		this.lambda = lambda;
		
		for (Shape s : graph.shapes)
		{
			if (s instanceof Rectangle)
				rects.add(new DRect((Rectangle)s));
		}
	}
	

	@Override
	public double calc_grads()
	{
		for (DRect r : rects)
		{			
			r.dx = 0;
			r.dy = 0;
		}
		
		
		double gradmax = 0;
		for (DRect r : rects)
		{			
			for (DRect r2 : rects)
			{
				if (!r2.equals(r))
				{
					Helper.Vec2 v = Helper.interpenetration(r.r, r2.r);
					r.dx += lambda*v.x/2;
					r.dy += lambda*v.y/2;
					r2.dx -= lambda*v.x/2;
					r2.dy -= lambda*v.y/2;
					
					gradmax = Math.max(gradmax, v.x*v.x + v.y*v.y);
				}
			}
		}
		return lambda*Math.sqrt(gradmax);
	}

	@Override
	public void apply_grads(double coef)
	{
		for (DRect r : rects)
		{
			r.r.center().set(r.r.center().x() + r.dx*coef, r.r.center().y() + r.dy*coef);
		}
	}

}
