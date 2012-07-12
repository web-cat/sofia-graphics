package sofia.app;

import sofia.graphics.Shape;
import sofia.graphics.ShapeView;

// -------------------------------------------------------------------------
/**
 * TODO
 *
 * @author  Tony Allevato
 * @version 2011.12.14
 */
public abstract class ShapeScreen extends Screen
{
    //~ Instance/static variables .............................................

    private ShapeView shapeView;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    protected void beforeInitialize()
    {
        shapeView = new ShapeView(this);
        setContentView(shapeView);
    }


    // ----------------------------------------------------------
/*    @Override
    protected void afterInitialize()
    {
        shapeView.relayout();
    }*/


    // ----------------------------------------------------------
    @Override
    public boolean doInitializeAfterLayout()
    {
        return true;
    }


    // ----------------------------------------------------------
    /**
     * Gets the {@link ShapeView} that holds all of the shapes on this screen.
     *
     * @return the {@link ShapeView} that holds all of the shapes on this
     *     screen
     */
    public ShapeView getShapeView()
    {
        return shapeView;
    }


    // ----------------------------------------------------------
    public float getWidth()
    {
        return shapeView.getWidth();
    }


    // ----------------------------------------------------------
    public float getHeight()
    {
        return shapeView.getHeight();
    }


    // ----------------------------------------------------------
    /**
     * Adds a shape to the screen.
     *
     * @param shape the shape to add to the screen
     */
    public void add(Shape shape)
    {
        shapeView.add(shape);
    }


    // ----------------------------------------------------------
    /**
     * Removes a shape from the screen.
     *
     * @param shape the shape to remove from the screen
     */
    public void remove(Shape shape)
    {
        shapeView.remove(shape);
    }


    // ----------------------------------------------------------
    public void enableScaleGestures()
    {
        shapeView.enableScaleGestures();
    }


    // ----------------------------------------------------------
    public void enableRotateGestures()
    {
        shapeView.enableRotateGestures();
    }
}
