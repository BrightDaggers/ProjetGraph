package graph;

import java.util.*;

public class Graph
{
	private ArrayList<Shape> shapes;
	
	
	public Graph ()
	{
		shapes = new ArrayList<>();
	}
	
	public void add (Shape e)
	{
		shapes.add(e);
	}
	
	
	public void draw (javafx.collections.ObservableList<javafx.scene.Node> list)
	{
		shapes.forEach((e)->e.draw(list));
	}
}