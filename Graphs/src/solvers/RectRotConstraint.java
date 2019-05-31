package solvers;

import java.util.ArrayList;

import graph.Graph;
import graph.Rectangle;
import graph.Shape;

public class RectRotConstraint extends Constraint
{
	
	class DRect
	{
		public Rectangle r;
		public double dtheta;
		
		public DRect(Rectangle r) {this.r=r; dtheta=0;}
	}
	ArrayList<DRect> rects;
	double lambda;
	
	
	public RectRotConstraint (Graph graph, double lambda)
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
			r.dtheta = 0;
		}
		
		
		double gradmax = 0;
		for (DRect r : rects)
		{
			double x = Helper.normalizeAngle(r.r.theta());
			
			r.dtheta = lambda * x<0? x<-Math.PI/2? -Math.PI-x : -x : x>Math.PI/2? Math.PI-x : -x;
			
			gradmax = Math.max(gradmax, Math.abs(r.dtheta));
		}
		return gradmax;
	}

	@Override
	public void apply_grads(double coef)
	{
		for (DRect r : rects)
		{
			r.r.thetaProperty().set(Helper.normalizeAngle(r.r.theta()+coef*r.dtheta));
		}
	}

}
