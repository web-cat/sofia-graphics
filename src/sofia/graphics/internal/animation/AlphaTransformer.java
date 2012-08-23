package sofia.graphics.internal.animation;

import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;

// ----------------------------------------------------------
public class AlphaTransformer implements PropertyTransformer
{
    private Shape shape;
    private int start;
    private int end;


    // ----------------------------------------------------------
    public AlphaTransformer(Shape shape, int end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        start = shape.getAlpha();
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        int value = Math.max(0, Math.min(255,
            (int) (start + (end - start) * t)));
        shape.setAlpha(value);
    }
}