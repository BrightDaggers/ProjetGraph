package solvers;

import java.util.ArrayList;

import graph.Graph;
import graph.Rectangle;
import graph.Shape;

public class BubbleConstraint extends Constraint
{
	class DRect
	{
		public Rectangle r;
		public double dx, dy;
		public double l;
		
		public DRect(Rectangle r) {this.r=r; dx=0; dy=0; l=Math.sqrt(r.w()*r.w()+r.h()*r.h());}
	}
	ArrayList<DRect> rects;
	double lambda, coef;
	
	
	public BubbleConstraint (Graph graph, double lambda, double size)
	{
		rects = new ArrayList<>();
		this.lambda = lambda;
		coef = size;
		
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
			if (r.l*r.l != r.r.w()*r.r.w()+r.r.h()*r.r.h())
				r.l = Math.sqrt(r.r.w()*r.r.w()+r.r.h()*r.r.h());
		}
		
		
		double gradmax = 0;
		for (DRect r1 : rects)
		{
			for (DRect r2 : rects)
			{
				if (!r1.equals(r2))
				{
					double ex = r1.r.x() - r2.r.x();
					double ey = r1.r.y() - r2.r.y();
					
					if (ex*ex+ey*ey < coef*coef*(r1.l+r2.l)*(r1.l+r2.l))
					{
						double l = Math.sqrt(ex*ex + ey*ey);
						
						r1.dx += lambda*(coef*(r1.l+r2.l)/l - 1)*ex;
						r1.dy += lambda*(coef*(r1.l+r2.l)/l - 1)*ey;
						r2.dx -= lambda*(coef*(r1.l+r2.l)/l - 1)*ex;
						r2.dy -= lambda*(coef*(r1.l+r2.l)/l - 1)*ey;
						
						if (((r1.l+r2.l)/l - 1)*l > gradmax)
							gradmax = (coef*(r1.l+r2.l)/l - 1)*l;
					}
				}
			}
		}
		
		return lambda*gradmax;
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
