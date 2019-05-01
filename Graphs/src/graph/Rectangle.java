package graph;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Rectangle extends Shape
{
	private Point center;
	
	private Point p1;
	private Point p2;
	private Point p3;
	private Point p4;
	
	DoubleProperty m_theta;
	DoubleProperty m_w;
	DoubleProperty m_h;
	
	
	private javafx.scene.shape.Rectangle r;
	
	public Rectangle (Point p, DoubleProperty width, DoubleProperty height)
	{
		r = new javafx.scene.shape.Rectangle();
		m_w = width;
		m_h = height;
		m_theta = new SimpleDoubleProperty(0f);
		
		r.setFill(Color.TRANSPARENT);
		r.setStroke(Color.BLACK);
		
		genRect(p);
	}
	
	
	public Rectangle (Point p, DoubleProperty width, DoubleProperty height, DoubleProperty theta)
	{
		r = new javafx.scene.shape.Rectangle();
		m_w = width;
		m_h = height;
		m_theta = theta;
		
		r.setFill(Color.TRANSPARENT);
		r.setStroke(Color.BLACK);
		
		genRect(p);
	}
	
	public boolean isConnected(Rectangle r) // A faire
	{
		return false;
	}
	
	public Point center() {return center;}
	public Point p1() {return p1;}
	public Point p2() {return p2;}
	public Point p3() {return p3;}
	public Point p4() {return p4;}
	public double x () {return p1.x();}
	public double y () {return p1.y();}
	public double w () {return m_w.get();}
	public double h () {return m_h.get();}
	public double theta () {return m_theta.get();}
	
	public DoubleProperty thetaProperty () {return m_theta;}
	
	public void draw (javafx.collections.ObservableList<javafx.scene.Node> list)
	{
		m_list = list;
		list.add(r);
		
		p1.drawOnEdition(m_list);
		p2.drawOnEdition(m_list);
		p3.drawOnEdition(m_list);
		p4.drawOnEdition(m_list);
		
		anchorPoints.forEach((e) -> {e.drawOnEdition(m_list);});
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
		double tmpx = p.x()-center.x(),
				tmpy = p.y()-center.y();
		double x = (tmpx*Math.cos(m_theta.get())+tmpy*Math.sin(m_theta.get()))/m_w.get(),
				y = (-tmpx*Math.sin(m_theta.get())+tmpy*Math.cos(m_theta.get()))/m_h.get();
		
		if (y<=-Math.abs(x))
		{
			final double alpha = Math.max(0., Math.min(1., x+.5));
			
			p.xProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.xProperty(), p2.xProperty());}
						@Override
						protected double computeValue()
						{ return (1-alpha)*p1.x() + alpha*p2.x(); }
					} );
			p.yProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.yProperty(),p2.yProperty());}
						@Override
						protected double computeValue()
						{ return (1-alpha)*p1.y() + alpha*p2.y(); }
					} );
			p.setMoveFnct( (abs,ord) -> { center.set(abs.doubleValue()-p.x()+center.x(), ord.doubleValue()-p.y()+center.y()); } );
			p.setParents(new Line(p1, p2));
		}
		else if (y>=Math.abs(x))
		{
			final double alpha = Math.max(0., Math.min(1., x+.5));
			
			p.xProperty().bind(
					new DoubleBinding() {
						{super.bind(p3.xProperty(), p4.xProperty());}
						@Override
						protected double computeValue()
						{ return (1-alpha)*p3.x() + alpha*p4.x(); }
					} );
			p.yProperty().bind(
					new DoubleBinding() {
						{super.bind(p3.yProperty(), p4.yProperty());}
						@Override
						protected double computeValue()
						{ return (1-alpha)*p3.y() + alpha*p4.y(); }
					} );
			p.setMoveFnct( (abs,ord) -> { center.set(abs.doubleValue()-p.x()+center.x(), ord.doubleValue()-p.y()+center.y()); } );
			p.setParents(new Line(p3, p4));
		}
		else if (x>Math.abs(y))
		{
			final double alpha = Math.max(0., Math.min(1., y+.5));
			
			p.xProperty().bind(
					new DoubleBinding() {
						{super.bind(p2.xProperty(), p4.xProperty());}
						@Override
						protected double computeValue()
						{ return (1-alpha)*p2.x() + alpha*p4.x(); }
					} );
			p.yProperty().bind(
					new DoubleBinding() {
						{super.bind(p2.yProperty(), p4.yProperty());}
						@Override
						protected double computeValue()
						{ return (1-alpha)*p2.y() + alpha*p4.y(); }
					} );
			p.setMoveFnct( (abs,ord) -> { center.set(abs.doubleValue()-p.x()+center.x(), ord.doubleValue()-p.y()+center.y()); } );
			p.setParents(new Line(p2, p4));
		}
		else
		{
			final double alpha = Math.max(0., Math.min(1., y+.5));
			
			p.xProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.xProperty(), p3.xProperty());}
						@Override
						protected double computeValue()
						{ return (1-alpha)*p1.x() + alpha*p3.x(); }
					} );
			p.yProperty().bind(
					new DoubleBinding() {
						{super.bind(p1.yProperty(), p3.yProperty());}
						@Override
						protected double computeValue()
						{ return (1-alpha)*p1.y() + alpha*p3.y(); }
					} );
			p.setMoveFnct( (abs,ord) -> { center.set(abs.doubleValue()-p.x()+center.x(), ord.doubleValue()-p.y()+center.y()); } );

			p.setParents(new Line(p1, p3));
		}

		anchorPoints.add(p);
	}
	
	public void rmAnchorPoint (Point p)
	{
		anchorPoints.remove(p);
	}
	
	public void genRect (Point p)
	{
		if (p == null) return;
		
		center = p;
		
		p1 = new Point(0,0);
		p2 = new Point(0,0);
		p3 = new Point(0,0);
		p4 = new Point(0,0);
		
		r.xProperty().bind(new DoubleBinding() {
			{super.bind(center.xProperty(),m_w);}
			@Override
			protected double computeValue()
				{return center.x()-m_w.get()/2;}
		});
		r.yProperty().bind(new DoubleBinding() {
			{super.bind(center.yProperty(),m_h);}
			@Override
			protected double computeValue()
				{return center.y()-m_h.get()/2;}
		});
		
		r.widthProperty().bind(m_w);
		r.heightProperty().bind(m_h);
		r.rotateProperty().bind(Bindings.multiply(m_theta,180/Math.PI));
		
		// p1
		p1.xProperty().bind(new DoubleBinding() {
			{super.bind(center.xProperty(),m_w,m_h,m_theta);}
			@Override
			protected double computeValue()
				{return center.x()+(-m_w.get()*Math.cos(m_theta.get())+m_h.get()*Math.sin(m_theta.get()))/2;}
		});
		p1.yProperty().bind(new DoubleBinding() {
			{super.bind(center.yProperty(),m_w,m_h,m_theta);}
			@Override
			protected double computeValue()
				{return center.y()+(-m_h.get()*Math.cos(m_theta.get())-m_w.get()*Math.sin(m_theta.get()))/2;}
		});
		// p2
		p2.xProperty().bind(new DoubleBinding() {
			{super.bind(center.xProperty(),m_w,m_h,m_theta);}
			@Override
			protected double computeValue()
				{return center.x()+(m_w.get()*Math.cos(m_theta.get())+m_h.get()*Math.sin(m_theta.get()))/2;}
		});
		p2.yProperty().bind(new DoubleBinding() {
			{super.bind(center.yProperty(),m_w,m_h,m_theta);}
			@Override
			protected double computeValue()
				{return center.y()+(-m_h.get()*Math.cos(m_theta.get())+m_w.get()*Math.sin(m_theta.get()))/2;}
		});
		// p3
		p3.xProperty().bind(new DoubleBinding() {
			{super.bind(center.xProperty(),m_w,m_h,m_theta);}
			@Override
			protected double computeValue()
				{return center.x()+(-m_w.get()*Math.cos(m_theta.get())-m_h.get()*Math.sin(m_theta.get()))/2;}
		});
		p3.yProperty().bind(new DoubleBinding() {
			{super.bind(center.yProperty(),m_w,m_h,m_theta);}
			@Override
			protected double computeValue()
				{return center.y()+(m_h.get()*Math.cos(m_theta.get())-m_w.get()*Math.sin(m_theta.get()))/2;}
		});
		// p4
		p4.xProperty().bind(new DoubleBinding() {
			{super.bind(center.xProperty(),m_w,m_h,m_theta);}
			@Override
			protected double computeValue()
				{return center.x()+(m_w.get()*Math.cos(m_theta.get())-m_h.get()*Math.sin(m_theta.get()))/2;}
		});
		p4.yProperty().bind(new DoubleBinding() {
			{super.bind(center.yProperty(),m_w,m_h,m_theta);}
			@Override
			protected double computeValue()
				{return center.y()+(m_h.get()*Math.cos(m_theta.get())+m_w.get()*Math.sin(m_theta.get()))/2;}
		});

		p1.setMoveFnct((abs,ord) -> {center.setX(abs.doubleValue()); center.setY(ord.doubleValue());});
		p2.setMoveFnct((abs,ord) -> {center.setX(abs.doubleValue()); center.setY(ord.doubleValue());});
		p3.setMoveFnct((abs,ord) -> {center.setX(abs.doubleValue()); center.setY(ord.doubleValue());});
		p4.setMoveFnct((abs,ord) -> {center.setX(abs.doubleValue()); center.setY(ord.doubleValue());});
	}
	
	@Override
	public void addEventHandler(EventType<MouseEvent> type, EventHandler<MouseEvent> handler)
	{
		r.addEventHandler(type, handler);
	}
	
	@Override
	public void setFill (Color color)
	{
		r.setFill(color);
	}
}
