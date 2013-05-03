package sofia.graphics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import sofia.graphics.internal.Box2DUtils;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    //~ Fields ................................................................

    private static final int OVAL_VERTEX_COUNT = 24;

    private PointF center;
    private float xRadius;
    private float yRadius;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates an {@code OvalShape} with default position and size.
     */
    public OvalShape()
    {
        this(new PointF(0, 0), 0, 0);
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code OvalShape} with the specified bounds.
     */
    public OvalShape(RectF bounds)
    {
        this(new PointF(bounds.centerX(), bounds.centerY()),
                bounds.width() / 2, bounds.height() / 2);
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
        this(center, radius, radius);
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
        this(new PointF(x, y), radius);
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
        this.center = Geometry.clone(center);
        this.xRadius = horizontalRadius;
        this.yRadius = verticalRadius;
        updateTransform(center.x, center.y);
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
        this(new PointF(x, y), horizontalRadius, verticalRadius);
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
    public RectF getBounds()
    {
        // If the body has been created, update the center point using the
        // body's current position.

        Body b2Body = getB2Body();
        if (b2Body != null)
        {
            Box2DUtils.vec2ToPointF(b2Body.getPosition(), center);
        }

        return new RectF(center.x - xRadius, center.y - yRadius,
                center.x + xRadius, center.y + yRadius);
    }


    // ----------------------------------------------------------
    @Override
    public void setBounds(RectF newBounds)
    {
        updateTransform(newBounds.centerX(), newBounds.centerY());
        xRadius = newBounds.width() / 2;
        yRadius = newBounds.height() / 2;

        recreateFixtures();
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    @Override
    public boolean contains(float x, float y)
    {
        float[] point = inverseTransformPoint(x, y);

        double rx = xRadius;
        double ry = yRadius;

        if (rx == 0 || ry == 0)
        {
            return false;
        }

        double dx = point[0] - center.x;
        double dy = point[1] - center.y;

        return (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) <= 1.0;
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Drawing drawing)
    {
        RectF bounds = getBounds();
        Canvas canvas = drawing.getCanvas();

        if (isFilled())
        {
            getFill().fillOval(drawing, getAlpha(), bounds);
        }

        // TODO abstract out stroke
        if (!getColor().isTransparent())
        {
            Paint paint = getPaint();
            canvas.drawOval(bounds, paint);
        }
    }


    // ----------------------------------------------------------
    @Override
    protected void createFixtures()
    {
        if (Math.abs(xRadius - yRadius) < 0.001)
        {
            // It's a circle, so use a CircleShape.
            CircleShape circle = new CircleShape();
            circle.m_radius = xRadius;
            addFixtureForShape(circle);
        }
        else
        {
            int vertex = 0;

            Vec2[] vertices = new Vec2[8];

            // This algorithm splits the oval into polygonal slices that
            // each have 8 vertices (the center of the oval and 7 vertices
            // around the circumference), except for the final slice, which
            // only contains enough of the remaining vertices to complete
            // the final slice.

            while (vertex < OVAL_VERTEX_COUNT)
            {
                vertices[0] = new Vec2(0, 0);
                int verticesUsed = 1;
                int startVertex = vertex;

                for (int i = 0; i < 7 &&
                        startVertex + i <= OVAL_VERTEX_COUNT;
                        i++, vertex++)
                {
                    float fraction = (float) vertex / OVAL_VERTEX_COUNT;
                    vertices[verticesUsed++] =
                            pointOnEllipse(xRadius, yRadius, fraction);
                }

                PolygonShape polygon = new PolygonShape();
                polygon.set(vertices, verticesUsed);
                addFixtureForShape(polygon);

                if (vertex != OVAL_VERTEX_COUNT)
                {
                    // Roll back one so that the slices touch each other.
                    vertex--;
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets the location of a vertex on the circumference of an ellipse.
     *
     * @param xrad the horizontal radius of the ellipse
     * @param yrad the vertical radius of the ellipse
     * @param fraction a value from 0 to 1 indicating the location along the
     *     circumference of the point that should be computed
     * @return a {@code Vec2} object with the (x, y) coordinates of the point
     *     on the ellipse
     */
    private static Vec2 pointOnEllipse(float xrad, float yrad, float fraction)
    {
        double angle = fraction * 2 * Math.PI;
        float x = (float) (xrad * Math.cos(angle));
        float y = (float) (yrad * Math.sin(angle));

        return new Vec2(x, y);
    }
}
