import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
		GraphBuilder gb = new GraphBuilder();
		gb.addRectangle(200, 100, 130, 100, 0.3);
		gb.addRectangle(500, 100, 150, 100);
		gb.addRectangle(100, 300, 100, 100);
		gb.addRectangle(300, 500, 90, 70);
		gb.addRectangle(600, 400, 110, 90);
		
		gb.connect(0, 1);
		gb.connect(0, 2);
		gb.connect(0, 4);
		gb.connect(2, 3);
		gb.connect(3, 4);
		gb.connect(1, 4);
		
		solver = new Convex(gb.toRectsArray(), 0.1);
		
		graph = ((Convex)solver).mainAlgo();
		//solver = new GradientDescent(graph, 0.1);//new InterMolecular(graph);
		//((GradientDescent)solver).addConstraint(new RectPenConstraint(graph,5));
		//((GradientDescent)solver).addConstraint(new LineLenConstraint(graph,10));
		//((GradientDescent)solver).addConstraint(new RectRotConstraint(graph,1));
		//((GradientDescent)solver).addConstraint(new BubbleConstraint(graph, 1, 3));
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
			public void handle(KeyEvent e)
			{
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
