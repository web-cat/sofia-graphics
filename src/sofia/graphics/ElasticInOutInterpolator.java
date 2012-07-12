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
