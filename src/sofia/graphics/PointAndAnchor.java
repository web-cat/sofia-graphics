package sofia.graphics;

import sofia.graphics.internal.GeometryUtils;
import android.graphics.RectF;
import android.graphics.PointF;

//-------------------------------------------------------------------------
/**
 * A {@code PointAndAnchor} encapsulates a {@code PointF} denoting a location
 * on a shape canvas and an {@code Anchor} that indicates how a shape should be
 * anchored to that point.
 *
 * @author  Tony Allevato
 * @version 2012.09.29
 */
public class PointAndAnchor implements CopyableGeometry<PointAndAnchor>
{
    //~ Fields ................................................................

    private PointF point;
    private Anchor anchor;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new {@code PointAndAnchor} with the specified point and
     * anchor.
     *
     * @param point the {@link PointF}
     * @param anchor the {@link Anchor}
     */
    public PointAndAnchor(PointF point, Anchor anchor)
    {
        this.point = point;
        this.anchor = anchor;
    }


    // ----------------------------------------------------------
    /**
     * Creates a new {@code PointAndAnchor} that is a copy of the specified
     * one.
     *
     * @param source the {@code PointAndAnchor} to copy
     */
    public PointAndAnchor(PointAndAnchor source)
    {
        this.point = GeometryUtils.copy(source.point);
        this.anchor = source.anchor;
    }


    // ----------------------------------------------------------
    /**
     * Gets a copy of this {@code PointAndAnchor}.
     *
     * @return a copy of the receiver
     */
    public PointAndAnchor copy()
    {
        return new PointAndAnchor(this);
    }


    // ----------------------------------------------------------
    /**
     * Gets the point represented by the receiver.
     *
     * @return the point represented by the receiver
     */
    public PointF getPoint()
    {
        return point;
    }


    // ----------------------------------------------------------
    /**
     * Gets the anchor represented by the receiver.
     *
     * @return the anchor represented by the receiver
     */
    public Anchor getAnchor()
    {
        return anchor;
    }


    // ----------------------------------------------------------
    /**
     * Returns a {@link RectF}-compatible object that is positioned with
     * respect to this point and anchor and has the specified width and height.
     *
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     *
     * @return a {@link RectF}-compatible object
     */
    public RectF sized(float width, float height)
    {
        return sized(new SizeF(width, height));
    }


    // ----------------------------------------------------------
    /**
     * Returns a {@link RectF}-compatible object that is positioned with
     * respect to this point and anchor and has the specified size.
     *
     * @param size the size of the rectangle
     *
     * @return a {@link RectF}-compatible object
     */
    public RectF sized(SizeF size)
    {
        return new RectF(point.x, point.y,
                point.x + size.width, point.y + size.height);
        //return RelativeRect.withPointAnchorAndSize(this, size);
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code RectF}-compatible object that is positioned with respect
     * to this point and anchor and has the specified width and height,
     * calculated as proportions of the size of another shape.
     *
     * @param sourceShape the shape that the width and height are proportional
     *     to
     * @param widthRatio the ratio of the desired width compared to the width
     *     of the specified shape; 1.0 is equal, and larger numbers will
     *     increase the size
     * @param heightRatio the ratio of the desired height compared to the
     *     height of the specified shape
     * @return a {@code RectF}-compatible object that has the receiver as the
     *     origin and the specified size
     */
    public RectF sizedProportionalTo(
        Shape sourceShape, double widthRatio, double heightRatio)
    {
        return null;
        //return sized(new RelativeSize(sourceShape, widthRatio, heightRatio));
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
    public RectF sizedProportionalToView(double widthRatio, double heightRatio)
    {
        return null;
        //return sized(new RelativeSize(null, widthRatio, heightRatio));
    }
}
