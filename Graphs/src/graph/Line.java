package graph;

public class Line extends Shape
{
	private Point a;
	private Point b;
	private javafx.scene.shape.Line l;
	
	public Line (Point p1, Point p2)
	{
		a = p1;
		b = p2;
		l = new javafx.scene.shape.Line(p1.x, p1.y, p2.x, p2.y);
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
	
	public void draw (javafx.collections.ObservableList<javafx.scene.Node> list)
	{
		list.add(l);
	}
}
