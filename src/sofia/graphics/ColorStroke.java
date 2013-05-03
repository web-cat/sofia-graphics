package sofia.graphics;

import android.graphics.Paint;

//-------------------------------------------------------------------------
/**
 * A stroke type that strokes a shape with a solid color. Most users will
 * probably not use this class directly, but will instead use the
 * {@link StrokedShape#setStrokeColor(Color)} convenience method instead.
 *
 * @author Tony Allevato
 * @version 2013.04.16
 */
public class ColorStroke extends Stroke
{
    //~ Fields ................................................................

    private Color color;
    private Paint paint;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new fill that will fill a region with the specified color.
     *
     * @param color the {@link Color} to use
     */
    public ColorStroke(Color color)
    {
        this.color = color;

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color.toRawColor());
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the {@link Color} used by this fill.
     *
     * @return the {@code Color} used by this fill
     */
    public Color getColor()
    {
        return color;
    }


    // ----------------------------------------------------------
    @Override
    public Paint getPaint()
    {
        return paint;
    }
}
