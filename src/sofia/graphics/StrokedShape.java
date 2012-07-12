package sofia.graphics;

import android.view.animation.Interpolator;
import sofia.graphics.animation.ShapeAnimator;
import sofia.graphics.animation.StrokedShapeAnimator;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

public abstract class StrokedShape extends Shape
{
    private double strokeWidth;
    private Paint.Cap strokeCap;
    private Paint.Join strokeJoin;
    private double strokeMiter;


    public StrokedShape()
    {
        init();
    }


    private void init()
    {
        strokeWidth = 1.0;
        strokeCap = Paint.Cap.BUTT;
        strokeJoin = Paint.Join.MITER;
        strokeMiter = 0.0;
    }


    @SuppressWarnings("rawtypes")
    public StrokedShapeAnimator animate(long duration)
    {
        return new StrokedShapeAnimator(this, duration);
    }


    public double getStrokeWidth()
    {
        return strokeWidth;
    }


    public void setStrokeWidth(double newStrokeWidth)
    {
        this.strokeWidth = newStrokeWidth;
        conditionallyRepaint();
    }


    public Paint.Cap getStrokeCap()
    {
        return strokeCap;
    }


    public void setStrokeCap(Paint.Cap newStrokeCap)
    {
        this.strokeCap = newStrokeCap;
        conditionallyRepaint();
    }


    public Paint.Join getStrokeJoin()
    {
        return strokeJoin;
    }


    public void setStrokeJoin(Paint.Join newStrokeJoin)
    {
        this.strokeJoin = newStrokeJoin;
        conditionallyRepaint();
    }


    public double getStrokeMiter()
    {
        return strokeMiter;
    }


    public void setStrokeMiter(double newStrokeMiter)
    {
        this.strokeMiter = newStrokeMiter;
        conditionallyRepaint();
    }


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
}
