package graph;

import javafx.beans.property.*;

public class Point
{
	private DoubleProperty m_x;
	private DoubleProperty m_y;
	
	
	public Point(float x, float y)
	{
		m_x = new SimpleDoubleProperty(x);
		m_y = new SimpleDoubleProperty(y);
	}
	
	
	public boolean equals(Point o)
	{
		if (o==null) return false;
		
		return m_x.get()==o.m_x.get() && m_y.get()==o.m_y.get();	
	}
	
	
	public DoubleProperty xProperty() {return m_x;}
	public DoubleProperty yProperty() {return m_y;}
	
	
	public double x() {return m_x.get();}
	public double y() {return m_y.get();}
	
	
	public void setX(double x) {m_x.set(x);}
	public void setY(double y) {m_y.set(y);}
	
	
	public double distance2(double x, double y)
	{
		return (x()-x)*(x()-x) + (y()-y)*(y()-y);
	}
}
