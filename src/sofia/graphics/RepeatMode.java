package sofia.graphics;

// -------------------------------------------------------------------------
/**
 * Defines how an animation repeats itself.
 *
 * @author  Tony Allevato
 * @version 2011.12.12
 */
public enum RepeatMode
{
    //~ Constants .............................................................

    // ----------------------------------------------------------
    /**
     * The animation will not repeat; it will execute once from start to end.
     */
    NONE,


    // ----------------------------------------------------------
    /**
     * The animation will repeat by executing from start to end, then
     * instantaneously returning to the start and executing again, until the
     * animation is stopped (by calling {@link Shape#stopAnimation()}).
     */
    REPEAT,


    // ----------------------------------------------------------
    /**
     * The animation will oscillate by executing from start to finish, then
     * executing backward from end to start, and then executing again, until
     * the animation is stopped (by calling {@link Shape#stopAnimation()}).
     */
    OSCILLATE
}
