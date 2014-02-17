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

import sofia.graphics.PropertyTransformer;
import sofia.graphics.TextShape;

// ----------------------------------------------------------
public class TypeSizeTransformer implements PropertyTransformer
{
    private TextShape shape;
    private double    start;
    private double    end;


    // ----------------------------------------------------------
    public TypeSizeTransformer(TextShape shape, double end)
    {
        this.shape = shape;
        this.end = end;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        start = shape.getTypeSize();
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        shape.setTypeSize((int)(start + (end - start) * t));
    }
}