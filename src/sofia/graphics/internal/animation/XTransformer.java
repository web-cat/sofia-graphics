package sofia.graphics.internal.animation;

import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;
import android.graphics.PointF;

// ----------------------------------------------------------
public class XTransformer implements PropertyTransformer
{
    private Shape shape;
    private PointF start;
    private float end;


    // ----------------------------------------------------------
    public XTransformer(Shape shape, float end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        //start = GeometryUtils.copy(shape.getPosition());
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        /*shape.setPosition(new PointF(
            start.x + (end - start.x) * t, start.y));*/
    }
}
