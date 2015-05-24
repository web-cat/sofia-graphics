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

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * The abstract base class for all shape stroke types, such as color strokes
 * and image strokes.
 *
 * @author Tony Allevato
 */
public abstract class Stroke
{
    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Draws the specified rectangle on a canvas.
     */
    public void drawRect(Drawing drawing, int alpha, RectF bounds)
    {
        Paint paint = getPaint();
        int oldAlpha = paint.getAlpha();
        paint.setAlpha(alpha);
        drawing.getCanvas().drawRect(bounds, paint);
        paint.setAlpha(oldAlpha);
    }


    // ----------------------------------------------------------
    /**
     * Draws the specified rectangle on a canvas.
     */
    public void drawOval(Drawing drawing, int alpha, RectF bounds)
    {
        Paint paint = getPaint();
        int oldAlpha = paint.getAlpha();
        paint.setAlpha(alpha);
        drawing.getCanvas().drawOval(bounds, paint);
        paint.setAlpha(oldAlpha);
    }


    // ----------------------------------------------------------
    /**
     * Draws the specified polygon on a canvas.
     */
    public void drawPolygon(Drawing drawing, int alpha,
            Polygon polygon, PointF origin)
    {
        Paint paint = getPaint();
        int oldAlpha = paint.getAlpha();
        paint.setAlpha(alpha);

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

        drawing.getCanvas().drawPath(path, paint);
        paint.setAlpha(oldAlpha);
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code Paint} object that will be used by the {@code draw*}
     * methods to draw the stroke around a shape. This method does not need to
     * be implemented if the subclass overrides the other {@code draw*} methods
     * in their entirety.
     *
     * @return a {@code Paint} object used by the other methods
     */
    protected Paint getPaint()
    {
        return null;
    }
}
