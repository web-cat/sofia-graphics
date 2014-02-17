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

package sofia.graphics.internal.animation;

import sofia.graphics.Color;
import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;

// ----------------------------------------------------------
public class ColorTransformer implements PropertyTransformer
{
    private Shape shape;
    private Color start;
    private Color end;


    // ----------------------------------------------------------
    public ColorTransformer(Shape shape, Color end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        start = shape.getColor();
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        int alpha = Math.max(0, Math.min(255,
            (int) (start.alpha() + (end.alpha() - start.alpha()) * t)));
        int red = Math.max(0, Math.min(255,
            (int) (start.red() + (end.red() - start.red()) * t)));
        int green = Math.max(0, Math.min(255,
            (int) (start.green() + (end.green() - start.green()) * t)));
        int blue = Math.max(0, Math.min(255,
            (int) (start.blue() + (end.blue() - start.blue()) * t)));

        shape.setColor(Color.rgb(red, green, blue, alpha));
    }
}