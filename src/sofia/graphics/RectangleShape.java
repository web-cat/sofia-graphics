package sofia.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

// -------------------------------------------------------------------------
/**
 * TODO Document
 *
 * @author  Tony Allevato
 * @version 2011.11.25
 */
public class RectangleShape extends FillableShape
{
    // ----------------------------------------------------------
    public RectangleShape(RectF bounds)
    {
        setBounds(bounds);
    }


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
