package sofia.graphics;

import sofia.graphics.internal.GeometryUtils;
import android.graphics.RectF;
import android.graphics.PointF;
import java.util.HashSet;
import java.util.Set;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here.
 * Follow it with additional details about its purpose, what abstraction
 * it represents, and how to use it.
 *
 * @author  Tony Allevato
   @version 2011.11.24
 */
public abstract class RelativeRect
    extends RectF
    implements ResolvableGeometry<RelativeRect>
{
    protected PointF shift;


    // ----------------------------------------------------------
    protected RelativeRect()
    {
        super(Float.NaN, Float.NaN, Float.NaN, Float.NaN);

        shift = new PointF();
    }


    // ----------------------------------------------------------
    public RelativeRect shiftBy(float dx, float dy)
    {
        RelativeRect newRect = copy();
        newRect.shift.x += dx;
        newRect.shift.y += dy;

        if (isGeometryResolved())
        {
            newRect.left += dx;
            newRect.right += dx;
            newRect.top += dy;
            newRect.bottom += dy;
        }

        return newRect;
    }


    // ----------------------------------------------------------
    static RelativeRect withOriginAndExtent(PointF origin, PointF extent)
    {
        return new WithOriginAndExtent(origin, extent);
    }


    // ----------------------------------------------------------
    static RelativeRect withOriginAndSize(PointF origin, SizeF size)
    {
        return new WithOriginAndSize(origin, size);
    }


    // ----------------------------------------------------------
    static RelativeRect withPointAnchorAndSize(
        PointAndAnchor pointAndAnchor, SizeF size)
    {
        return new WithPointAnchorAndSize(pointAndAnchor, size);
    }


    // ----------------------------------------------------------
    private static class WithOriginAndExtent extends RelativeRect
    {
        private PointF origin;
        private PointF extent;


        public WithOriginAndExtent(PointF origin, PointF extent)
        {
            this.origin = origin;
            this.extent = extent;

            if (GeometryUtils.isGeometryResolved(origin))
            {
                this.left = origin.x;
                this.top = origin.y;
            }

            if (GeometryUtils.isGeometryResolved(extent))
            {
                this.right = extent.x;
                this.bottom = extent.y;
            }
        }


        public RelativeRect copy()
        {
            return new WithOriginAndExtent(
                GeometryUtils.copy(origin), GeometryUtils.copy(extent));
        }


        public Set<Shape> getShapeDependencies()
        {
            HashSet<Shape> shapes = new HashSet<Shape>();
            shapes.addAll(GeometryUtils.getShapeDependencies(origin));
            shapes.addAll(GeometryUtils.getShapeDependencies(extent));
            return shapes;
        }

        public boolean isGeometryResolved()
        {
            return GeometryUtils.isGeometryResolved(origin)
                && GeometryUtils.isGeometryResolved(extent);
        }

        public void resolveGeometry(Shape resolveFor)
        {
            GeometryUtils.resolveGeometry(origin, resolveFor);
            GeometryUtils.resolveGeometry(extent, resolveFor);

            this.left = origin.x + shift.x;
            this.top = origin.y + shift.y;
            this.right = extent.x + shift.x;
            this.bottom = extent.y + shift.y;
        }

        @Override
        public String toString()
        {
            return "<DynamicRect: " + origin + " to " + extent + ">";
        }
    }


    // ----------------------------------------------------------
    private static class WithOriginAndSize extends RelativeRect
    {
        private PointF origin;
        private SizeF size;

        public WithOriginAndSize(PointF origin, SizeF size)
        {
            this.origin = origin;
            this.size = size;

            if (GeometryUtils.isGeometryResolved(origin))
            {
                this.left = origin.x;
                this.top = origin.y;

                if (GeometryUtils.isGeometryResolved(size))
                {
                    this.right = this.left + size.width;
                    this.bottom = this.top + size.height;
                }
            }
        }

        public RelativeRect copy()
        {
            return new WithOriginAndSize(
                GeometryUtils.copy(origin), GeometryUtils.copy(size));
        }

        public Set<Shape> getShapeDependencies()
        {
            HashSet<Shape> shapes = new HashSet<Shape>();
            shapes.addAll(GeometryUtils.getShapeDependencies(origin));
            shapes.addAll(GeometryUtils.getShapeDependencies(size));
            return shapes;
        }

        public boolean isGeometryResolved()
        {
            return GeometryUtils.isGeometryResolved(origin)
                && GeometryUtils.isGeometryResolved(size);
        }

        public void resolveGeometry(Shape resolveFor)
        {
            GeometryUtils.resolveGeometry(origin, resolveFor);
            GeometryUtils.resolveGeometry(size, resolveFor);

            this.left = origin.x + shift.x;
            this.top = origin.y + shift.y;
            this.right = this.left + size.width;
            this.bottom = this.top + size.height;
        }

        @Override
        public String toString()
        {
            return "<RelativeRect: " + origin + ", size " + size + ">";
        }
    }


    // ----------------------------------------------------------
    private static class WithPointAnchorAndSize extends RelativeRect
    {
        private PointAndAnchor pointAndAnchor;
        private SizeF size;

        public WithPointAnchorAndSize(PointAndAnchor pointAndAnchor, SizeF size)
        {
            this.pointAndAnchor = pointAndAnchor;
            this.size = size;

            if (GeometryUtils.isGeometryResolved(pointAndAnchor.getPoint())
                && GeometryUtils.isGeometryResolved(size))
            {
                compute();
            }
        }

        public RelativeRect copy()
        {
            return new WithPointAnchorAndSize(
                GeometryUtils.copy(pointAndAnchor), GeometryUtils.copy(size));
        }

        public Set<Shape> getShapeDependencies()
        {
            HashSet<Shape> shapes = new HashSet<Shape>();
            shapes.addAll(GeometryUtils.getShapeDependencies(
                pointAndAnchor.getPoint()));
            shapes.addAll(GeometryUtils.getShapeDependencies(size));
            return shapes;
        }

        public boolean isGeometryResolved()
        {
            return GeometryUtils.isGeometryResolved(
                pointAndAnchor.getPoint())
                && GeometryUtils.isGeometryResolved(size);
        }

        public void resolveGeometry(Shape resolveFor)
        {
            GeometryUtils.resolveGeometry(
                pointAndAnchor.getPoint(), resolveFor);
            GeometryUtils.resolveGeometry(size, resolveFor);

            compute();
        }

        private void compute()
        {
            PointF anchorPt = pointAndAnchor.getAnchor().getPoint(
                new RectF(0, 0, size.width, size.height));

            this.left = pointAndAnchor.getPoint().x - anchorPt.x + shift.x;
            this.top = pointAndAnchor.getPoint().y - anchorPt.y + shift.y;
            this.right = this.left + size.width;
            this.bottom = this.top + size.height;
        }

        @Override
        public String toString()
        {
            return "<RelativeRect: " + pointAndAnchor.getAnchor()
                + " anchored at " + pointAndAnchor.getPoint()
                + ", size " + size + ">";
        }
    };
}
