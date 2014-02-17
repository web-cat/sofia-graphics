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

import android.view.animation.Interpolator;

public class ElasticInOutInterpolator implements Interpolator
{
    // ----------------------------------------------------------
    public float getInterpolation(float t)
    {
        if (t == 0 || t == 1)
        {
            return t;
        }

        float nt = t * 2;
        float p = 0.3f * 1.5f;
        float s = p / 4;

        if (nt < 1)
        {
            nt -= 1;
            return (float) (-0.5 * (Math.pow(2, 10 * nt) * Math.sin((nt - s)
                * (2 * Math.PI) / p)));
        }
        else
        {
            nt -= 1;
            return (float) (0.5 * (Math.pow(2, -10 * nt) * Math.sin((nt - s)
                * (2 * Math.PI) / p)) + 1);
        }
    }
}
