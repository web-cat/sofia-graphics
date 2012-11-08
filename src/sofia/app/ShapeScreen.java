package sofia.app;

import java.util.Set;

import android.graphics.PointF;
import android.view.View;
import sofia.graphics.Color;
import sofia.graphics.Shape;
import sofia.graphics.ShapeManipulating;
import sofia.graphics.ShapeQuerying;
import sofia.graphics.ShapeView;

// -------------------------------------------------------------------------
/**
 * <p>
 * {@code ShapeScreen} is a subclass of screen that provides a built-in
 * {@link ShapeView} and convenience methods for manipulating shapes directly
 * in the screen class instead of having to call {@link #getShapeView()} for
 * every operation.
 * </p><p>
 * When you subclass {@code ShapeScreen}, by default it will create a new
 * {@code ShapeView} that occupies the entire width and height of the screen.
 * If this is not what you want (for example, if you want to have a
 * {@code ShapeView} alongside other widgets but still retain the convenience
 * of methods like {@link #add(Shape)} directly on the screen), then place an
 * instance of {@code ShapeView} in your layout file with the ID
 * {@code shapeView}. Then the {@code ShapeScreen} will use that view for all
 * of its other methods instead of creating its own.
 * </p>
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public abstract class ShapeScreen
    extends Screen
    implements ShapeManipulating, ShapeQuerying
{
    //~ Fields ................................................................

    private ShapeView shapeView;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    protected void afterLayoutInflated(boolean inflated)
    {
        if (inflated)
        {
            boolean hasShapeView = false;

            int shapeViewId = getResources().getIdentifier(
                    "shapeView", "id", getPackageName());

            if (shapeViewId != 0)
            {
                View view = findViewById(shapeViewId);

                if (view instanceof ShapeView)
                {
                    shapeView = (ShapeView) findViewById(shapeViewId);
                    hasShapeView = true;
                }
            }

            if (!hasShapeView)
            {
                throw new IllegalStateException("A ShapeScreen that uses a "
                        + "custom layout must contain an "
                        + "android.graphics.ShapeView with the ID "
                        + "\"shapeView\".");
            }
        }
        else
        {
            shapeView = createShapeView(this);
            setContentView(shapeView);

            shapeView.requestFocus();
        }
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


    // ----------------------------------------------------------
    /**
     * Sets the background color of the screen's ShapeView. Note that if you
     * provide your own layout where the ShapeView only occupies a portion of
     * the screen, this method will only affect the ShapeView portion and not
     * the entire screen.
     *
     * @param color the desired background color
     */
    public void setBackgroundColor(Color color)
    {
        shapeView.setBackgroundColor(color);
    }


    // ----------------------------------------------------------
    public Set<Shape> getShapes()
    {
        return shapeView.getShapes();
    }


    // ----------------------------------------------------------
    public <MyShape extends Shape> Set<MyShape> getShapes(Class<MyShape> cls)
    {
        return shapeView.getShapes(cls);
    }


    // ----------------------------------------------------------
    public Shape getShapeAt(float x, float y)
    {
        return shapeView.getShapeAt(x, y);
    }


    // ----------------------------------------------------------
    public <MyShape extends Shape> MyShape getShapeAt(float x, float y,
            Class<MyShape> cls)
    {
        return shapeView.getShapeAt(x, y, cls);
    }


    // ----------------------------------------------------------
    public Shape getShapeAt(PointF point)
    {
        return shapeView.getShapeAt(point);
    }


    // ----------------------------------------------------------
    public <MyShape extends Shape> MyShape getShapeAt(PointF point,
            Class<MyShape> cls)
    {
        return shapeView.getShapeAt(point, cls);
    }


    // ----------------------------------------------------------
    public Set<Shape> getShapesAt(float x, float y)
    {
        return shapeView.getShapesAt(x, y);
    }


    // ----------------------------------------------------------
    public <MyShape extends Shape> Set<MyShape> getShapesAt(float x, float y,
            Class<MyShape> cls)
    {
        return shapeView.getShapesAt(x, y, cls);
    }


    // ----------------------------------------------------------
    public Set<Shape> getShapesAt(PointF point)
    {
        return shapeView.getShapesAt(point);
    }


    // ----------------------------------------------------------
    public <MyShape extends Shape> Set<MyShape> getShapesAt(PointF point,
            Class<MyShape> cls)
    {
        return shapeView.getShapesAt(point, cls);
    }
}
