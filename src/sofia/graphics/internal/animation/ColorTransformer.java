package sofia.graphics.internal.animation;

import sofia.graphics.Color;
import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;

// ----------------------------------------------------------
public class ColorTransformer implements PropertyTransformer
{
    private Shape shape;
    private Color start;
    private Color end;


    // ----------------------------------------------------------
    public ColorTransformer(Shape shape, Color end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        start = shape.getColor();
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        int alpha = Math.max(0, Math.min(255,
            (int) (start.alpha() + (end.alpha() - start.alpha()) * t)));
        int red = Math.max(0, Math.min(255,
            (int) (start.red() + (end.red() - start.red()) * t)));
        int green = Math.max(0, Math.min(255,
            (int) (start.green() + (end.green() - start.green()) * t)));
        int blue = Math.max(0, Math.min(255,
            (int) (start.blue() + (end.blue() - start.blue()) * t)));

        shape.setColor(Color.rgb(red, green, blue, alpha));
    }
}