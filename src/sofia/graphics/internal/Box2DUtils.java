package sofia.graphics.internal;

import org.jbox2d.common.Vec2;

import android.graphics.PointF;

//-------------------------------------------------------------------------
/**
 * Utility methods to convert between Android graphics types and JBox2D types.
 * Intended for framework internal use, but some advanced users (who need to
 * access the JBox2D objects directly) might find these methods useful.
 *
 * @author  Tony Allevato
 * @version 2012.11.04
 */
public class Box2DUtils
{
    //~ Constructors .........................................................

    // ----------------------------------------------------------
    /**
     * Prevent instantiation.
     */
    private Box2DUtils()
    {
        // Do nothing.
    }


    //~ Methods ..............................................................

    // ----------------------------------------------------------
    /**
     * Converts a JBox2D {@code Vec2} object to an Android {@code PointF}.
     *
     * @param in the {@code Vec2} object
     * @param out a non-null {@code PointF} object where the result will be
     *     stored
     */
    public static void vec2ToPointF(Vec2 in, PointF out)
    {
        out.x = in.x;
        out.y = in.y;
    }


    // ----------------------------------------------------------
    /**
     * Converts a JBox2D {@code Vec2} object to an Android {@code PointF}.
     *
     * @param in the {@code Vec2} object
     * @return a new {@code PointF} object equal to the {@code Vec2}
     */
    public static PointF vec2ToPointF(Vec2 in)
    {
        PointF out = new PointF();
        vec2ToPointF(in, out);
        return out;
    }


    // ----------------------------------------------------------
    /**
     * Converts an Android {@code PointF} object to a JBox2D {@code Vec2}.
     *
     * @param in the {@code PointF} object
     * @param out a non-null {@code Vec2} object where the result will be
     *     stored
     */
    public static void pointFToVec2(PointF in, Vec2 out)
    {
        out.x = in.x;
        out.y = in.y;
    }


    // ----------------------------------------------------------
    /**
     * Converts an Android {@code PointF} object to a JBox2D {@code Vec2}.
     *
     * @param in the {@code PointF} object
     * @return a new {@code Vec2} object equal to the {@code PointF}
     */
    public static Vec2 pointFToVec2(PointF in)
    {
        Vec2 out = new Vec2();
        pointFToVec2(in, out);
        return out;
    }
}
