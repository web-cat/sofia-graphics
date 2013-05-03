package sofia.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * <p>
 * The idea behind this class is to abstract out the idea of a drawing canvas
 * so that the same shape code can be used across different platforms (i.e.,
 * Android and Swing/Java Graphics or JavaFX) or with different rendering
 * mechanisms (e.g., OpenGL). It also provides some unique features though,
 * like access to the current frame number for animation purposes.
 * </p><p>
 * This API is currently very fragile; right now it just provides a method to
 * grab the Android {@link Canvas} instead of its own drawing methods. Since
 * this class is only used internally, there is the strong possibility that it
 * will change in breaking ways in the future.
 * </p><p>
 * This class is only used internally by shapes to do their own drawing, and
 * users should never have to worry about it unless they're subclassing a shape
 * to provide custom drawing behavior, or they are providing a new rendering
 * engine.
 * </p>
 *
 * @author  Tony Allevato
 * @version 2013.04.16
 */
public interface Drawing
{
    //~ Public methods ........................................................

    // ----------------------------------------------------------
    public Context getContext();


    // ----------------------------------------------------------
    public Canvas getCanvas();


    // ----------------------------------------------------------
    public CoordinateSystem getCoordinateSystem();


    // ----------------------------------------------------------
    public int getFrameNumber();
}
