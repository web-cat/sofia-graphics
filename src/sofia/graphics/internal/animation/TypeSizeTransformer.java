package sofia.graphics.internal.animation;

import sofia.graphics.PropertyTransformer;
import sofia.graphics.TextShape;

// ----------------------------------------------------------
public class TypeSizeTransformer implements PropertyTransformer
{
    private TextShape shape;
    private double    start;
    private double    end;


    // ----------------------------------------------------------
    public TypeSizeTransformer(TextShape shape, double end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        start = shape.getTypeSize();
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        shape.setTypeSize((int)(start + (end - start) * t));
    }
}