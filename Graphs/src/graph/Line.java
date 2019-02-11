package graph;

import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Line extends Shape
{
	private Point a;
	private Point b;
	
	private javafx.scene.shape.Line l;
	private javafx.scene.shape.Line clickBox;
	
	
	public Line (Point p1, Point p2)
	{
		l = new javafx.scene.shape.Line();
		clickBox = new javafx.scene.shape.Line();
		
		clickBox.strokeWidthProperty().set(20.); //------------------------------------------------------- A voir -----------------------------------------------
		clickBox.setStroke(Color.TRANSPARENT);
		
		
		clickBox.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event)
					{
						Shape.setShapeEdition(Line.this);
					}
	        	}
			);
		
		newPointA(p1);
		newPointB(p2);
	}
	
	
	public boolean isPart(Point c)
	{
		return a.equals(c) || b.equals(c);
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
		m_list = list;
		list.add(l);
		list.add(clickBox);
	}
	
	
	public void drawOnEdition ()
	{
		a.drawOnEdition(m_list);
		b.drawOnEdition(m_list);
		
		anchorPoints.forEach((e) -> {e.drawOnEdition(m_list);});
	}
	
	
	public void drawEndEdition ()
	{
		a.drawEndEdition(m_list);
		b.drawEndEdition(m_list);
		
		anchorPoints.forEach((e) -> {e.drawEndEdition(m_list);});
	}
	
	public void addAnchorPoint (Point p)
	{
		final double alpha = Math.max(0., Math.min(1., 
				((p.x()-a.x())*(b.x()-a.x())+(p.y()-a.y())*(b.y()-a.y()))
					/
				((b.x()-a.x())*(b.x()-a.x())+(b.y()-a.y())*(b.y()-a.y()))
		));
		
		p.xProperty().bind(
				new DoubleBinding() {
					{super.bind(a.xProperty(), b.xProperty());}
					
					@Override
					protected double computeValue()
					{
						return (1-alpha)*a.x() + alpha*b.x();
					}
				}
		);
		
		p.yProperty().bind(
				new DoubleBinding() {
					{super.bind(a.yProperty(), b.yProperty());}
					
					@Override
					protected double computeValue()
					{
						return (1-alpha)*a.y() + alpha*b.y();
					}
				}
		);
		p.setImmovable();
		anchorPoints.add(p);
	}
	
	public void rmAnchorPoint (Point p)
	{
		anchorPoints.remove(p);
	}
	
	public void newPointA (Point p)
	{
		if (p == null) return;
		
		if (a != null   &&   a.getCircle().onMouseDraggedProperty().get() != null)
			a.getCircle().removeEventHandler(MouseEvent.MOUSE_DRAGGED, a.getCircle().onMouseDraggedProperty().get());
		
		a = p;
		
		l.startXProperty().bind(a.xProperty());
		l.startYProperty().bind(a.yProperty());
		clickBox.startXProperty().bind(a.xProperty());
		clickBox.startYProperty().bind(a.yProperty());
		
		a.addEventHandler(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event)
					{
						if (Shape.getShapeEdited() == Line.this)
						{
							a.setX(event.getX());
							a.setY(event.getY());
						}
					}
	        	}
			);
	}
	
	public void newPointB (Point p)
	{
		if (p == null) return;
		
		if (b != null   &&   b.getCircle().onMouseDraggedProperty().get() != null)
			b.getCircle().removeEventHandler(MouseEvent.MOUSE_DRAGGED, b.getCircle().onMouseDraggedProperty().get());
		
		b = p;
		
		l.endXProperty().bind(b.xProperty());
		l.endYProperty().bind(b.yProperty());
		clickBox.endXProperty().bind(b.xProperty());
		clickBox.endYProperty().bind(b.yProperty());
		
		b.addEventHandler(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event)
					{
						if (Shape.getShapeEdited() == Line.this)
						{
							b.setX(event.getX());
							b.setY(event.getY());
						}
					}
	        	}
			);
	}
}
