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

//-------------------------------------------------------------------------
/**
 * A stroke type that strokes a shape with a solid color. Most users will
 * probably not use this class directly, but will instead use the
 * {@link StrokedShape#setStrokeColor(Color)} convenience method instead.
 *
 * @author Tony Allevato
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
