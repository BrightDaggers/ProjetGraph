package graph;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class Point
{
	private DoubleProperty m_x;
	private DoubleProperty m_y;
	
	private javafx.scene.shape.Circle m_c;
	
	
	public Point(float x, float y)
	{
		m_x = new SimpleDoubleProperty(x);
		m_y = new SimpleDoubleProperty(y);
		
		m_c = new javafx.scene.shape.Circle(5.); // ---------------------------- A voir -------------------------------------
		m_c.setFill(Color.TRANSPARENT);
		m_c.setStroke(Color.BLACK);
		m_c.centerXProperty().bind(m_x);
		m_c.centerYProperty().bind(m_y);
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
	
	public void drawOnEdition (javafx.collections.ObservableList<javafx.scene.Node> list)
	{
		list.add(m_c);
	}
	
	
	public void drawEndEdition (javafx.collections.ObservableList<javafx.scene.Node> list)
	{
		list.remove(m_c);
	}
	
	public javafx.scene.shape.Circle getCircle () {return m_c;}
}
