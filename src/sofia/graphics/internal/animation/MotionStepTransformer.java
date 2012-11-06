package sofia.graphics.internal.animation;

import sofia.graphics.MotionStep;
import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;
import android.graphics.PointF;

// ----------------------------------------------------------
public class MotionStepTransformer implements PropertyTransformer
{
    private Shape shape;
    private float lastT;
    private MotionStep step;


    // ----------------------------------------------------------
    public MotionStepTransformer(Shape shape, MotionStep step)
    {
        this.shape = shape;
        this.step = step;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        float timeChange = t - lastT;
        if (timeChange < 0)
        {
            timeChange = 1 + timeChange;
        }

        float fraction = timeChange;

        /*PointF point = shape.getPosition();
        step.step(fraction, point);
        shape.setPosition(point);*/
        lastT = t;
    }
}
