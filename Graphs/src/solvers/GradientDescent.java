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
	ArrayList<DRectangle> m_rectangles;
	double maxGrad;
	
	FitnessFnct m_f;
	
	public GradientDescent(Graph graph, double step, FitnessFnct fnct)
	{
		m_graph = graph;
		m_s = step;
		m_f = fnct;
		m_rectangles = new ArrayList<>();
		
		for (Shape s : m_graph.shapes)
		{
			if (s instanceof Rectangle)
				m_rectangles.add(new DRectangle((Rectangle)s));
		}
	}
	
	@Override
	protected void update(double dt)
	{
		double v = m_f.calc();
		maxGrad = 0;

		Platform.runLater(() -> {
			for (DRectangle r : m_rectangles)
			{
				r.r.center().setX(r.r.center().x() + m_s/6f);
				r.dx = m_f.calc() - v;
				if (Math.abs(r.dx)>maxGrad) maxGrad = Math.abs(r.dx);
				r.r.center().setX(r.r.center().x() - m_s/6f);
				
				r.r.center().setY(r.r.center().y() + m_s/6f);
				r.dy = m_f.calc() - v;
				if (Math.abs(r.dy)>maxGrad) maxGrad = Math.abs(r.dy);
				r.r.center().setY(r.r.center().y() - m_s/6f);
				
				r.r.thetaProperty().set(r.r.theta() + m_s/60);
				r.dtheta = m_f.calc() - v;
				if (Math.abs(r.dtheta)>maxGrad) maxGrad = Math.abs(r.dtheta);
				r.r.thetaProperty().set(r.r.theta() - m_s/60);
			}
			for (DRectangle r : m_rectangles)
			{
				r.r.center().setX(r.r.center().x() - m_s*r.dx*10/maxGrad);
				r.r.center().setY(r.r.center().y() - m_s*r.dy*10/maxGrad);
				r.r.thetaProperty().set(r.r.theta() - m_s*r.dtheta/maxGrad/10);
			}
		});
	}

}
