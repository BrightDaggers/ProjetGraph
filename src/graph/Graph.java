package graph;

import java.awt.*;
import java.util.*;

public class Graph
{
	private static final int VSIZE = 1024;
	
	private Point vertices[];
	private ArrayList<Integer> vindfree;
	private ArrayList<Edge> edges;
	
	
	public Graph ()
	{
		vertices = new Point[VSIZE];
		edges = new ArrayList<>();
		
		vindfree = new ArrayList<>();
		for (int i=0; i<1024; i++)
		{
			vindfree.add(Integer.valueOf(i));
		}
	}
	
	
	public boolean add (Point p)
	{
		if (vindfree.size()==0)
			return false;
		
		int i = vindfree.remove(0).intValue();
		vertices[i] = p;
		
		return true;
	}
	
	public boolean add (Edge e)
	{
		edges.add(e);
		return true;
	}
	
	
	public void draw (Graphics _g)
	{
		Graphics2D g = (Graphics2D)_g;
		
		g.setColor(Color.blue);
		g.fillOval(98, 98, 5, 5);
		g.fillOval(98, 198, 5, 5);
		
		g.setColor(Color.black);
		g.drawLine(100, 100, 100, 200);
	}
}
