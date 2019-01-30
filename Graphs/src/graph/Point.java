package graph;

public class Point
{
	// to change ?
	public final float x;
	public final float y;
	
	public Point(float _x, float _y)
	{
		x = _x;
		y = _y;
	}
	
	public boolean equals(Point o)
	{
		if (o==null) return false;
		
		return x==o.x && y==o.y;	
	}
}
