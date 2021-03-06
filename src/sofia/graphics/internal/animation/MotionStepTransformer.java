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

import sofia.graphics.MotionStep;
import sofia.graphics.PropertyTransformer;
import sofia.graphics.Shape;
import android.graphics.PointF;

// ----------------------------------------------------------
public class MotionStepTransformer implements PropertyTransformer
{
    private Shape shape;
    private float lastT;
    private MotionStep step;


    // ----------------------------------------------------------
    public MotionStepTransformer(Shape shape, MotionStep step)
    {
        this.shape = shape;
        this.step = step;
    }


    // ----------------------------------------------------------
    public void onStart()
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    public void transform(float t)
    {
        float timeChange = t - lastT;
        if (timeChange < 0)
        {
            timeChange = 1 + timeChange;
        }

        float fraction = timeChange;

        PointF point = shape.getPosition();
        step.step(fraction, point);
        shape.setPosition(point);
        lastT = t;
    }
}
