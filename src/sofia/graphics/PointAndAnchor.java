package sofia.graphics;

import sofia.graphics.internal.GeometryUtils;
import android.graphics.RectF;
import android.graphics.PointF;

public class PointAndAnchor implements CopyableGeometry<PointAndAnchor>
{
    private PointF point;
    private Anchor anchor;


    // ----------------------------------------------------------
    public PointAndAnchor(PointF point, Anchor anchor)
    {
        this.point = point;
        this.anchor = anchor;
    }


    // ----------------------------------------------------------
    public PointAndAnchor(PointAndAnchor source)
    {
        this.point = GeometryUtils.copy(source.point);
        this.anchor = source.anchor;
    }


    // ----------------------------------------------------------
    public PointAndAnchor copy()
    {
        return new PointAndAnchor(this);
    }


    // ----------------------------------------------------------
    public PointF getPoint()
    {
        return point;
    }


    // ----------------------------------------------------------
    public Anchor getAnchor()
    {
        return anchor;
    }


    // ----------------------------------------------------------
    public RelativeRect sized(float width, float height)
    {
        return sized(new SizeF(width, height));
    }


    // ----------------------------------------------------------
    public RelativeRect sized(SizeF size)
    {
        return RelativeRect.withPointAnchorAndSize(this, size);
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
    public RectF sizedProportionalTo(
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
    public RectF sizedProportionalToView(double widthRatio, double heightRatio)
    {
        return sized(new RelativeSize(null, widthRatio, heightRatio));
    }
}
