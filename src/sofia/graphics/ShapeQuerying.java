package sofia.graphics;

import java.util.Set;

import android.graphics.PointF;

//-------------------------------------------------------------------------
/**
 * Methods that query shapes based on position, intersection, or other
 * properties.
 * 
 * @author Tony Allevato
 * @version 2012.10.10
 */
public interface ShapeQuerying
{
	//~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets a set that represents all the shapes currently in this view. Note
     * that this set is not a copy of the view's shape set; changes to this set
     * will <em>directly affect</em> the view.
     * 
     * @return a set that represents all the shapes currently in this view
     */
	Set<Shape> getShapes();
	
    
    // ----------------------------------------------------------
    /**
     * Get all the shapes of the specified type in this view.
     *
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return List of all the shapes of the specified type (or any of its
     *         subtypes) in the view.
     */
	<MyShape extends Shape> Set<MyShape> getShapes(Class<MyShape> cls);

    
    // ----------------------------------------------------------
    /**
     * Get one shape (if any) that overlaps the specified location.  If
     * multiple shapes overlap that location, the one "in front" (drawn
     * latest) is returned.
     * @param x The x-coordinate of the location to check.
     * @param y The y-coordinate of the location to check.
     * @return The front-most shape at the specified location, or null if none.
     */
    Shape getShapeAt(float x, float y);


    // ----------------------------------------------------------
    /**
     * Get one shape of the specified type (if any) that overlaps the
     * specified location.  If multiple shapes overlap that location, the
     * one "in front" (drawn latest) is returned.
     * @param x The x-coordinate of the location to check.
     * @param y The y-coordinate of the location to check.
     * @param cls Class of shape to look for (passing 'null' will find any
     *            object).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return The front-most shape at the specified location, or null if none.
     */
    public <MyShape extends Shape> MyShape getShapeAt(
        float x, float y, Class<MyShape> cls);


    // ----------------------------------------------------------
    /**
     * Get one shape (if any) that overlaps the specified location.  If
     * multiple shapes overlap that location, the one "in front" (drawn
     * latest) is returned.
     * @param point The location to check.
     * @return The front-most shape at the specified location, or null if none.
     */
    Shape getShapeAt(PointF point);


    // ----------------------------------------------------------
    /**
     * Get one shape of a specified type (if any) that overlaps the
     * specified location.  If multiple shapes overlap that location, the
     * one "in front" (drawn latest) is returned.
     * @param point The location to check.
     * @param cls Class of shape to look for (passing 'null' will find any
     *            object).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return The front-most shape at the specified location, or null if none.
     */
    <MyShape extends Shape> MyShape getShapeAt(
        PointF point, Class<MyShape> cls);


    // ----------------------------------------------------------
    /**
     * Get all the shapes overlapping the specified location.
     * @param x The x-coordinate of the location to check.
     * @param y The y-coordinate of the location to check.
     * @return A set of all shapes at the specified location.
     */
    Set<Shape> getShapesAt(float x, float y);


    // ----------------------------------------------------------
    /**
     * Get all the shapes of the specified type overlapping the specified
     * location.
     * @param x The x-coordinate of the location to check.
     * @param y The y-coordinate of the location to check.
     * @param cls Class of shape to look for (passing 'null' will find any
     *            object).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return A set of all shapes at the specified location.
     */
    <MyShape extends Shape> Set<MyShape> getShapesAt(
        float x, float y, Class<MyShape> cls);


    // ----------------------------------------------------------
    /**
     * Get all the shapes overlapping the specified location.
     * @param point The location to check.
     * @return A set of all shapes at the specified location.
     */
    Set<Shape> getShapesAt(PointF point);


    // ----------------------------------------------------------
    /**
     * Get all the shapes of the specified type overlapping the specified
     * location.
     * @param point The location to check.
     * @param cls Class of shape to look for (passing 'null' will find any
     *            object).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return A set of all shapes at the specified location.
     */
    <MyShape extends Shape> Set<MyShape> getShapesAt(
        PointF point, Class<MyShape> cls);
}
