package sofia.graphics;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Canvas;

public class BitmapShape extends Shape
{
    private Bitmap bitmap;
    private int bitmapId;
    private Rect sourceBounds;


    // ----------------------------------------------------------
    public BitmapShape(Bitmap bitmap, RectF bounds)
    {
        this.bitmap = bitmap;
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    public BitmapShape(int bitmapId, RectF bounds)
    {
        this.bitmapId = bitmapId;
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    public Bitmap getBitmap()
    {
        return bitmap;
    }


    // ----------------------------------------------------------
    public void setBitmap(Bitmap newBitmap)
    {
        this.bitmap = newBitmap;
        sourceBounds = null;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public Rect getSourceBounds()
    {
        return sourceBounds;
    }


    // ----------------------------------------------------------
    public void setSourceBounds(Rect newSourceBounds)
    {
        this.sourceBounds = newSourceBounds;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public void setSourceBounds(int left, int top, int right, int bottom)
    {
        setSourceBounds(new Rect(left, top, right, bottom));
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Canvas canvas)
    {
        if (bitmap == null && bitmapId != 0)
        {
            loadBitmapFromResource();
        }

        Paint paint = getPaint();
        canvas.drawBitmap(bitmap, sourceBounds, getBounds(), paint);
    }


    // ----------------------------------------------------------
    private void loadBitmapFromResource()
    {
        Resources res = getParentView().getResources();
        bitmap = BitmapFactory.decodeResource(res, bitmapId);
    }
}
