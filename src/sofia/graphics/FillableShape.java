package sofia.graphics;

import sofia.graphics.internal.animation.FillColorTransformer;
import android.graphics.Paint;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here.
 * Follow it with additional details about its purpose, what abstraction
 * it represents, and how to use it.
 *
 * @author  Tony Allevato
 * @version 2011.11.27
 */
public abstract class FillableShape extends StrokedShape
{
    //~ Instance/static variables .............................................

    private boolean filled;
    private Color fillColor;
    private boolean fillColorSet;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new FillableShape object.
     *
     * @param canvas
     * @param bounds
     */
    public FillableShape()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @SuppressWarnings("rawtypes")
    public Animator<?> animate(long duration)
    {
        return new Animator(duration);
    }


    // ----------------------------------------------------------
    public boolean isFilled()
    {
        return filled;
    }


    // ----------------------------------------------------------
    public void setFilled(boolean newFilled)
    {
        this.filled = newFilled;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public Color getFillColor()
    {
        if (fillColorSet)
        {
            return fillColor;
        }
        else
        {
            return getColor();
        }
    }


    // ----------------------------------------------------------
    public void setFillColor(Color newFillColor)
    {
        this.fillColor = newFillColor;
        this.fillColorSet = true;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    protected Paint getFillPaint()
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(getFillColor().toRawColor());
        return paint;
    }


    //~ Animation support classes .............................................

    // ----------------------------------------------------------
    public class Animator<
	    ConcreteType extends FillableShape.Animator<ConcreteType>>
	    extends StrokedShape.Animator<ConcreteType>
	{
	    //~ Constructors ......................................................
	
	    // ----------------------------------------------------------
	    public Animator(long duration)
	    {
	        super(duration);
	    }
	
	
	    //~ Methods ...........................................................
	
	    // ----------------------------------------------------------
	    @Override
	    public FillableShape getShape()
	    {
	    	return FillableShape.this;
	    }


	    // ----------------------------------------------------------
	    @SuppressWarnings("unchecked")
	    public ConcreteType fillColor(Color color)
	    {
	        addTransformer(new FillColorTransformer(getShape(), color));
	        return (ConcreteType) this;
	    }
	}
}
