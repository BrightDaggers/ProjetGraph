package graph;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Line extends Shape
{
	private Point a;
	private Point b;
	
	private javafx.scene.shape.Line l;
	private javafx.scene.shape.Line clickBox;
	
	private javafx.scene.shape.Circle ca;
	private javafx.scene.shape.Circle cb;
	
	
	public Line (Point p1, Point p2)
	{
		a = p1;
		b = p2; //test
	
		
		createLine();
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
		m_list = list;
		list.add(l);
		list.add(clickBox);
	}
	
	
	public void drawOnEdition ()
	{
		m_list.add(ca);
		m_list.add(cb);
	}
	
	
	public void drawEndEdition ()
	{
		m_list.remove(ca);
		m_list.remove(cb);
	}
	
	
	private void createLine()
	{

		l = new javafx.scene.shape.Line();
		clickBox = new javafx.scene.shape.Line();
		
		l.startXProperty().bind(a.xProperty());
		l.startYProperty().bind(a.yProperty());
		l.endXProperty().bind(b.xProperty());
		l.endYProperty().bind(b.yProperty());
		
		clickBox.startXProperty().bind(a.xProperty());
		clickBox.startYProperty().bind(a.yProperty());
		clickBox.endXProperty().bind(b.xProperty());
		clickBox.endYProperty().bind(b.yProperty());
		
		clickBox.strokeWidthProperty().set(20.); //------------------------------------------------------- A voir -----------------------------------------------
		clickBox.setStroke(Color.TRANSPARENT);
		
		ca = new javafx.scene.shape.Circle(5.);
		cb = new javafx.scene.shape.Circle(5.);
		
		ca.centerXProperty().bind(a.xProperty());
		ca.centerYProperty().bind(a.yProperty());
		cb.centerXProperty().bind(b.xProperty());
		cb.centerYProperty().bind(b.yProperty());
		
		
		clickBox.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event)
					{
						
						Shape.setShapeEdition(Line.this);
					}
	        	}
			);
		
		ca.addEventHandler(MouseEvent.MOUSE_DRAGGED,
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
		
		cb.addEventHandler(MouseEvent.MOUSE_DRAGGED,
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
