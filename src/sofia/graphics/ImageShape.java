package sofia.graphics;

import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.Canvas;

//-------------------------------------------------------------------------
/**
 * TODO
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public class ImageShape
    extends Shape
{
    private Image image;
    private Rect sourceBounds;


    // ----------------------------------------------------------
    public ImageShape(Image image, RectF bounds)
    {
        this.image = image;
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    public ImageShape(Bitmap bitmap, RectF bounds)
    {
        image = new Image(bitmap);
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    public ImageShape(int bitmapId, RectF bounds)
    {
        image = new Image(bitmapId);
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    public Image getImage()
    {
        return image;
    }


    // ----------------------------------------------------------
    public void setBitmap(Bitmap newBitmap)
    {
        this.image = new Image(newBitmap);
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
        loadBitmapIfNecessary();

        // In some cases, the bitmap may be missing ...
        Bitmap bm = image.asBitmap();
        if (bm != null)
        {
            canvas.drawBitmap(bm, sourceBounds, getBounds(), getPaint());
        }
    }


    // ----------------------------------------------------------
    private void loadBitmapIfNecessary()
    {
        if (image.asBitmap() == null)
        {
            image.resolveAgainstContext(getParentView().getContext());
        }
    }
}
