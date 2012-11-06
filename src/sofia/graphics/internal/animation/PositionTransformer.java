package sofia.graphics.internal.animation;

import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;
import sofia.graphics.internal.GeometryUtils;
import android.graphics.PointF;

// ----------------------------------------------------------
public class PositionTransformer implements PropertyTransformer
{
    private Shape shape;
    private PointF start;
    private PointF end;


    // ----------------------------------------------------------
    public PositionTransformer(Shape shape, PointF end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        //start = shape.getPosition();
        //GeometryUtils.resolveGeometry(end, shape);
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        /*shape.setPosition(new PointF(
            start.x + (end.x - start.x) * t,
            start.y + (end.y - start.y) * t));*/
    }
}
