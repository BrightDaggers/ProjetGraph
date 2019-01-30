import javafx.application.Application;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

import graph.Graph;


public class Main extends Application
{
	Graph graph;
	
	public Main ()
	{
		graph = new Graph();
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage pStage) throws Exception
	{
		pStage.setTitle("Graph Printer");
		
		Rectangle r = new Rectangle(100, 100, 100, 100);
		r.setFill(Color.BLUE);
		Rectangle r2 = new Rectangle(150, 150, 100, 100);
		r2.setFill(Color.RED);
		
		
		r.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Click Event");
			}
        });
		
		Group root = new Group();
		root.getChildren().add(r);
		root.getChildren().add(r2);
		
		
		pStage.setScene(new Scene(root, 800, 600));
		pStage.show();
	}
}
