import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import graph.*;
import solvers.*;



public class Main extends Application
{
	Graph graph;
	Solver solver;
	Thread t;
	
	boolean color = false;
	
	public Main ()
	{		
		graph = new Graph();
		
		Point p1 = new Point(200,100);
		Point p2 = new Point(500,100);
		Point p3 = new Point(100,300);
		Point p4 = new Point(300,500);
		Point p5 = new Point(600,400);
		
		Rectangle r1 = new Rectangle(p1, new SimpleDoubleProperty(130), new SimpleDoubleProperty(100), new SimpleDoubleProperty(0.3));
		Rectangle r2 = new Rectangle(p2, new SimpleDoubleProperty(150), new SimpleDoubleProperty(100));
		Rectangle r3 = new Rectangle(p3, new SimpleDoubleProperty(100), new SimpleDoubleProperty(100));
		Rectangle r4 = new Rectangle(p4, new SimpleDoubleProperty(90), new SimpleDoubleProperty(70));
		Rectangle r5 = new Rectangle(p5, new SimpleDoubleProperty(110), new SimpleDoubleProperty(90));
		
		graph.add(r1);
		graph.add(r2);
		graph.add(r3);
		graph.add(r4);
		graph.add(r5);
		
		Point a = new Point(0,0);
		r1.addAnchorPoint(a,r2.center());
		Point b = new Point(0,0);
		r2.addAnchorPoint(b,r1.center());
		Line l = new Line(a,b);
		graph.add(l);
		
		a = new Point(500,150);
		l.addAnchorPoint(a,.5);
		b = new Point(0,0);
		r3.addAnchorPoint(b,a);
		graph.add(new Line(a,b));
		
		a = new Point(0,0);
		r1.addAnchorPoint(a, r3.center());
		b = new Point(0,0);
		r3.addAnchorPoint(b, r1.center());
		graph.add(new Line(a,b));
		
		a = new Point(0,0);
		r4.addAnchorPoint(a,r3.center());
		b = new Point(0,0);
		r3.addAnchorPoint(b,r4.center());
		graph.add(new Line(a,b));
		
		a = new Point(0,0);
		r1.addAnchorPoint(a,r4.center());
		b = new Point(0,0);
		r4.addAnchorPoint(b,r1.center());
		graph.add(new Line(a,b));
		
		a = new Point(0,0);
		r4.addAnchorPoint(a,r5.center());
		b = new Point(0,0);
		r5.addAnchorPoint(b,r4.center());
		graph.add(new Line(a,b));
		
		a = new Point(0,0);
		r2.addAnchorPoint(a,r5.center());
		b = new Point(0,0);
		r5.addAnchorPoint(b,r2.center());
		graph.add(new Line(a,b));
		
		r1.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(color)
					r1.setFill(Color.TRANSPARENT);
				else
					r1.setFill(Color.DARKORCHID);
				color = !color;
			}});
		
		solver = new GradientDescent(graph, 0.1);//new InterMolecular(graph);
		//((GradientDescent)solver).addConstraint(new RectPenConstraint(graph,5));
		//((GradientDescent)solver).addConstraint(new LineLenConstraint(graph,1));
		((GradientDescent)solver).addConstraint(new RectRotConstraint(graph,1));
		t = new Thread(solver);
		t.start();
	}
	
	
	public static void main(String[] args)
	{
		launch(args);
	}

	
	@Override
	public void start(Stage pStage) throws Exception
	{
		pStage.setTitle("Graph Printer");
		
		Group root = new Group();
		graph.draw(root.getChildren());
				
		Scene s = new Scene(root, 800, 600);
		
		s.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			@Override
			public void handle(KeyEvent e) {
				
				if (e.getCode() == KeyCode.SPACE)
				{
					if (solver.isRunning())
						solver.stop();
					else
						solver.start();
				}
			}
		});
		
		pStage.setScene(s);
		pStage.show();
	}
	
	
	@Override
	public void stop ()
	{
		solver.end();
	}
}
