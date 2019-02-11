package graph;

import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Point
{
	private DoubleProperty m_x;
	private DoubleProperty m_y;
	private boolean movable = true;
	
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
	
	
	public void drawOnEdition (javafx.collections.ObservableList<javafx.scene.Node> list)
	{
		list.add(m_c);
	}
	
	
	public void drawEndEdition (javafx.collections.ObservableList<javafx.scene.Node> list)
	{
		list.remove(m_c);
	}
	
	public javafx.scene.shape.Circle getCircle () {return m_c;}
	
	public void setImmovable ()
	{
		if (m_c.onMouseDraggedProperty().get() != null)
			m_c.removeEventHandler(MouseEvent.MOUSE_DRAGGED, m_c.onMouseDraggedProperty().get());
		movable = false;
	}
	
	public void setMovable ()
	{
		movable = true;
	}
	
	void addEventHandler(EventType<MouseEvent> type, EventHandler<MouseEvent> handler)
	{
		if (!movable) return;
		
		m_c.addEventHandler(type, handler);
	}
}
