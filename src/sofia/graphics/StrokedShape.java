package sofia.graphics;

import sofia.graphics.internal.animation.StrokeWidthTransformer;
import android.graphics.Paint;

//-------------------------------------------------------------------------
/**
 * A {@link Shape} that is stroked when it is drawn.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public abstract class StrokedShape
    extends Shape
{
    //~ Fields ................................................................

    private double strokeWidth;
    private Paint.Cap strokeCap;
    private Paint.Join strokeJoin;
    private double strokeMiter;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
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
    public double getStrokeWidth()
    {
        return strokeWidth;
    }


    // ----------------------------------------------------------
    public void setStrokeWidth(double newStrokeWidth)
    {
        this.strokeWidth = newStrokeWidth;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public Paint.Cap getStrokeCap()
    {
        return strokeCap;
    }


    // ----------------------------------------------------------
    public void setStrokeCap(Paint.Cap newStrokeCap)
    {
        this.strokeCap = newStrokeCap;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public Paint.Join getStrokeJoin()
    {
        return strokeJoin;
    }


    // ----------------------------------------------------------
    public void setStrokeJoin(Paint.Join newStrokeJoin)
    {
        this.strokeJoin = newStrokeJoin;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public double getStrokeMiter()
    {
        return strokeMiter;
    }


    // ----------------------------------------------------------
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
     * Write a one-sentence summary of your class here.
     * Follow it with additional details about its purpose, what abstraction
     * it represents, and how to use it.
     *
     * @author  Tony Allevato
     * @version Dec 4, 2011
     */
    public class Animator<
        AnimatorType extends StrokedShape.Animator<AnimatorType>>
        extends Shape.Animator<AnimatorType>
    {
        //~ Constructors ..........................................................

        // ----------------------------------------------------------
        public Animator(long duration)
        {
            super(duration);
        }


        //~ Methods ...............................................................

        // ----------------------------------------------------------
        @Override
        public StrokedShape getShape()
        {
        	return StrokedShape.this;
        }


        // ----------------------------------------------------------
        @SuppressWarnings("unchecked")
        public AnimatorType strokeWidth(double strokeWidth)
        {
            addTransformer(new StrokeWidthTransformer(getShape(), strokeWidth));
            return (AnimatorType) this;
        }
    }
}
