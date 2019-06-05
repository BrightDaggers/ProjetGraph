package graph;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public abstract class Shape
{
	public enum FLAGS
	{
		CONNECTOR, SHAPE
	}
	
	public final FLAGS flag;
	
	public Shape(FLAGS flag)
	{
		this.flag = flag;
	}
	
	protected javafx.collections.ObservableList<javafx.scene.Node> m_list;
	protected ArrayList<Point> anchorPoints = new ArrayList<>();
	
	public abstract void draw (javafx.collections.ObservableList<javafx.scene.Node> list);
	
	public abstract void drawOnEdition ();
	public abstract void drawEndEdition ();

	public abstract void addAnchorPoint (Point p);
	public abstract void rmAnchorPoint (Point p);
	
	public abstract void addEventHandler(EventType<MouseEvent> type, EventHandler<MouseEvent> handler);
	
	public abstract void setFill(Color color);
}
