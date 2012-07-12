package sofia.graphics.animation;

import android.view.animation.Interpolator;
import sofia.graphics.StrokedShape;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here.
 * Follow it with additional details about its purpose, what abstraction
 * it represents, and how to use it.
 *
 * @author flobbster
 * @version Dec 4, 2011
 */
public class StrokedShapeAnimator<
    ShapeType extends StrokedShape,
    AnimatorType extends StrokedShapeAnimator<ShapeType, AnimatorType>>
    extends ShapeAnimator<ShapeType, StrokedShapeAnimator<ShapeType, AnimatorType>>
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public StrokedShapeAnimator(ShapeType shape, long duration)
    {
        super(shape, duration);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public AnimatorType strokeWidth(double strokeWidth)
    {
        addTransformer(new StrokeWidthTransformer(getShape(), strokeWidth));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    private class StrokeWidthTransformer implements PropertyTransformer
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
}
