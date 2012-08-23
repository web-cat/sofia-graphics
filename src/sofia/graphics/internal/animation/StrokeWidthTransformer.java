package sofia.graphics.internal.animation;

import sofia.graphics.PropertyTransformer;
import sofia.graphics.StrokedShape;

// ----------------------------------------------------------
public class StrokeWidthTransformer implements PropertyTransformer
{
    private StrokedShape shape;
    private double start;
    private double end;


    // ----------------------------------------------------------
    public StrokeWidthTransformer(StrokedShape shape, double end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        start = shape.getStrokeWidth();
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        int value = Math.max(0, (int) (start + (end - start) * t));
        shape.setStrokeWidth(value);
    }
}