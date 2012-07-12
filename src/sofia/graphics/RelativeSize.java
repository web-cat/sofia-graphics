package sofia.graphics;

import android.graphics.RectF;
import java.util.Collections;
import java.util.Set;

public class RelativeSize
    extends SizeF
    implements ResolvableGeometry<RelativeSize>
{
    private Shape relativeTo;
    private double widthRatio;
    private double heightRatio;
    private boolean resolved;


    // ----------------------------------------------------------
    public RelativeSize(
        Shape relativeTo, double widthRatio, double heightRatio)
    {
        super(Float.NaN, Float.NaN);

        this.relativeTo = relativeTo;
        this.widthRatio = widthRatio;
        this.heightRatio = heightRatio;
    }


    // ----------------------------------------------------------
    public RelativeSize(RelativeSize source)
    {
        super(Float.NaN, Float.NaN);

        this.relativeTo = source.relativeTo;
        this.widthRatio = source.widthRatio;
        this.heightRatio = source.heightRatio;
        this.resolved = source.resolved;
        this.width = source.width;
        this.height = source.height;
    }


    // ----------------------------------------------------------
    public RelativeSize copy()
    {
        return new RelativeSize(this);
    }


    // ----------------------------------------------------------
    public void resolveGeometry(Shape resolveFor)
    {
        RectF bounds;

        if (relativeTo != null)
        {
            bounds = relativeTo.getBounds();
        }
        else
        {
            bounds = resolveFor.getShapeParent().getBounds();
        }

        this.width = (float) (bounds.width() * widthRatio);
        this.height = (float) (bounds.height() * heightRatio);

        resolved = true;
    }


    // ----------------------------------------------------------
    public boolean isGeometryResolved()
    {
        return resolved;
    }


    // ----------------------------------------------------------
    public Set<Shape> getShapeDependencies()
    {
        if (relativeTo != null)
        {
            return Collections.singleton(relativeTo);
        }
        else
        {
            return Collections.<Shape>emptySet();
        }
    }
}
