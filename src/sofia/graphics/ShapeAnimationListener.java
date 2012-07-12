package sofia.graphics;

import sofia.graphics.animation.ShapeAnimator;

// -------------------------------------------------------------------------
/**
 * A listener that receives notifications when various shape animation events
 * occur.
 *
 * @author  Tony Allevato
 * @version 2011.12.12
 */
public interface ShapeAnimationListener
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Called when the animation first starts. This will happen as soon as the
     * {@link ShapeAnimator#play()} method is called, unless the animation is
     * set to start after a delay, in which case this method will be called
     * after the delay expires.
     *
     * @param shape the shape being animated
     */
    void onAnimationStart(Shape shape);


    // ----------------------------------------------------------
    /**
     * Called as soon as possible after the animation has repeated.
     *
     * @param shape the shape being animated
     * @param backward true if the animation is now running backward (that is,
     *     it is in oscillating mode and started over from the end), false if
     *     it is now running forward (either in regular repeating mode or
     *     oscillating mode)
     */
    void onAnimationRepeat(Shape shape, boolean backward);


    // ----------------------------------------------------------
    /**
     * Called when the animation ends after its time has expired. This method
     * is not called if you call {@link Shape#stopAnimation()} manually, nor if
     * the animation is repeating.
     *
     * @param shape the shape being animated
     */
    void onAnimationEnd(Shape shape);
}
