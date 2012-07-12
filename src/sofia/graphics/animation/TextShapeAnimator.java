package sofia.graphics.animation;

import sofia.graphics.TextShape;
import android.view.animation.Interpolator;
import sofia.graphics.StrokedShape;

public class TextShapeAnimator<
    ShapeType extends TextShape,
    AnimatorType extends TextShapeAnimator<ShapeType, AnimatorType>>
    extends ShapeAnimator<ShapeType, AnimatorType>
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public TextShapeAnimator(ShapeType shape, long duration)
    {
        super(shape, duration);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public AnimatorType typeSize(double typeSize)
    {
        addTransformer(new TypeSizeTransformer(getShape(), typeSize));
        return (AnimatorType)this;
    }


    // ----------------------------------------------------------
    private class TypeSizeTransformer implements PropertyTransformer
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
}
