package sofia.graphics;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

// -------------------------------------------------------------------------
/**
 * A shape that is drawn as a rectangle.
 *
 * @author  Tony Allevato
 * @version 2011.11.25
 */
public class RectangleShape extends FillableShape
{
    //~ Fields ................................................................

    private RectF bounds;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a {@code RectangleShape} with default position and size.
     */
    public RectangleShape()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Creates a {@code RectangleShape} with the specified bounds.
     *
     * @param bounds the bounds of the rectangle
     */
    public RectangleShape(RectF bounds)
    {
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    /**
     * Creates a {@code RectangleShape} with the specified bounds.
     *
     * @param left the x-coordinate of the top-left corner of the rectangle
     * @param top the y-coordinate of the top-left corner of the rectangle
     * @param right the x-coordinate of the bottom-right corner of the
     *     rectangle
     * @param bottom the y-coordinate of the bottom-right corner of the
     *     rectangle
     */
    public RectangleShape(float left, float top, float right, float bottom)
    {
        this(new RectF(left, top, right, bottom));
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public RectF getBounds()
    {
        // If the body has been created, update the bounding box using the
        // body's current position.

        Body b2Body = getB2Body();
        if (b2Body != null)
        {
            float hw = bounds.width() / 2;
            float hh = bounds.height() / 2;
            Vec2 center = b2Body.getPosition();
            bounds.offsetTo(center.x - hw, center.y - hh);
        }

        return new RectF(bounds);
    }


    // ----------------------------------------------------------
    public void setBounds(RectF newBounds)
    {
        bounds = new RectF(newBounds);

        updateTransform(bounds.centerX(), bounds.centerY());

        recreateFixtures();
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Canvas canvas)
    {
        RectF bounds = getBounds();

        if (isFilled())
        {
            Paint fillPaint = getFillPaint();
            canvas.drawRect(bounds, fillPaint);
        }

        Paint paint = getPaint();
        canvas.drawRect(bounds, paint);
    }


    // ----------------------------------------------------------
    @Override
    protected void createFixtures()
    {
        PolygonShape box = new PolygonShape();
        box.setAsBox(
                Math.abs(bounds.width() / 2), Math.abs(bounds.height() / 2));

        addFixtureForShape(box);
    }
}
