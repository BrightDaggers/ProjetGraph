package graph;

import javafx.collections.*;
import javafx.scene.*;

public class Edge extends Shape
{
	private Point a;
	private Point b;
	
	public Edge (Point p1, Point p2)
	{
		a = p1;
		b = p2;
	}
	
	public boolean isPart(Point c)
	{
		return a.equals(c) || c.equals(c);
	}
	
	public Point neighboor (Point c)
	{
		if (a.equals(c))
			return b;
		else if (b.equals(c))
			return a;
		else
			return null;
	}
	
	public void draw (ObservableList<Node> list)
	{
		
		
	}
}
