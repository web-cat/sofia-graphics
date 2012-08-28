package sofia.app;

import sofia.graphics.Shape;
import sofia.graphics.ShapeView;

// -------------------------------------------------------------------------
/**
 * TODO
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public abstract class ShapeScreen
    extends Screen
{
    //~ Instance/static variables .............................................

    private ShapeView shapeView;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    protected void beforeInitialize()
    {
        shapeView = createShapeView(this);
        setContentView(shapeView);
        
        shapeView.requestFocus();
    }


    // ----------------------------------------------------------
    /**
     * This factory method is used to create the {@link ShapeView}
     * that will be contained by this screen.  It is provided for
     * subclass extensibility, in case a subclass of ShapeScreen wants
     * to use a more specialized ShapeView instance.
     * @param parent The screen that will contain the view (e.g., "this")
     * @return A new ShapeView object to use for this screen.
     */
    protected ShapeView createShapeView(ShapeScreen parent)
    {
        return new ShapeView(parent);
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
     * @return The {@link ShapeView} that holds all of the shapes on this
     *         screen.
     */
    public ShapeView getShapeView()
    {
        return shapeView;
    }


    // ----------------------------------------------------------
    /**
     * Return the width of the your view.
     *
     * @return The width of your view, in pixels.
     */
    public float getWidth()
    {
        return shapeView.getWidth();
    }


    // ----------------------------------------------------------
    /**
     * Return the height of your view.
     *
     * @return The height of your view, in pixels.
     */
    public float getHeight()
    {
        return shapeView.getHeight();
    }


    // ----------------------------------------------------------
    /**
     * Adds a shape to the screen.
     *
     * @param shape The shape to add to the screen.
     */
    public void add(Shape shape)
    {
        shapeView.add(shape);
    }


    // ----------------------------------------------------------
    /**
     * Removes a shape from the screen.
     *
     * @param shape The shape to remove from the screen.
     */
    public void remove(Shape shape)
    {
        shapeView.remove(shape);
    }
    
    
    // ----------------------------------------------------------
    /**
     * Removes all shapes currently on the screen.
     */
    public void clear()
    {
    	shapeView.clear();
    }


    // ----------------------------------------------------------
    /**
     * Turn on detection of scale (pinch) gestures on the ShapeView.
     */
    public void enableScaleGestures()
    {
        shapeView.enableScaleGestures();
    }


    // ----------------------------------------------------------
    /**
     * Turn on detection of rotation gestures on the ShapeView.
     */
    public void enableRotateGestures()
    {
        shapeView.enableRotateGestures();
    }
}
