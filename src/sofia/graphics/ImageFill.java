/*
 * Copyright (C) 2011 Virginia Tech Department of Computer Science
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sofia.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * A fill type that fills a shape with an image.
 *
 * @author Tony Allevato
 */
public class ImageFill extends Fill
{
    //~ Fields ................................................................

    private Image image;
    private boolean clip;
    private Paint paint;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public ImageFill(String imageName)
    {
        this(new Image(imageName));
    }


    // ----------------------------------------------------------
    public ImageFill(String imageName, boolean clip)
    {
        this(new Image(imageName), clip);
    }


    // ----------------------------------------------------------
    public ImageFill(Image image)
    {
        this(image, false);
    }


    // ----------------------------------------------------------
    /**
     * Creates a new fill that will fill a region with the specified image.
     *
     * @param image the {@link Image} to use
     */
    public ImageFill(Image image, boolean clip)
    {
        this.image = image;
        this.clip = clip;

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the {@link Image} used by this fill.
     *
     * @return the {@code Image} used by this fill
     */
    public Image getImage()
    {
        return image;
    }


    // ----------------------------------------------------------
    /**
     * Fills the specified region on a canvas.
     *
     * @param drawing the {@link Drawing} object
     * @param bounds the bounds
     */
    @Override
    public void fillRect(Drawing drawing, int alpha, RectF bounds)
    {
        resolveBitmapIfNecessary(drawing.getContext());

        // In some cases, the bitmap may be missing...
        Bitmap bm = image.asBitmap();
        if (bm != null)
        {
            int oldAlpha = paint.getAlpha();
            paint.setAlpha(alpha);

            Canvas canvas = drawing.getCanvas();

            RectF sortedBounds = new RectF(bounds);
            sortedBounds.sort();

            // If the coordinate system is flipped in either direction, we need
            // to do another temporary flip to ensure that the images are drawn
            // in their native orientation.

            CoordinateSystem cs = drawing.getCoordinateSystem();
            boolean flipX = cs.isFlippedX();
            boolean flipY = cs.isFlippedY();

            if (flipX || flipY)
            {
                canvas.save();

                Matrix matrix = new Matrix();
                matrix.setScale(flipX ? -1 : 1, flipY ? -1 : 1);
                matrix.postTranslate(
                        flipX ? sortedBounds.left + sortedBounds.right : 0,
                        flipY ? sortedBounds.top + sortedBounds.bottom : 0);
                canvas.concat(matrix);
            }

            canvas.drawBitmap(bm, null, sortedBounds, paint);
            paint.setAlpha(oldAlpha);

            if (flipX || flipY)
            {
                canvas.restore();
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Fills the specified region on a canvas.
     *
     * @param drawing the {@link Drawing} object
     * @param bounds the bounds
     */
    @Override
    public void fillOval(Drawing drawing, int alpha, RectF bounds)
    {
        fillRect(drawing, alpha, bounds);
    }


    // ----------------------------------------------------------
    /**
     * Fills the specified region on a canvas.
     *
     * @param drawing the {@link Drawing} object
     * @param bounds the bounds
     */
    @Override
    public void fillPolygon(Drawing drawing, int alpha, Polygon polygon,
            PointF origin)
    {
        if (clip)
        {
            drawing.getCanvas().save();
            drawing.getCanvas().clipPath(pathForPolygon(polygon, origin));
        }

        RectF bounds = polygon.getBounds();
        bounds.offset(origin.x, origin.y);
        fillRect(drawing, alpha, bounds);

        if (clip)
        {
            drawing.getCanvas().restore();
        }
    }


    // ----------------------------------------------------------
    private static Path pathForPolygon(Polygon polygon, PointF origin)
    {
        Path path = new Path();
        path.incReserve(polygon.size());

        for (int i = 0; i < polygon.size(); i++)
        {
            PointF pt = polygon.get(i);
            if (i == 0)
            {
                path.moveTo(origin.x + pt.x, origin.y + pt.y);
            }
            else
            {
                path.lineTo(origin.x + pt.x, origin.y + pt.y);
            }
        }

        path.close();

        return path;
    }


    // ----------------------------------------------------------
    private void resolveBitmapIfNecessary(Context context)
    {
        if (image != null && image.asBitmap() == null)
        {
            image.resolveAgainstContext(context);
        }
    }
}
