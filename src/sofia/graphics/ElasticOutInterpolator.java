package sofia.graphics;

import android.view.animation.Interpolator;

public class ElasticOutInterpolator implements Interpolator
{
    // ----------------------------------------------------------
    public float getInterpolation(float t)
    {
        if (t == 0 || t == 1)
        {
            return t;
        }

        float p = 0.3f;
        float s = p / 4;

        return (float) (Math.pow(2, -10 * t) * Math.sin((t - s)
            * (2 * Math.PI) / p) + 1);
    }
}
