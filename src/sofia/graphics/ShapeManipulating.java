package sofia.graphics;

//-------------------------------------------------------------------------
/**
 * Methods that manipulate shapes by adding or removing them from their parent,
 * such as a {@link ShapeView} or a {@link CompositeShape}.
 * 
 * @author  Tony Allevato
 * @version 2012.10.10
 */
public interface ShapeManipulating
{
	//~ Methods ...............................................................

	// ----------------------------------------------------------
    /**
     * Add a shape to the object implementing this interface.
     * 
     * @param shape The shape to add.
     */
	void add(Shape shape);


	// ----------------------------------------------------------
    /**
     * Remove a shape from the object implementing this interface.
     * 
     * @param shape The shape to remove.
     */
	void remove(Shape shape);
	

	// ----------------------------------------------------------
    /**
     * Removes all shapes currently in the object implementing this interface.
     */
	void clear();
}
