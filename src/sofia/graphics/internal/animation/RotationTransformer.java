package sofia.graphics.internal.animation;

import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;

// ----------------------------------------------------------
public class RotationTransformer implements PropertyTransformer
{
    private Shape shape;
    private float start;
    private float end;


    // ----------------------------------------------------------
    public RotationTransformer(Shape shape, float end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        start = shape.getRotation();
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        shape.setRotation(start + (end - start) * t);
    }
}