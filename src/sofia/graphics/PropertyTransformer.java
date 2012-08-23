package sofia.graphics;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here.
 * Follow it with additional details about its purpose, what abstraction
 * it represents, and how to use it.
 *
 * @author  Tony Allevato
 * @version 2011.12.04
 */
public interface PropertyTransformer
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Called when the animation is first started so that the transformer can
     * initialize any data that needs to be computed when the animation is
     * <strong>started</strong> rather than when it is
     * <strong>created</strong>.
     */
    void onStart();


    // ----------------------------------------------------------
    /**
     * Applies the receiver's transformation at time t, where t is between
     * 0 and 1.
     *
     * @param t the time of the transformation, normalized between 0 and 1
     */
    void transform(float t);
}