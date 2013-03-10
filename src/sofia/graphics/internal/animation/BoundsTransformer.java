package sofia.graphics.internal.animation;

import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;
import android.graphics.RectF;

// ----------------------------------------------------------
public class BoundsTransformer implements PropertyTransformer
{
    private Shape shape;
    private RectF start;
    private RectF end;


    // ----------------------------------------------------------
    public BoundsTransformer(Shape shape, RectF end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        start = shape.getBounds();
        //GeometryUtils.resolveGeometry(end, shape);
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        shape.setBounds(new RectF(
            start.left + (end.left - start.left) * t,
            start.top + (end.top - start.top) * t,
            start.right + (end.right - start.right) * t,
            start.bottom + (end.bottom - start.bottom) * t));
    }
}
