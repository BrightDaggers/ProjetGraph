package graph;

public abstract class Shape
{
	// static shape being edited
	// callback to this shape when click outside
	
	protected javafx.collections.ObservableList<javafx.scene.Node> m_list;
	
	private static Shape shapeEdited = null;
	
	public abstract void draw (javafx.collections.ObservableList<javafx.scene.Node> list);
	
	public abstract void drawOnEdition ();
	public abstract void drawEndEdition ();
	
	
	public static void setShapeEdition (Shape shape)
	{
		if (shape.equals(shapeEdited))
			return;
		
		if (shapeEdited!=null)
			shapeEdited.drawEndEdition();
		
		if (shape!=null)
			shape.drawOnEdition();
		
		shapeEdited = shape;
	}
	
	public static Shape getShapeEdited ()
	{
		return shapeEdited;
	}
}
