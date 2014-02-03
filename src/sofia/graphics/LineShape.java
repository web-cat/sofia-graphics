package sofia.graphics;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * <p>
 * A shape that is drawn as a line between two points.
 * </p>
 *
 * <h2>Physics</h2>
 * <p>
 * Lines have no volume, and therefore cannot collide with each other. However,
 * lines can still be involved in collisions with other shapes that do have
 * volume, such as {@link RectangleShape}, {@link OvalShape}, and
 * {@link ImageShape} to name a few.
 * </p>
 *
 * @author  Tony Allevato
 * @version 2011.09.29
 */
public class LineShape extends StrokedShape
{
    //~ Fields ................................................................

    private PointF startPoint;
    private PointF endPoint;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new {@code LineShape} with default position and size.
     */
    public LineShape()
    {
        this(new PointF(0, 0), new PointF(0, 0));
    }


    // ----------------------------------------------------------
    /**
     * Creates a new {@code LineShape} between two points.
     *
     * @param point1 starting point for the line
     * @param point2 ending point for the line
     */
    public LineShape(PointF point1, PointF point2)
    {
        startPoint = point1;
        endPoint = point2;

        PointF mid = Geometry.midpoint(point1, point2);
        updateTransform(mid.x, mid.y);
    }


    // ----------------------------------------------------------
    /**
     * Creates a new {@code LineShape} between two points.
     *
     * @param x1 the x-coordinate of the first endpoint
     * @param y1 the y-coordinate of the first endpoint
     * @param x2 the x-coordinate of the second endpoint
     * @param y2 the y-coordinate of the second endpoint
     */
    public LineShape(float x1, float y1, float x2, float y2)
    {
        setBounds(new RectF(x1, y1, x2, y2));
    }


    // ----------------------------------------------------------
    /**
     * Creates a new {@code LineShape} that starts at the top-left corner of
     * the specified rectangle and ends at the bottom-right corner.
     *
     * @param bounds the bounding rectangle of the line
     */
    public LineShape(RectF bounds)
    {
        setBounds(bounds);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public RectF getBounds()
    {
        // If the body has been created, update the center point using the
        // body's current position.

        Body b2Body = getB2Body();
        if (b2Body != null)
        {
            PointF mid = Geometry.midpoint(startPoint, endPoint);
            float startOffsetX = startPoint.x - mid.x;
            float startOffsetY = startPoint.y - mid.y;
            float endOffsetX = endPoint.x - mid.x;
            float endOffsetY = endPoint.y - mid.y;

            Vec2 pos = b2Body.getPosition();
            startPoint.x = pos.x + startOffsetX;
            startPoint.y = pos.y + startOffsetY;
            endPoint.x = pos.x + endOffsetX;
            endPoint.y = pos.y + endOffsetY;
        }

        return new RectF(startPoint.x, startPoint.y,
                endPoint.x, endPoint.y);
    }


    // ----------------------------------------------------------
    @Override
    public void setBounds(RectF newBounds)
    {
        startPoint = new PointF(newBounds.left, newBounds.top);
        endPoint = new PointF(newBounds.right, newBounds.bottom);

        updateTransform(newBounds.centerX(), newBounds.centerY());

        recreateFixtures();
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Drawing drawing)
    {
        Canvas canvas = drawing.getCanvas();
        Paint paint = getPaint();
        RectF bounds = getBounds();

        canvas.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom,
            paint);
    }


    // ----------------------------------------------------------
    @Override
    public boolean contains(float x, float y)
    {
        float[] point = inverseTransformPoint(x, y);
        float nx = point[0];
        float ny = point[1];

        float tolerance = (float) getStrokeWidth() + 0.5f;

        RectF bounds = getBounds();

        float x1 = bounds.left;
        float y1 = bounds.top;

        float x2 = bounds.right;
        float y2 = bounds.bottom;

        float tSquared = tolerance * tolerance;

        if (distanceSquared(nx, ny, x1, y1) < tSquared) return true;
        if (distanceSquared(nx, ny, x2, y2) < tSquared) return true;

        if (nx < Math.min(x1, x2) - tolerance) return false;
        if (nx > Math.max(x1, x2) + tolerance) return false;
        if (ny < Math.min(y1, y2) - tolerance) return false;
        if (ny > Math.max(y1, y2) + tolerance) return false;

        if (x1 - x2 == 0 && y1 - y2 == 0) return false;

        float u = ((nx - x1) * (x2 - x1) + (ny - y1) * (y2 - y1))
            / distanceSquared(x1, y1, x2, y2);

        return distanceSquared(nx, ny, x1 + u * (x2 - x1), y1 + u * (y2 - y1))
            < tSquared;
    }


    // ----------------------------------------------------------
    private static float distanceSquared(
        float x1, float y1, float x2, float y2)
    {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    }


    // ----------------------------------------------------------
    @Override
    protected void createFixtures()
    {
        PointF mid = Geometry.midpoint(startPoint, endPoint);

        EdgeShape edge = new EdgeShape();
        edge.set(new Vec2(startPoint.x - mid.x, startPoint.y - mid.y),
                new Vec2(endPoint.x - mid.x, endPoint.y - mid.y));

        addFixtureForShape(edge);
    }
}
