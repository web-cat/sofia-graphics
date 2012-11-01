package sofia.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

// -------------------------------------------------------------------------
/**
 * A shape that is drawn as a rectangle.
 *
 * @author  Tony Allevato
 * @version 2011.11.25
 */
public class RectangleShape extends FillableShape
{
	//~ Constructors ..........................................................

    // ----------------------------------------------------------
	/**
	 * Creates a {@code RectangleShape} with default position and size.
	 */
	public RectangleShape()
	{
		super();
	}


    // ----------------------------------------------------------
	/**
	 * Creates a {@code RectangleShape} with the specified bounds.
	 * 
	 * @param bounds the bounds of the rectangle
	 */
    public RectangleShape(RectF bounds)
    {
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    /**
     * Creates a {@code RectangleShape} with the specified bounds.
     * 
     * @param x1 the x-coordinate of the top-left corner of the rectangle
     * @param y1 the y-coordinate of the top-left corner of the rectangle
     * @param x2 the x-coordinate of the bottom-right corner of the rectangle
     * @param y2 the y-coordinate of the bottom-right corner of the rectangle
     */
    public RectangleShape(float x1, float y1, float x2, float y2)
    {
    	this(new RectF(x1, y1, x2, y2));
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public void draw(Canvas canvas)
    {
        RectF bounds = getBounds();

        if (isFilled())
        {
            Paint fillPaint = getFillPaint();
            canvas.drawRect(bounds, fillPaint);
        }

        Paint paint = getPaint();
        canvas.drawRect(bounds, paint);
    }
}
