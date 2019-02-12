import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;

import graph.*;



public class Main extends Application
{
	Graph graph;
	
	public Main ()
	{
		graph = new Graph();
		
		Point p = new Point(200,200);
		Line l = new Line(new Point(100,100), new Point(300,200));
		l.addAnchorPoint(p);
		graph.add(l);
		Point p2 = new Point(500,200);
		Rectangle r = new Rectangle(p2, new SimpleDoubleProperty(150), new SimpleDoubleProperty(100));
		graph.add(r);
		Point p3 = new Point(500,230);
		r.addAnchorPoint(p3);
		l = new Line(p, p3);
		graph.add(l);
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
		
		pStage.setScene(s);
		pStage.show();
	}
}
