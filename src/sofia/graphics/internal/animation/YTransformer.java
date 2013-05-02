package sofia.graphics.internal.animation;

import sofia.graphics.Geometry;
import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;
import android.graphics.PointF;

// ----------------------------------------------------------
public class YTransformer implements PropertyTransformer
{
    private Shape shape;
    private PointF start;
    private float end;


    // ----------------------------------------------------------
    public YTransformer(Shape shape, float end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        start = Geometry.clone(shape.getPosition());
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        shape.setPosition(new PointF(
            start.x, start.y + (end - start.y) * t));
    }
}
