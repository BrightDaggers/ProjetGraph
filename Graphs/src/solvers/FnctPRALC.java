package solvers;

import java.util.ArrayList;

import graph.*;

public class FnctPRALC extends FitnessFnct// Positions, Rotations, Angles, Lengths, Crossings
{	
	Graph m_g;
	ArrayList<Rectangle> m_rectangles; // Position & rotation
	ArrayList<Line> m_lines; // Angle & crossing
	
	
	public FnctPRALC(Graph graph)
	{
		m_g = graph;
		update();
	}
	
	public void update()
	{
		m_rectangles = new ArrayList<>();
		m_lines = new ArrayList<>();
		
		for (Shape s : m_g.shapes)
		{
			if (s instanceof Rectangle)
				m_rectangles.add((Rectangle)s);
			else if (s instanceof Line)
				m_lines.add((Line)s);
		}
	}
	
	@Override
	public double calc()
	{
		double value = 0;
		
		for (Rectangle r : m_rectangles)
		{
			value += Helper.normalizeAngle(r.theta());
			
			for (Rectangle r2 : m_rectangles)
			{
				if (!r2.equals(r))
				{
					value += Helper.interpenetration(r, r2).len();
				}
			}
		}
		
		for (Line l : m_lines)
		{
			value += Math.sqrt(Math.pow(l.p1().x()-l.p2().x(),2) + Math.pow(l.p1().y()-l.p2().y(),2));
			
			if (l.p1().getParents()!=null)
			{
				value += Helper.angle(l.p1().getParents(), l);
			}
			if (l.p2().getParents()!=null)
			{
				value += Helper.angle(l.p2().getParents(), l);
			}
			
			for (Line l2 : m_lines)
			{
				if (!l.equals(l2))
				{
					value += Helper.isCrossing(l, l2).len();
				}
			}
		}
		
		return value;
	}

}
