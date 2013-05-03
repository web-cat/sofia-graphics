package sofia.graphics;

import sofia.graphics.internal.animation.StrokeWidthTransformer;
import android.graphics.Paint;

//-------------------------------------------------------------------------
/**
 * An abstract class that represents shapes that include a stroke or outline
 * when they are drawn. This class provides getters and setters for the visual
 * appearance of the stroke.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public abstract class StrokedShape
    extends Shape
{
    //~ Fields ................................................................

    //~ Fields ................................................................

    private Stroke stroke;

    private double strokeWidth;
    private Paint.Cap strokeCap;
    private Paint.Join strokeJoin;
    private double strokeMiter;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new {@code StrokedShape}.
     */
    public StrokedShape()
    {
        init();
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    @SuppressWarnings("rawtypes")
    public Animator<?> animate(long duration)
    {
        return new Animator(duration);
    }


    // ----------------------------------------------------------
    /**
     * Gets the width of the stroke.
     *
     * @return the width of the stroke
     */
    public double getStrokeWidth()
    {
        return strokeWidth;
    }


    // ----------------------------------------------------------
    /**
     * Sets the width of the stroke. A width of 0 indicates a "hairline" stroke
     * that will always be rendered with a width of 1 pixel regardless of the
     * scaling applied to the view.
     *
     * @param newStrokeWidth the new width of the stroke
     */
    public void setStrokeWidth(double newStrokeWidth)
    {
        this.strokeWidth = newStrokeWidth;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Gets the stroke's cap, which determines how to treat the beginning and
     * end of the stroke.
     *
     * @return the stroke's cap
     */
    public Paint.Cap getStrokeCap()
    {
        return strokeCap;
    }


    // ----------------------------------------------------------
    /**
     * Sets the stroke's cap, which determines how to treat the beginning and
     * end of the stroke.
     *
     * @param newStrokeCap the stroke's cap
     */
    public void setStrokeCap(Paint.Cap newStrokeCap)
    {
        this.strokeCap = newStrokeCap;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Gets the stroke's join type.
     *
     * @return the stroke's join type
     */
    public Paint.Join getStrokeJoin()
    {
        return strokeJoin;
    }


    // ----------------------------------------------------------
    /**
     * Sets the stroke's join type.
     *
     * @param newStrokeJoin the stroke's join type
     */
    public void setStrokeJoin(Paint.Join newStrokeJoin)
    {
        this.strokeJoin = newStrokeJoin;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Gets the stroke's miter value, which is used to control the behavior of
     * miter joins when the join angle is sharp.
     *
     * @return the stroke's miter value
     */
    public double getStrokeMiter()
    {
        return strokeMiter;
    }


    // ----------------------------------------------------------
    /**
     * Sets the stroke's miter value, which is used to control the behavior of
     * miter joins when the join angle is sharp.
     *
     * @param newStrokeMiter the stroke's miter value
     */
    public void setStrokeMiter(double newStrokeMiter)
    {
        this.strokeMiter = newStrokeMiter;
        conditionallyRepaint();
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    @Override
    protected Paint getPaint()
    {
        Paint paint = super.getPaint();
        paint.setStrokeWidth((float) strokeWidth);

        if (strokeCap != null)
        {
            paint.setStrokeCap(strokeCap);
        }

        if (strokeJoin != null)
        {
            paint.setStrokeJoin(strokeJoin);
        }

        paint.setStrokeMiter((float) strokeMiter);

        return paint;
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private void init()
    {
        strokeWidth = 0;  // 0 == "hair line", 1 pixel regardless of zoom.
        strokeCap = Paint.Cap.BUTT;
        strokeJoin = Paint.Join.MITER;
        strokeMiter = 0.0;
    }


    //~ Animation support classes .............................................

    // -------------------------------------------------------------------------
    /**
     * Provides animation support for shapes. Most uses of this class will not
     * need to reference it directly; for example, an animation can be
     * constructed and played by chaining method calls directly:
     *
     * <pre>
     *     shape.animate(500).color(Color.BLUE).alpha(128).play();</pre>
     *
     * In situations where the type of the class must be referenced directly
     * (for example, when one is passed to an event handler like
     * {@code onAnimationDone}), referring to the name of that type can be
     * somewhat awkward due to the use of some Java generics tricks to ensure
     * that the methods chain properly. In nearly all cases, it is reasonable
     * to use a "?" wildcard in place of the generic parameter:
     *
     * <pre>
     *     Shape.Animator&lt;?&gt; anim = shape.animate(500).color(Color.BLUE);
     *     anim.play();</pre>
     *
     * @param <AnimatorType> the concrete type of the animator
     *
     * @author  Tony Allevato
     * @version 2011.12.11
     */
    public class Animator<
        AnimatorType extends StrokedShape.Animator<AnimatorType>>
        extends Shape.Animator<AnimatorType>
    {
        //~ Constructors ..........................................................

        // ----------------------------------------------------------
        /**
         * Creates a new animator for the specified shape. Users cannot call
         * call this constructor directly; instead, they need to use the
         * {@link StrokedShape#animate(long)} method to get an animator object.
         *
         * @param shape the shape to animate
         * @param duration the length of one pass of the animation, in
         *     milliseconds
         */
        protected Animator(long duration)
        {
            super(duration);
        }


        //~ Methods ...............................................................

        // ----------------------------------------------------------
        /**
         * Gets the shape that the receiver is animating.
         *
         * @return the shape that the receiver is animating
         */
        @Override
        public StrokedShape getShape()
        {
            return StrokedShape.this;
        }


        // ----------------------------------------------------------
        /**
         * Sets the final stroke width of the shape when the animation ends.
         *
         * @param strokeWidth the final stroke width of the shape when the
         *     animation ends
         * @return this animator, for method chaining
         */
        @SuppressWarnings("unchecked")
        public AnimatorType strokeWidth(double strokeWidth)
        {
            addTransformer(new StrokeWidthTransformer(
                    getShape(), strokeWidth));
            return (AnimatorType) this;
        }
    }
}
