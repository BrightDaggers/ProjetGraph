package graph;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Rectangle extends Shape
{
	private Point p1;
	private Point p2;
	private Point p3;
	private Point p4;
	
	DoubleProperty m_w;
	DoubleProperty m_h;
	
	private javafx.scene.shape.Rectangle r;
	
	private double mouseX;
	private double mouseY;
	
	
	public Rectangle (Point p, DoubleProperty width, DoubleProperty height)
	{
		mouseX = 0;
		mouseY = 0;
		
		r = new javafx.scene.shape.Rectangle();
		m_w = width;
		m_h = height;
		
		r.setFill(Color.TRANSPARENT);
		r.setStroke(Color.BLACK);
		
		
		r.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event)
					{
						Shape.setShapeEdition(Rectangle.this);
						mouseX = event.getX();
						mouseY = event.getY();
					}
	        	}
			);
		
		r.addEventHandler(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event)
					{
						if (Shape.getShapeEdited() == Rectangle.this)
						{
							p1.set(event.getX()-mouseX+p1.x(), event.getY()-mouseY+p1.y());
							
							mouseX = event.getX();
							mouseY = event.getY();
						}
					}
	        	}
			);
		
		genRect(p);
	}
	
	public Point p1() {return p1;}
	public double x () {return p1.x();}
	public double y () {return p1.y();}
	public double w () {return m_w.get();}
	public double h () {return m_h.get();}
	
	
	public void draw (javafx.collections.ObservableList<javafx.scene.Node> list)
	{
		m_list = list;
		list.add(r);
	}
	
	
	public void drawOnEdition ()
	{
		p1.drawOnEdition(m_list);
		p2.drawOnEdition(m_list);
		p3.drawOnEdition(m_list);
		p4.drawOnEdition(m_list);
		
		anchorPoints.forEach((e) -> {e.drawOnEdition(m_list);});
	}
	
	
	public void drawEndEdition ()
	{
		p1.drawEndEdition(m_list);
		p2.drawEndEdition(m_list);
		p3.drawEndEdition(m_list);
		p4.drawEndEdition(m_list);
		
		anchorPoints.forEach((e) -> {e.drawEndEdition(m_list);});
	}
	
	public void addAnchorPoint (Point p)
	{
		double x = (p.x()-p1.x())/m_w.get()-.5,
				y = (p.y()-p1.y())/m_h.get()-.5;
		
		if (y<=-Math.abs(x))
		{
			final double alpha = Math.max(0., Math.min(1., x+.5));
			
			p.xProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.xProperty(), m_w);}
						@Override
						protected double computeValue()
						{ return p1.x() + alpha*m_w.get(); }
					} );
			p.yProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.yProperty());}
						@Override
						protected double computeValue()
						{ return p1.y(); }
					} );
			p.setMoveFnct( (abs,ord) -> { p1.set(abs.doubleValue()-p.x()+p1.x(), ord.doubleValue()-p.y()+p1.y()); } );
		}
		else if (y>=Math.abs(x))
		{
			final double alpha = Math.max(0., Math.min(1., x+.5));
			
			p.xProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.xProperty(), m_w);}
						@Override
						protected double computeValue()
						{ return p1.x() + alpha*m_w.get(); }
					} );
			p.yProperty().bind(
					new DoubleBinding() {
						{super.bind(p3.yProperty());}
						@Override
						protected double computeValue()
						{ return p3.y(); }
					} );
			p.setMoveFnct( (abs,ord) -> { p1.set(abs.doubleValue()-p.x()+p1.x(), ord.doubleValue()-p.y()+p1.y()); } );
		}
		else if (x<Math.abs(y))
		{
			final double alpha = Math.max(0., Math.min(1., y+.5));
			
			p.xProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.xProperty());}
						@Override
						protected double computeValue()
						{ return p1.x(); }
					} );
			p.yProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.yProperty(), m_h);}
						@Override
						protected double computeValue()
						{ return p1.y() + alpha*m_h.get(); }
					} );
			p.setMoveFnct( (abs,ord) -> { p1.set(abs.doubleValue()-p.x()+p1.x(), ord.doubleValue()-p.y()+p1.y()); } );
		}
		else
		{
			final double alpha = Math.max(0., Math.min(1., y+.5));
			
			p.xProperty().bind(
					new DoubleBinding() {
						{super.bind(p2.xProperty());}
						@Override
						protected double computeValue()
						{ return p2.x(); }
					} );
			p.yProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.yProperty(), m_h);}
						@Override
						protected double computeValue()
						{ return p1.y() + alpha*m_h.get(); }
					} );
			p.setMoveFnct( (abs,ord) -> { p1.set(abs.doubleValue()-p.x()+p1.x(), ord.doubleValue()-p.y()+p1.y()); } );
		}
		
		
		p.addEventHandler(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event)
					{
						if (Shape.getShapeEdited() == Rectangle.this)
						{
							p.set(event.getX(), event.getY());
						}
					}
	        	}
			);
		anchorPoints.add(p);
	}
	
	public void rmAnchorPoint (Point p)
	{
		anchorPoints.remove(p);
	}
	
	public void genRect (Point p)
	{
		if (p == null) return;
		
		if (p1 != null   &&   p1.getCircle().onMouseDraggedProperty().get() != null)
			p1.getCircle().removeEventHandler(MouseEvent.MOUSE_DRAGGED, p1.getCircle().onMouseDraggedProperty().get());
		if (p2 != null   &&   p2.getCircle().onMouseDraggedProperty().get() != null)
			p2.getCircle().removeEventHandler(MouseEvent.MOUSE_DRAGGED, p2.getCircle().onMouseDraggedProperty().get());
		if (p3 != null   &&   p3.getCircle().onMouseDraggedProperty().get() != null)
			p3.getCircle().removeEventHandler(MouseEvent.MOUSE_DRAGGED, p3.getCircle().onMouseDraggedProperty().get());
		if (p4 != null   &&   p4.getCircle().onMouseDraggedProperty().get() != null)
			p4.getCircle().removeEventHandler(MouseEvent.MOUSE_DRAGGED, p4.getCircle().onMouseDraggedProperty().get());
		
		p1 = p;
		p2 = new Point(0,0);
		p3 = new Point(0,0);
		p4 = new Point(0,0);
		
		r.xProperty().bind(p1.xProperty());
		r.yProperty().bind(p1.yProperty());
		r.widthProperty().bind(m_w);
		r.heightProperty().bind(m_h);;
		
		p2.xProperty().bind(Bindings.add(p1.xProperty(), m_w));
		p2.yProperty().bind(p1.yProperty());
		p3.xProperty().bind(p1.xProperty());
		p3.yProperty().bind(Bindings.add(p1.yProperty(), m_h));
		p4.xProperty().bind(Bindings.add(p1.xProperty(), m_w));
		p4.yProperty().bind(Bindings.add(p1.yProperty(), m_h));
		
		p2.setMoveFnct((abs,ord) -> { m_w.set(Math.max(0,m_w.get()+abs.doubleValue()-p2.x())); p1.setY(ord.doubleValue()); });
		p3.setMoveFnct((abs,ord) -> { p1.setX(abs.doubleValue()); m_h.set(Math.max(0,m_h.get()+ord.doubleValue()-p3.y())); });
		p4.setMoveFnct((abs,ord) -> { m_w.set(Math.max(0,m_w.get()+abs.doubleValue()-p4.x())); m_h.set(Math.max(0,m_h.get()+ord.doubleValue()-p4.y())); });
		
		p1.addEventHandler(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event)
					{
						if (Shape.getShapeEdited() == Rectangle.this)
						{
							p1.set(event.getX(), event.getY());
						}
					}
	        	}
			);
		p2.addEventHandler(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event)
			{
				if (Shape.getShapeEdited() == Rectangle.this)
				{
					p2.set(event.getX(), event.getY());
				}
			}
		}
				);
		p3.addEventHandler(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event)
			{
				if (Shape.getShapeEdited() == Rectangle.this)
				{
					p3.set(event.getX(), event.getY());
				}
			}
		}
				);
		p4.addEventHandler(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event)
			{
				if (Shape.getShapeEdited() == Rectangle.this)
				{
					p4.set(event.getX(), event.getY());
				}
			}
		}
				);
	}
}
