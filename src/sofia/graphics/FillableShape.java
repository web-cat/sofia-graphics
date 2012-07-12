package sofia.graphics;

import android.view.animation.Interpolator;
import sofia.graphics.animation.ShapeAnimator;
import sofia.graphics.animation.FillableShapeAnimator;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

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
    public FillableShapeAnimator animate(long duration)
    {
        return new FillableShapeAnimator(this, duration);
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
}
