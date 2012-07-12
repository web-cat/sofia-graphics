package sofia.graphics;

import sofia.graphics.internal.GeometryUtils;
import android.graphics.PointF;
import android.graphics.RectF;
import java.util.Collections;
import java.util.Set;

public class RelativePoint
    extends PointF
    implements ResolvableGeometry<RelativePoint>
{
    //~ Instance/static variables .........................................

    private Anchor anchor;
    private Shape shape;
    private PointF shift;
    private boolean resolved;


    //~ Constructors ......................................................

    // ----------------------------------------------------------
    public RelativePoint(Anchor anchor, Shape shape)
    {
        this.anchor = anchor;
        this.shape = shape;
        this.shift = new PointF(0, 0);

        if (shape != null)
        {
            RectF bounds = shape.getBounds();

            if (GeometryUtils.isGeometryResolved(bounds))
            {
                PointF point = anchor.getPoint(bounds);

                x = point.x;
                y = point.y;

                resolved = true;
            }
        }
    }


    // ----------------------------------------------------------
    public RelativePoint(RelativePoint source)
    {
        this.anchor = source.anchor;
        this.shape = source.shape;
        this.shift = new PointF(source.shift.x, source.shift.y);
        this.resolved = source.resolved;
        this.x = source.x;
        this.y = source.y;
    }


    //~ Methods ...........................................................

    // ----------------------------------------------------------
    public RelativePoint copy()
    {
        return new RelativePoint(this);
    }


    // ----------------------------------------------------------
    public RelativePoint shiftBy(float x, float y)
    {
        RelativePoint newPoint = new RelativePoint(this);
        newPoint.shift.x += x;
        newPoint.shift.y += y;

        if (newPoint.resolved)
        {
            newPoint.x += x;
            newPoint.y += y;
        }

        return newPoint;
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code RectF} object that has the receiver as the origin and the
     * specified point as the extent.
     *
     * @param extent the extent of the rectangle
     * @return a {@code RectF} that has the receiver as the origin and the
     *     specified point as the extent
     */
    public RelativeRect to(PointF extent)
    {
        return RelativeRect.withOriginAndExtent(this, extent);
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code RectF} object that has the receiver as the origin and the
     * specified size.
     *
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @return a {@code RectF} that has the receiver as the origin and the
     *     specified size
     */
    public RelativeRect sized(float width, float height)
    {
        return sized(new SizeF(width, height));
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code RectF} object that has the receiver as the origin and the
     * specified size.
     *
     * @param size the size of the rectangle
     * @return a {@code RectF} that has the receiver as the origin and the
     *     specified size
     */
    public RelativeRect sized(SizeF size)
    {
        return RelativeRect.withOriginAndSize(this, size);
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code RectF} object that has the receiver as the origin and the
     * specified size, proportional to the size of another shape.
     *
     * @param sourceShape the shape that the width and height are proportional
     *     to
     * @param widthRatio the ratio of the desired width compared to the width
     *     of the specified shape; 1.0 is equal, and larger numbers will
     *     increase the size
     * @param heightRatio the ratio of the desired height compared to the
     *     height of the specified shape
     * @return a {@code RectF} that has the receiver as the origin and the
     *     specified size
     */
    public RelativeRect sizedProportionalTo(
        Shape sourceShape, double widthRatio, double heightRatio)
    {
        return sized(new RelativeSize(sourceShape, widthRatio, heightRatio));
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code RectF} object that has the receiver as the origin and the
     * specified size, proportional to the size of the {@link ShapeView} that
     * contains the shape.
     *
     * @param widthRatio the ratio of the desired width compared to the width
     *     of the specified shape; 1.0 is equal, and larger numbers will
     *     increase the size
     * @param heightRatio the ratio of the desired height compared to the
     *     height of the specified shape
     * @return a {@code RectF} that has the receiver as the origin and the
     *     specified size
     */
    public RelativeRect sizedProportionalToView(double widthRatio, double heightRatio)
    {
        return sized(new RelativeSize(null, widthRatio, heightRatio));
    }


    // ----------------------------------------------------------
    public boolean isGeometryResolved()
    {
        return resolved;
    }


    // ----------------------------------------------------------
    public void resolveGeometry(Shape resolveFor)
    {
        RectF rect;

        if (shape != null)
        {
            rect = shape.getBounds();
        }
        else
        {
            rect = resolveFor.getShapeParent().getBounds();
        }

        PointF point = anchor.getPoint(rect);

        x = point.x + shift.x;
        y = point.y + shift.y;

        resolved = true;
    }


    // ----------------------------------------------------------
    public Set<Shape> getShapeDependencies()
    {
        if (!resolved && shape != null)
        {
            return Collections.singleton(shape);
        }
        else
        {
            return Collections.<Shape>emptySet();
        }
    }


    // ----------------------------------------------------------
    @Override
    public String toString()
    {
        if (shape != null)
        {
            String shapeString = shape.getClass().getSimpleName() + "@"
                + System.identityHashCode(shape);

            return "<RelativePoint: " + anchor + " of " + shapeString + ">";
        }
        else
        {
            return "<RelativePoint: " + anchor + " of shape's view>";
        }
    }
}
