package sofia.graphics;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * The abstract base class for all shape fill types, such as color fills and
 * image fills.
 *
 * @author  Tony Allevato
 * @version 2013.04.16
 */
public abstract class Fill
{
    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Fills the specified rectangle on a canvas.
     */
    public void fillRect(Drawing drawing, int alpha, RectF bounds)
    {
        Paint paint = getPaint();
        int oldAlpha = paint.getAlpha();
        paint.setAlpha(alpha);
        drawing.getCanvas().drawRect(bounds, paint);
        paint.setAlpha(oldAlpha);
    }


    // ----------------------------------------------------------
    /**
     * Fills the specified rectangle on a canvas.
     */
    public void fillOval(Drawing drawing, int alpha, RectF bounds)
    {
        Paint paint = getPaint();
        int oldAlpha = paint.getAlpha();
        paint.setAlpha(alpha);
        drawing.getCanvas().drawOval(bounds, paint);
        paint.setAlpha(oldAlpha);
    }


    // ----------------------------------------------------------
    /**
     * Fills the specified rectangle on a canvas.
     */
    public void fillPolygon(Drawing drawing, int alpha,
            Polygon polygon, PointF origin)
    {
        Paint paint = getPaint();
        int oldAlpha = paint.getAlpha();
        paint.setAlpha(alpha);

        Path path = new Path();
        path.incReserve(polygon.size());

        for (int i = 0; i < polygon.size(); i++)
        {
            PointF pt = polygon.get(i);
            if (i == 0)
            {
                path.moveTo(origin.x + pt.x, origin.y + pt.y);
            }
            else
            {
                path.lineTo(origin.x + pt.x, origin.y + pt.y);
            }
        }

        path.close();

        drawing.getCanvas().drawPath(path, paint);
        paint.setAlpha(oldAlpha);
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code Paint} object that will be used by
     * {@link #fillRect(Drawing, float, float, float, float)} and
     * ... to fill shapes. This method does not need to be implemented if the
     * subclass overrides the other {@code fill*} methods in their entirety.
     *
     * @return a {@code Paint} object used by the other methods
     */
    protected Paint getPaint()
    {
        return null;
    }
}
