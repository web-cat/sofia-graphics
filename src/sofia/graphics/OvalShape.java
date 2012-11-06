package sofia.graphics;

import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * A shape that is drawn as an oval.
 *
 * @author  Tony Allevato
 * @version 2012.09.29
 */
public class OvalShape extends FillableShape
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates an {@code OvalShape} with default position and size.
     */
    public OvalShape()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code OvalShape} with the specified bounds.
     */
    public OvalShape(RectF bounds)
    {
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    /**
     * Creates a circular {@code OvalShape} with the specified center point
     * and radius.
     *
     * @param center the center of the circle
     * @param radius the radius of the circle
     */
    public OvalShape(PointF center, float radius)
    {
        this(Anchor.CENTER.anchoredAt(center).sized(radius * 2, radius * 2));
    }


    // ----------------------------------------------------------
    /**
     * Creates a circular {@code OvalShape} with the specified center point
     * and radius.
     *
     * @param x the x-coordinate of the center of the circle
     * @param y the y-coordinate of the center of the circle
     * @param radius the radius of the circle
     */
    public OvalShape(float x, float y, float radius)
    {
        this(Anchor.CENTER.anchoredAt(x, y).sized(radius * 2, radius * 2));
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code OvalShape} with the specified center point and
     * horizontal and vertical radii.
     *
     * @param center the center of the oval
     * @param horizontalRadius the radius of the oval along the x-axis
     * @param verticalRadius the radius of the oval along the y-axis
     */
    public OvalShape(PointF center, float horizontalRadius,
            float verticalRadius)
    {
        this(Anchor.CENTER.anchoredAt(center).sized(
                horizontalRadius * 2, verticalRadius * 2));
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code OvalShape} with the specified center point and
     * horizontal and vertical radii.
     *
     * @param x the x-coordinate of the center of the oval
     * @param y the y-coordinate of the center of the oval
     * @param horizontalRadius the radius of the oval along the x-axis
     * @param verticalRadius the radius of the oval along the y-axis
     */
    public OvalShape(float x, float y, float horizontalRadius,
            float verticalRadius)
    {
        this(Anchor.CENTER.anchoredAt(x, y).sized(
                horizontalRadius * 2, verticalRadius * 2));
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    protected Paint getPaint()
    {
        Paint paint = super.getPaint();
        paint.setAntiAlias(true);
        return paint;
    }


    // ----------------------------------------------------------
    @Override
    protected Paint getFillPaint()
    {
        Paint paint = super.getFillPaint();
        paint.setAntiAlias(true);
        return paint;
    }


    // ----------------------------------------------------------
    @Override
    public boolean contains(float x, float y)
    {
/*        float[] point = inverseTransformPoint(x, y);

        double rx = getWidth() / 2;
        double ry = getHeight() / 2;

        if (rx == 0 || ry == 0)
        {
            return false;
        }

        double dx = point[0] - (getX() + rx);
        double dy = point[1] - (getY() + ry);

        return (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) <= 1.0;*/
        return true;
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Canvas canvas)
    {
        RectF bounds = getBounds();

        if (isFilled())
        {
            Paint fillPaint = getFillPaint();
            canvas.drawOval(bounds, fillPaint);
        }

        Paint paint = getPaint();
        canvas.drawOval(bounds, paint);
    }


    // ----------------------------------------------------------
    @Override
    protected void createFixtures()
    {
        // TODO Auto-generated method stub
    }
}
