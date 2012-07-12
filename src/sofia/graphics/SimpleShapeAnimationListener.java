package sofia.graphics;

// -------------------------------------------------------------------------
/**
 * This class implements the {@link ShapeAnimationListener} interface and
 * provides empty default implementations for the methods. If you do not want
 * to implement every method in the interface, you can subclass this and
 * implement only those that you are interested in.
 *
 * @author  Tony Allevato
 * @version 2011.12.12
 */
public class SimpleShapeAnimationListener implements ShapeAnimationListener
{
    // ----------------------------------------------------------
    public void onAnimationStart(Shape shape)
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    public void onAnimationRepeat(Shape shape, boolean backward)
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    public void onAnimationEnd(Shape shape)
    {
        // Do nothing.
    }
}
