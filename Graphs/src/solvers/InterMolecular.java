package solvers;

import java.util.ArrayList;

import graph.*;
import javafx.application.Platform;

public class InterMolecular extends Solver
{
	class Data
	{
		public Rectangle m_r;
		public double m_x, m_y, m_vx, m_vy, m_fx, m_fy, m_d;
		public static final double a = 1.4;
		
		Data(Rectangle r)
		{
			m_r = r;
			m_x = rX();
			m_y = rY();
			m_vx = 0.;
			m_vy = 0.;
			m_fx = 0.;
			m_fy = 0.;
			m_d = a*Math.sqrt(m_r.w()*m_r.w() + m_r.h()+m_r.h());
		}
		
		void update ()
		{
			m_fx = 0.;
			m_fy = 0.;
			if (rX()!=m_x || rY()!=m_y)
			{
				m_x = rX();
				m_y = rY();
				m_vx = 0.;
				m_vy = 0.;
			}
			if ((m_r.w()*m_r.w() + m_r.h()+m_r.h())*a*a != m_d*m_d)
				m_d = Math.sqrt(m_r.w()*m_r.w() + m_r.h()+m_r.h());
		}
		
		double rX () {return m_r.x() + m_r.w()/2.;}
		double rY () {return m_r.y() + m_r.h()/2.;}
		
		void apply (double dt)
		{
			// add f (v)
			m_fx -= 0.7*m_vx;
			m_fy -= 0.7*m_vy;
			m_vx += dt*m_fx;
			m_vy += dt*m_fy;
			m_x += dt*m_vx;
			m_y += dt*m_vy;
			
			m_r.p1().set(m_x-m_r.w()/2., m_y-m_r.h()/2.);
		}
		
		boolean neq(Data d)
		{
			return m_x!=d.m_x || m_y!=d.m_y;
		}
		
		void addForce(Data d)
		{
			double x = d.m_x - m_x;
			double y = d.m_y - m_y;
			double r = x*x + y*y;
			double f = Math.pow(m_d+d.m_d, 6)/Math.pow(r, 4) * (Math.pow(m_d+d.m_d, 6)/Math.pow(r, 3) - 1);
			m_fx += f * x;
			m_fy += f * y;
		}
	}
	
	Graph m_graph;
	ArrayList<Data> points;
	
	
	public InterMolecular(Graph graph)
	{
		m_graph = graph;
		points = new ArrayList<>();
		
		m_graph.shapes.forEach(
			(e) -> {if (e instanceof Rectangle) points.add(new Data((Rectangle)e));}
		);
	}

	@Override
	protected void update(double dt)
	{
		points.forEach((e)-> {e.update();});
		
		points.forEach((e) ->
			{
				points.forEach((f) -> {if (e.neq(f)) e.addForce(f);});
			}
		);
		
		Platform.runLater(() -> {points.forEach((e) -> {e.apply(dt);});});
	}
}