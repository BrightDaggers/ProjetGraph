package graph;

import java.util.ArrayList;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import solvers.Helper;
import solvers.Convex.Rect;

public class GraphBuilder
{
	ArrayList<Rectangle> rs;
	ArrayList<int[]> connect;
	
	public GraphBuilder ()
	{
		rs = new ArrayList<>();
		connect = new ArrayList<>();
	}
	
	public void addRectangle (Point p, DoubleProperty w, DoubleProperty h)
	{
		rs.add(new Rectangle(p, w, h));
	}
	
	public void addRectangle (Point p, DoubleProperty w, DoubleProperty h, DoubleProperty rot)
	{
		rs.add(new Rectangle(p, w, h, rot));
	}
	
	public void addRectangle(double x, double y, double w, double h)
	{
		addRectangle(new Point(x, y), new SimpleDoubleProperty(w), new SimpleDoubleProperty(h));
	}
	
	public void addRectangle(double x, double y, double w, double h, double rot)
	{
		addRectangle(new Point(x, y), new SimpleDoubleProperty(w), new SimpleDoubleProperty(h), new SimpleDoubleProperty(rot));
	}
	
	public void addRectangle(double w, double h)
	{
		addRectangle(new Point(0, 0), new SimpleDoubleProperty(w), new SimpleDoubleProperty(h));
	}
	
	public void addRectangle(double[] pos, double[] size)
	{
		addRectangle(pos[0], pos[1], size[0],size[1]);
	}
	
	public void addRectangle(double[] rect)
	{
		addRectangle(rect[0], rect[1], rect[2],rect[3]);
	}
	
	public void addRectangle(Helper.Vec2 pos, Helper.Vec2 size)
	{
		addRectangle(pos.x, pos.y, size.x, size.y);
	}
	
	public void connect(int id0, int id1)
	{
		connect.add(new int[]{id0, id1});
	}
	
	public Rectangle getRect(int id)
	{
		return rs.get(id);
	}
	
	public Graph toGraph()
	{
		Graph g = new Graph();
		
		for (Rectangle r : rs)
			g.add(r);
		
		for (int[] t : connect)
		{
			Point p1 = new Point(0,0);
			Point p2 = new Point(0,0);
			
			Rectangle r1 = rs.get(t[0]);
			Rectangle r2 = rs.get(t[1]);
			
			r1.addAnchorPoint(p1, r2.center());
			r2.addAnchorPoint(p2, r1.center());
			
			g.add(new Line(p1, p2));
		}
		
		return g;
	}
	
	public ArrayList<Rect> toRectsArray ()
	{
		ArrayList<Rect> rects = new ArrayList<>();
		
		for (Rectangle r : rs)
			rects.add(new Rect(r.w(), r.h()));
		
		for (int[] t : connect)
		{
			Rect r1 = rects.get(t[0]);
			Rect r2 = rects.get(t[1]);
			
			r1.neighbours.add(r2);
			r2.neighbours.add(r1);
		}
		
		return rects;
	}
}
