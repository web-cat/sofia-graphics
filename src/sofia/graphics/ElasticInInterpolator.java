package sofia.graphics;

import android.view.animation.Interpolator;

public class ElasticInInterpolator implements Interpolator
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
        float nt = t - 1;

        return (float) (-1 * Math.pow(2, 10 * nt) * Math.sin((nt - s)
            * (2 * Math.PI) / p));
    }
}
