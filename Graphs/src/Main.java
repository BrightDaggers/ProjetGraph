import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import graph.*;
import solvers.*;



public class Main extends Application
{
	Graph graph;
	InterMolecular solver;
	Thread t;
	
	public Main ()
	{
		graph = new Graph();
		
		Point p1 = new Point(200,100);
		Point p2 = new Point(600,100);
		Point p3 = new Point(100,300);
		Point p4 = new Point(300,500);
		Point p5 = new Point(600,400);
		
		Rectangle r1 = new Rectangle(p1, new SimpleDoubleProperty(130), new SimpleDoubleProperty(100));
		Rectangle r2 = new Rectangle(p2, new SimpleDoubleProperty(150), new SimpleDoubleProperty(100));
		Rectangle r3 = new Rectangle(p3, new SimpleDoubleProperty(100), new SimpleDoubleProperty(100));
		Rectangle r4 = new Rectangle(p4, new SimpleDoubleProperty(90), new SimpleDoubleProperty(70));
		Rectangle r5 = new Rectangle(p5, new SimpleDoubleProperty(110), new SimpleDoubleProperty(90));
		
		graph.add(r1);
		graph.add(r2);
		graph.add(r3);
		graph.add(r4);
		graph.add(r5);
		
		Point a = new Point(330,150);
		r1.addAnchorPoint(a);
		Point b = new Point(600,150);
		r2.addAnchorPoint(b);
		Line l = new Line(a,b);
		graph.add(l);
		
		a = new Point(500,150);
		l.addAnchorPoint(a);
		b = new Point(200,350);
		r3.addAnchorPoint(b);
		graph.add(new Line(a,b));
		
		a = new Point(240,200);
		r1.addAnchorPoint(a);
		b = new Point(150,300);
		r3.addAnchorPoint(b);
		graph.add(new Line(a,b));
		
		a = new Point(300,530);
		r4.addAnchorPoint(a);
		b = new Point(170,400);
		r3.addAnchorPoint(b);
		graph.add(new Line(a,b));
		
		a = new Point(290,200);
		r1.addAnchorPoint(a);
		b = new Point(320,500);
		r4.addAnchorPoint(b);
		graph.add(new Line(a,b));
		
		a = new Point(390,530);
		r4.addAnchorPoint(a);
		b = new Point(600,450);
		r5.addAnchorPoint(b);
		graph.add(new Line(a,b));
		
		a = new Point(680,200);
		r2.addAnchorPoint(a);
		b = new Point(655,400);
		r5.addAnchorPoint(b);
		graph.add(new Line(a,b));
		
		
		solver = new InterMolecular(graph);
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
		
		/*Canvas canvas = new Canvas(800,600);
		GraphicsContext gc = canvas.getGraphicsContext2D();*/
		
		
		Scene s = new Scene(root, 800, 600);
		s.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
			{
				@Override
				public void handle(MouseEvent e) {
					
					if (e.isPrimaryButtonDown())
					{
						if (!(e.getTarget() instanceof javafx.scene.shape.Shape))
							Shape.setShapeEdition(null);
					}
					else if(e.isSecondaryButtonDown())
						e.consume();
						// open edition menu
				}
			});
		
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
