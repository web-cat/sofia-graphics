package sofia.graphics;

import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

public class OvalShape extends FillableShape
{
    // ----------------------------------------------------------
    public OvalShape()
    {
        super();
    }


    // ----------------------------------------------------------
    public OvalShape(RectF bounds)
    {
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    @Override
    protected Paint getPaint()
    {
        Paint paint = super.getPaint();
        paint.setAntiAlias(true);
        return paint;
    }


    // ----------------------------------------------------------
    @Override
    protected Paint getFillPaint()
    {
        Paint paint = super.getFillPaint();
        paint.setAntiAlias(true);
        return paint;
    }


    // ----------------------------------------------------------
    @Override
    public boolean contains(float x, float y)
    {
        float[] point = inverseTransformPoint(x, y);

        double rx = getWidth() / 2;
        double ry = getHeight() / 2;

        if (rx == 0 || ry == 0)
        {
            return false;
        }

        double dx = point[0] - (getX() + rx);
        double dy = point[1] - (getY() + ry);

        return (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) <= 1.0;
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Canvas canvas)
    {
        RectF bounds = getBounds();

        if (isFilled())
        {
            Paint fillPaint = getFillPaint();
            canvas.drawOval(bounds, fillPaint);
        }

        Paint paint = getPaint();
        canvas.drawOval(bounds, paint);
    }
}
