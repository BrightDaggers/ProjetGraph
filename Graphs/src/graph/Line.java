package graph;

import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
		super(FLAGS.CONNECTOR);
		l = new javafx.scene.shape.Line();
		clickBox = new javafx.scene.shape.Line();
		
		clickBox.strokeWidthProperty().set(20.); //------------------------------------------------------- A voir -----------------------------------------------
		clickBox.setStroke(Color.TRANSPARENT);
		
		newPointA(p1);
		newPointB(p2);
	}
	
	
	public boolean isPart(Point c)
	{
		return a.equals(c) || b.equals(c);
	}
	
	
	public Point p1() {return a;}
	public Point p2() {return b;}
	
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
		
		addAnchorPoint(p, alpha);
	}
	
	public void addAnchorPoint (Point p, double alpha)
	{		
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
		p.setMoveFnct(
				(abs,ord)
					->
				{
					double dx = abs.doubleValue()-p.x(),
							dy = ord.doubleValue()-p.y();
					
					a.set(dx+a.x(), dy+a.y());
					b.set(dx+b.x(), dy+b.y());
				}
		);
		p.setParents(this);
		anchorPoints.add(p);
	}
	
	public void rmAnchorPoint (Point p)
	{
		anchorPoints.remove(p);
	}
	
	public void newPointA (Point p)
	{
		if (p == null) return;
		
		a = p;
		
		l.startXProperty().bind(a.xProperty());
		l.startYProperty().bind(a.yProperty());
		clickBox.startXProperty().bind(a.xProperty());
		clickBox.startYProperty().bind(a.yProperty());
	}
	
	public void newPointB (Point p)
	{
		if (p == null) return;
		
		b = p;
		
		l.endXProperty().bind(b.xProperty());
		l.endYProperty().bind(b.yProperty());
		clickBox.endXProperty().bind(b.xProperty());
		clickBox.endYProperty().bind(b.yProperty());
	}


	@Override
	public void addEventHandler(EventType<MouseEvent> type, EventHandler<MouseEvent> handler)
	{
		clickBox.addEventHandler(type, handler);
	}
	
	@Override
	public void setFill (Color color)
	{
		l.setFill(color);
	}
}
