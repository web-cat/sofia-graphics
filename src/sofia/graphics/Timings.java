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

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

// -------------------------------------------------------------------------
/**
 * This class provides static helper methods that provide friendlier names for
 * timing functions (interpolators) used in animations. For example, one can
 * write "{@code Timings.easeInOut()}" instead of
 * "{@code new AcceleratingDeceleratingInterpolator()}". The former notation
 * can be shorted even further by writing
 * "{@code import static sofia.graphics.Timings.*}".
 *
 * @author Tony Allevato
 */
public class Timings
{
    //~ Fields ................................................................

    // These fields act as singletons for the interpolators returned by the
    // no-argument methods below. The versions of the methods that take
    // arguments, on the other hand, do not perform any caching; they simply
    // return a new interpolator each time they are called.

    private static AccelerateDecelerateInterpolator easeInOut;
    private static AccelerateInterpolator easeIn;
    private static DecelerateInterpolator easeOut;
    private static LinearInterpolator linear;
    private static AnticipateInterpolator backIn;
    private static OvershootInterpolator backOut;
    private static AnticipateOvershootInterpolator backInOut;
    private static BounceInterpolator bounce;
    private static ElasticInInterpolator elasticIn;
    private static ElasticOutInterpolator elasticOut;
    private static ElasticInOutInterpolator elasticInOut;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the rate of change starts and ends slowly but
     * speeds up through the middle. This is the default timing function for
     * any animation that does not specify an alternative.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = 0.5 + cos((1 + t) * pi) / 2</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * This timing function corresponds to
     * {@link AccelerateDecelerateInterpolator}.
     * </p>
     *
     * @return a timing function that eases in and eases out an animation
     */
    public static AccelerateDecelerateInterpolator easeInOut()
    {
        if (easeInOut == null)
        {
            easeInOut = new AccelerateDecelerateInterpolator();
        }

        return easeInOut;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the rate of change starts slowly and then speeds
     * up until it ends.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = t^2</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * This timing function corresponds to
     * {@link AccelerateInterpolator}.
     * </p>
     *
     * @return a timing function that eases in an accelerating animation
     */
    public static AccelerateInterpolator easeIn()
    {
        if (easeIn == null)
        {
            easeIn = new AccelerateInterpolator();
        }

        return easeIn;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the rate of change starts slowly and then speeds
     * up until it ends, based on a specified factor.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = t^(2 * factor)</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0]. A factor of 1.0 produces the same result as
     * {@link #easeIn()}; larger values exaggerate the effect (the animation
     * eases in more slowly but ends much faster).
     * </p><p>
     * This timing function corresponds to
     * {@link AccelerateInterpolator}.
     * </p>
     *
     * @param factor the factor by which to exaggerate the animation
     * @return a timing function that eases in an accelerating animation
     */
    public static AccelerateInterpolator easeIn(float factor)
    {
        return new AccelerateInterpolator(factor);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the rate of change starts quickly and then slows
     * down until it ends.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = 1 - (1 - t)^2</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * This timing function corresponds to
     * {@link DecelerateInterpolator}.
     * </p>
     *
     * @return a timing function that eases out a decelerating animation
     */
    public static DecelerateInterpolator easeOut()
    {
        if (easeOut == null)
        {
            easeOut = new DecelerateInterpolator();
        }

        return easeOut;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the rate of change starts quickly and then slows
     * down until it ends, based on a specified factor.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = 1 - (1 - t)^(2 * factor)</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0]. A factor of 1.0 produces the same result as
     * {@link #easeOut()}; larger values exaggerate the effect (the animation
     * starts more quickly but eases out much more slowly).
     * </p><p>
     * This timing function corresponds to
     * {@link DecelerateInterpolator}.
     * </p>
     *
     * @param factor the factor by which to exaggerate the animation
     * @return a timing function that eases out a decelerating animation
     */
    public static DecelerateInterpolator easeOut(float factor)
    {
        return new DecelerateInterpolator(factor);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the rate of change is constant.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = t</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * This timing function corresponds to
     * {@link LinearInterpolator}.
     * </p>
     *
     * @return a timing function that is constant
     */
    public static LinearInterpolator linear()
    {
        if (linear == null)
        {
            linear = new LinearInterpolator();
        }

        return linear;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the change starts backward and then flings
     * forward.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = t^2 * (3 * t - 2)</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0]. (In other words, it is equivalent to
     * {@link #backIn(float)} with a tension of 2.0.)
     * </p><p>
     * This timing function corresponds to
     * {@link AnticipateInterpolator}.
     * </p>
     *
     * @return a timing function that starts backward and then flings forward
     */
    public static AnticipateInterpolator backIn()
    {
        if (backIn == null)
        {
            backIn = new AnticipateInterpolator();
        }

        return backIn;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the change starts backward and then flings
     * forward.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = t^2 * ((tension + 1) * t - tension)</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0]. Higher values of <code>tension</code> cause the
     * animation to "pull backward" more and then accelerate faster when
     * "snapped"; a <code>tension</code> value of 0 makes it identical to
     * {@link #easeIn()}.
     * </p><p>
     * This timing function corresponds to
     * {@link AnticipateInterpolator}.
     * </p>
     *
     * @param tension the amount of tension to apply to the animation before it
     *     is "let go"
     * @return a timing function that starts backward and then flings forward
     */
    public static AnticipateInterpolator backIn(float tension)
    {
        return new AnticipateInterpolator(tension);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the change flings forward, overshooting the end
     * value, and then slowly settles back.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = (t - 1)^2 * ((tension + 1)(t - 1) + tension) + 1</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0]. (In other words, it is equivalent to
     * {@link #backOut(float)} with a tension of 2.0.)
     * </p><p>
     * This timing function corresponds to
     * {@link OvershootInterpolator}.
     * </p>
     *
     * @return a timing function that flings forward past the end point and
     *     then settles back to the end
     */
    public static OvershootInterpolator backOut()
    {
        if (backOut == null)
        {
            backOut = new OvershootInterpolator();
        }

        return backOut;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the change flings forward, overshooting the end
     * value, and then slowly settles back.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = (t - 1)^2 * ((tension + 1)(t - 1) + tension) + 1</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0]. (In other words, it is equivalent to
     * {@link #backOut(float)} with a tension of 2.0.)
     * </p><p>
     * This timing function corresponds to
     * {@link OvershootInterpolator}.
     * </p>
     *
     * @param tension the amount of tension to apply to the animation before it
     *     is "let go"
     * @return a timing function that flings forward past the end point and
     *     then settles back to the end
     */
    public static OvershootInterpolator backOut(float tension)
    {
        return new OvershootInterpolator(tension);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the change starts backward, then flings forward,
     * overshooting the end value, and then slowly settles back.
     * </p><p>
     * This timing function is represented by
     * <code>TODO</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0]. (In other words, it is equivalent to
     * {@link #backInOut(float)} with a tension of 2.0.)
     * </p><p>
     * This timing function corresponds to
     * {@link AnticipateOvershootInterpolator}.
     * </p>
     *
     * @return a timing function that starts backward, flings forward past the
     *     end point and then settles back to the end
     */
    public static AnticipateOvershootInterpolator backInOut()
    {
        if (backInOut == null)
        {
            backInOut = new AnticipateOvershootInterpolator();
        }

        return backInOut;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the change starts backward, then flings forward,
     * overshooting the end value, and then slowly settles back.
     * </p><p>
     * This timing function is represented by
     * <code>TODO</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0]. (In other words, it is equivalent to
     * {@link #backInOut(float)} with a tension of 2.0.)
     * </p><p>
     * This timing function corresponds to
     * {@link AnticipateOvershootInterpolator}.
     * </p>
     *
     * @param tension the amount of tension to apply to the animation before it
     *     is "let go"
     * @return a timing function that flings forward past the end point and
     *     then settles back to the end
     */
    public static AnticipateOvershootInterpolator backInOut(
        float tension)
    {
        return new AnticipateOvershootInterpolator(tension);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function where the change starts backward, then flings forward,
     * overshooting the end value, and then slowly settles back.
     * </p><p>
     * This timing function is represented by
     * <code>TODO</code>,
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * This timing function corresponds to
     * {@link AnticipateOvershootInterpolator}.
     * </p>
     *
     * @param tension the amount of tension to apply to the animation before it
     *     is "let go"
     * @param factor the amount by which to multiply the tension
     * @return a timing function that flings forward past the end point and
     *     then settles back to the end
     */
    public static AnticipateOvershootInterpolator backInOut(
        float tension, float factor)
    {
        return new AnticipateOvershootInterpolator(tension, factor);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function that causes the animation to "bounce".
     * </p><p>
     * This timing function is represented by a piecewise function:
     * <pre>
     * f(t) = 8 * (1.1226 * t)^2,                  if      0 &lt;= t &lt; 0.3535
     *      = 8 * (1.1226 * t - 0.54719)^2 + 0.7,  if 0.3535 &lt;= t &lt; 0.7408,
     *      = 8 * (1.1226 * t - 0.8526)^2 + 0.9,   if 0.7408 &lt;= t &lt; 0.9644,
     *      = 8 * (1.1226 * t - 1.0435)^2 + 0.95,  if 0.9644 &lt;= t &lt;= 1,</pre>
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * To elaborate, an animation that uses this timing function will ease in
     * (accelerate) from start to end in about 31% of its duration, then
     * "bounce" (accelerating backward and then decelerating forward) to the
     * 66% point in its duration, bounce again with less strength to the 86%
     * point, and then bounce one more time until the end of the duration, with
     * each bounce occurring at less strength than the one before.
     * </p><p>
     * This timing function corresponds to
     * {@link BounceInterpolator}.
     * </p>
     *
     * @return a timing function that causes the animation to bounce
     */
    public static BounceInterpolator bounce()
    {
        if (bounce == null)
        {
            bounce = new BounceInterpolator();
        }

        return bounce;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function that causes the animation to cycle forward and
     * backward in a sinusoidal pattern.
     * </p><p>
     * This timing function is represented by
     * <code>f(t) = sin(2 * cycles * pi)</code>
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * This timing function corresponds to {@link CycleInterpolator}.
     * </p>
     *
     * @param cycles the number of cycles to run the animation
     * @return a timing function that causes the animation to cycle
     */
    public static CycleInterpolator cycle(float cycles)
    {
        return new CycleInterpolator(cycles);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function that elastically snaps from the start value.
     * </p><p>
     * This timing function is represented by
     * {@code TODO}
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * This timing function corresponds to {@link ElasticInInterpolator}
     * (which is a custom interpolator provided by Sofia, not one built into
     * the Android API).
     * </p>
     * 
     * @return a timing function that causes the animation to snap in
     */
    public static ElasticInInterpolator elasticIn()
    {
        if (elasticIn == null)
        {
            elasticIn = new ElasticInInterpolator();
        }

        return elasticIn;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function that elastically snaps back when it reaches the end
     * value.
     * </p><p>
     * This timing function is represented by
     * {@code TODO}
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * This timing function corresponds to {@link ElasticOutInterpolator}
     * (which is a custom interpolator provided by Sofia, not one built into
     * the Android API).
     * </p>
     * 
     * @return a timing function that causes the animation to snap back
     */
    public static ElasticOutInterpolator elasticOut()
    {
        if (elasticOut == null)
        {
            elasticOut = new ElasticOutInterpolator();
        }

        return elasticOut;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A timing function that elastically snaps in from the start value and
     * then snaps back again when it reaches the end value.
     * </p><p>
     * This timing function is represented by
     * {@code TODO}
     * where <em>t</em> is the current time in the animation normalized to the
     * range [0.0, 1.0].
     * </p><p>
     * This timing function corresponds to {@link ElasticInOutInterpolator}
     * (which is a custom interpolator provided by Sofia, not one built into
     * the Android API).
     * </p>
     * 
     * @return a timing function that causes the animation to snap at the
     *     beginning and end
     */
    public static ElasticInOutInterpolator elasticInOut()
    {
        if (elasticInOut == null)
        {
            elasticInOut = new ElasticInOutInterpolator();
        }

        return elasticInOut;
    }
}
