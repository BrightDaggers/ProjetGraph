import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.*;

import graph.*;


public class Main extends Application
{
	Graph graph;
	
	public Main ()
	{
		graph = new Graph();
		
		graph.add(new Line(new Point(100,100), new Point(300,200)));
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage pStage) throws Exception
	{
		pStage.setTitle("Graph Printer");
		
		/*
		r.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Click Event");
			}
        });*/
		
		Group root = new Group();
		graph.draw(root.getChildren());
		
		pStage.setScene(new Scene(root, 800, 600));
		pStage.show();
	}
}
