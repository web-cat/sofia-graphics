package sofia.graphics.animation;

import sofia.graphics.Color;
import sofia.graphics.MotionStep;
import sofia.graphics.PointAndAnchor;
import sofia.graphics.ShapeAnimationListener;
import sofia.graphics.RepeatMode;
import sofia.graphics.Timings;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import java.util.HashSet;
import java.util.Set;
import sofia.graphics.Shape;
import sofia.graphics.internal.GeometryUtils;

// -------------------------------------------------------------------------
/**
 * Provides animation support for shapes.
 *
 * @param <ShapeType>
 * @param <AnimatorType>
 *
 * @author  Tony Allevato
 * @version 2011.12.11
 */
public class ShapeAnimator<
    ShapeType extends Shape,
    AnimatorType extends ShapeAnimator<ShapeType, AnimatorType>>
{
    private enum State
    {
        WAITING,
        FORWARD,
        BACKWARD,
        STOPPED
    }

    private ShapeType shape;
    private long duration;
    private Interpolator interpolator;
    private long startTime;
    private long delay;
    private RepeatMode repeatMode;
    private boolean removeWhenComplete;
    private ShapeAnimationListener listener;
    private State state;
    private long lastTime;

    private Set<PropertyTransformer> transformers;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new animator for the specified shape. Users should not call
     * this constructor directly; instead, they should use the
     * {@link Shape#animate(long)} method to get an animator object.
     *
     * @param shape the shape to animate
     * @param duration the length of one pass of the animation, in milliseconds
     */
    public ShapeAnimator(ShapeType shape, long duration)
    {
        this.shape = shape;
        this.duration = duration;
        this.delay = 0;
        this.interpolator = Timings.easeInOut();
        this.repeatMode = RepeatMode.NONE;
        this.removeWhenComplete = false;
        this.state = State.WAITING;

        transformers = new HashSet<PropertyTransformer>();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the shape that the receiver is animating. Most users will not have
     * a need to call this method; it is mainly provided for those who need to
     * subclass an animator to provide animation support for custom properties
     * of their own shapes.
     *
     * @return the shape that the receiver is animating
     */
    public ShapeType getShape()
    {
        return shape;
    }


    // ----------------------------------------------------------
    /**
     * Sets the timing function (interpolator) that determines how the
     * animation behaves during execution. A number of pre-written timing
     * functions can be found as static methods in the {@link Timings} class.
     *
     * @param newInterpolator the timing function (interpolator) that
     *     determines how the animation behaves during execution
     * @return this animator, for method chaining
     */
    @SuppressWarnings("unchecked")
    public AnimatorType timing(Interpolator newInterpolator)
    {
        this.interpolator = newInterpolator;
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    /**
     * Sets the delay, in milliseconds, that the animation will wait after the
     * {@link #play()} method is called until it actually starts.
     *
     * @param newDelay the delay, in milliseconds, before the animation starts
     * @return this animator, for method chaining
     */
    @SuppressWarnings("unchecked")
    public AnimatorType delay(long newDelay)
    {
        this.delay = newDelay;
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    /**
     * Sets the listener that will be notified of various events that occur
     * during the animation (such as when it starts, repeats, or ends).
     *
     * @param newListener the listener that will be notified of animation
     *     events
     * @return this animator, for method chaining
     */
    @SuppressWarnings("unchecked")
    public AnimatorType listener(ShapeAnimationListener newListener)
    {
        this.listener = newListener;
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    public AnimatorType position(float x, float y)
    {
        return position(new PointF(x, y));
    }


    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public AnimatorType position(PointF point)
    {
        addTransformer(new PositionTransformer(shape, point));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public AnimatorType moveBy(float dx, float dy)
    {
    	addTransformer(new MotionStepTransformer(shape,
    			MotionStep.constantVelocity(dx, dy)));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public AnimatorType moveBy(float dx, float dy, float ax, float ay)
    {
    	addTransformer(new MotionStepTransformer(shape,
    			MotionStep.constantAcceleration(dx, dy, ax, ay)));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public AnimatorType moveBy(MotionStep motionStep)
    {
    	addTransformer(new MotionStepTransformer(shape, motionStep));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public AnimatorType y(float y)
    {
        addTransformer(new XTransformer(shape, y));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public AnimatorType bounds(RectF bounds)
    {
        addTransformer(new BoundsTransformer(shape, bounds));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    public AnimatorType color(Color color)
    {
        addTransformer(new ColorTransformer(shape, color));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    /**
     * Sets the final alpha (opacity) of the shape when the animation ends.
     *
     * @param alpha the final alpha (opacity) of the shape when the animation
     *     ends, from 0 (fully transparent) to 255 (fully opaque)
     * @return this animator, for method chaining
     */
    @SuppressWarnings("unchecked")
    public AnimatorType alpha(int alpha)
    {
        addTransformer(new AlphaTransformer(shape, alpha));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Sets the final rotation, in degrees clockwise, of the shape when the
     * animation ends. Negative values will create a counter-clockwise
     * rotation.
     * </p><p>
     * A shape can be made to rotate completely multiple times by
     * providing values higher than 360 to this method. For example, passing
     * 360 would cause the shape to make one full rotation over the duration
     * of the animation, passing 720 would cause it to make two full rotations,
     * and so forth.
     * </p>
     *
     * @param rotation the final rotation, in degrees clockwise (negative
     *     values will rotate counter-clockwise)
     * @return this animator, for method chaining
     */
    @SuppressWarnings("unchecked")
    public AnimatorType rotation(float rotation)
    {
        addTransformer(new RotationTransformer(shape, rotation));
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    /**
     * Causes the animation to repeat until stopped. This method is provided as
     * shorthand, equivalent to {@code repeatMode(RepeatMode.REPEAT)}.
     *
     * @return this animator, for chaining method calls
     */
    public AnimatorType repeat()
    {
        return repeatMode(RepeatMode.REPEAT);
    }


    // ----------------------------------------------------------
    /**
     * Causes the animation to oscillate (from start to end and back to start)
     * until stopped. This method is provided as shorthand, equivalent to
     * {@code repeatMode(RepeatMode.OSCILLATE)}.
     *
     * @return this animator, for chaining method calls
     */
    public AnimatorType oscillate()
    {
        return repeatMode(RepeatMode.OSCILLATE);
    }


    // ----------------------------------------------------------
    /**
     * Sets the repeat mode for this animation. See the {@link RepeatMode}
     * enumeration for possible values.
     *
     * @param mode the repeat mode for the animation
     * @return this animator, for chaining method calls
     */
    @SuppressWarnings("unchecked")
    public AnimatorType repeatMode(RepeatMode mode)
    {
        repeatMode = mode;
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Causes the shape to be automatically removed from its view when the
     * animation completes. This is useful for animations that cause a shape to
     * fade out, where you want it to disappear for good when done.
     * </p><p>
     * Note that the shape will only be removed if the animation ends on its
     * own when its time expires; it will not be removed if you end the
     * animation prematurely by calling {@link Shape#stopAnimation()}. This
     * also means that this method will have no effect if the animation is
     * repeating or oscillating.
     * </p>
     *
     * @return this animator, for chaining method calls
     */
    @SuppressWarnings("unchecked")
    public AnimatorType removeWhenComplete()
    {
        removeWhenComplete = true;
        return (AnimatorType) this;
    }


    // ----------------------------------------------------------
    /**
     * Adds a property transformer to the list of those that will be applied
     * each time the animation advances.
     *
     * @param transformer the property transformer
     */
    protected void addTransformer(PropertyTransformer transformer)
    {
        transformers.add(transformer);
    }


    // ----------------------------------------------------------
    /**
     * Starts the animation.
     */
    public void play()
    {
        for (PropertyTransformer transformer : transformers)
        {
            transformer.onStart();
        }

        startTime = System.currentTimeMillis() + delay;
        shape.getParentView().getAnimationManager().enqueue(this);
    }


    // ----------------------------------------------------------
    /**
     * This method is intended for internal use. Users wishing to stop a
     * shape's animation should call {@link Shape#stopAnimation()} instead.
     */
    public void stop()
    {
        state = State.STOPPED;
    }


    // ----------------------------------------------------------
    /**
     * This method is intended for internal use.
     *
     * @param time
     * @return
     */
    public boolean advanceTo(long time)
    {
        if (time < startTime)
        {
            return false;
        }
        else if (state == State.STOPPED)
        {
            return true;
        }
        else if (state == State.WAITING)
        {
            state = State.FORWARD;
            postOnAnimationStart();
        }

        float t = 0;
        long scaledTime = time;
        boolean ended = false;

        switch (repeatMode)
        {
            case NONE:
                ended = (time >= startTime + duration);
                t = ended ? 1.0f :
                    (float) ((double) (time - startTime) / duration);
                break;

            case REPEAT:
                state = State.FORWARD;
                scaledTime = (time - startTime) % duration;
                t = (float) ((double) scaledTime / duration);

                if (scaledTime < lastTime)
                {
                    postOnAnimationRepeat(false);
                }

                break;

            case OSCILLATE:
                scaledTime = (time - startTime) % (2 * duration);

                if (scaledTime < duration)
                {
                    t = (float) ((double) scaledTime / duration);
                }
                else
                {
                    t = 1 - (float) ((double) (
                        scaledTime - duration) / duration);
                }

                if (state == State.FORWARD && scaledTime > duration)
                {
                    state = State.BACKWARD;
                    postOnAnimationRepeat(true);
                }
                else if (state == State.BACKWARD && scaledTime < duration)
                {
                    state = State.FORWARD;
                    postOnAnimationRepeat(false);
                }

                break;
        }

        float y = interpolator.getInterpolation(t);

        for (PropertyTransformer transformer : transformers)
        {
            transformer.transform(y);
        }

        if (ended)
        {
            if (removeWhenComplete)
            {
                shape.getParentView().remove(shape);
            }

            postOnAnimationEnd();
        }

        lastTime = scaledTime;

        return ended;
    }


    // ----------------------------------------------------------
    private void postOnAnimationStart()
    {
        if (listener != null)
        {
            shape.getParentView().post(new Runnable() {
                public void run()
                {
                    listener.onAnimationStart(shape);
                }
            });
        }
    }


    // ----------------------------------------------------------
    private void postOnAnimationRepeat(final boolean backward)
    {
        if (listener != null)
        {
            shape.getParentView().post(new Runnable() {
                public void run()
                {
                    listener.onAnimationRepeat(shape, backward);
                }
            });
        }
    }


    // ----------------------------------------------------------
    private void postOnAnimationEnd()
    {
        if (listener != null)
        {
            shape.getParentView().post(new Runnable() {
                public void run()
                {
                    listener.onAnimationEnd(shape);
                }
            });
        }
    }


    // ----------------------------------------------------------
    protected static class ColorTransformer implements PropertyTransformer
    {
        private Shape shape;
        private Color start;
        private Color end;


        // ----------------------------------------------------------
        public ColorTransformer(Shape shape, Color end)
        {
            this.shape = shape;
            this.end = end;
        }


        // ----------------------------------------------------------
        public void onStart()
        {
            start = shape.getColor();
        }


        // ----------------------------------------------------------
        public void transform(float t)
        {
            int alpha = Math.max(0, Math.min(255,
                (int) (start.alpha() + (end.alpha() - start.alpha()) * t)));
            int red = Math.max(0, Math.min(255,
                (int) (start.red() + (end.red() - start.red()) * t)));
            int green = Math.max(0, Math.min(255,
                (int) (start.green() + (end.green() - start.green()) * t)));
            int blue = Math.max(0, Math.min(255,
                (int) (start.blue() + (end.blue() - start.blue()) * t)));

            shape.setColor(Color.rgb(red, green, blue, alpha));
        }
    }


    // ----------------------------------------------------------
    protected static class PositionTransformer implements PropertyTransformer
    {
        private Shape shape;
        private PointF start;
        private PointF end;


        // ----------------------------------------------------------
        public PositionTransformer(Shape shape, PointF end)
        {
            this.shape = shape;
            this.end = end;
        }


        // ----------------------------------------------------------
        public void onStart()
        {
            start = shape.getPosition();
            GeometryUtils.resolveGeometry(end, shape);
        }


        // ----------------------------------------------------------
        public void transform(float t)
        {
            shape.setPosition(new PointF(
                start.x + (end.x - start.x) * t,
                start.y + (end.y - start.y) * t));
        }
    }


    // ----------------------------------------------------------
    protected class MotionStepTransformer implements PropertyTransformer
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


    // ----------------------------------------------------------
    protected static class XTransformer implements PropertyTransformer
    {
        private Shape shape;
        private PointF start;
        private float end;


        // ----------------------------------------------------------
        public XTransformer(Shape shape, float end)
        {
            this.shape = shape;
            this.end = end;
        }


        // ----------------------------------------------------------
        public void onStart()
        {
            start = GeometryUtils.copy(shape.getPosition());
        }


        // ----------------------------------------------------------
        public void transform(float t)
        {
            shape.setPosition(new PointF(
                start.x + (end - start.x) * t, start.y));
        }
    }


    // ----------------------------------------------------------
    protected static class YTransformer implements PropertyTransformer
    {
        private Shape shape;
        private PointF start;
        private float end;


        // ----------------------------------------------------------
        public YTransformer(Shape shape, float end)
        {
            this.shape = shape;
            this.end = end;
        }


        // ----------------------------------------------------------
        public void onStart()
        {
            start = GeometryUtils.copy(shape.getPosition());
        }


        // ----------------------------------------------------------
        public void transform(float t)
        {
            shape.setPosition(new PointF(
                start.x, start.y + (end - start.y) * t));
        }
    }


    // ----------------------------------------------------------
    protected static class BoundsTransformer implements PropertyTransformer
    {
        private Shape shape;
        private RectF start;
        private RectF end;


        // ----------------------------------------------------------
        public BoundsTransformer(Shape shape, RectF end)
        {
            this.shape = shape;
            this.end = end;
        }


        // ----------------------------------------------------------
        public void onStart()
        {
            start = shape.getBounds();
            GeometryUtils.resolveGeometry(end, shape);
        }


        // ----------------------------------------------------------
        public void transform(float t)
        {
            shape.setBounds(new RectF(
                start.left + (end.left - start.left) * t,
                start.top + (end.top - start.top) * t,
                start.right + (end.right - start.right) * t,
                start.bottom + (end.bottom - start.bottom) * t));
        }
    }


    // ----------------------------------------------------------
    protected static class AlphaTransformer implements PropertyTransformer
    {
        private Shape shape;
        private int start;
        private int end;


        // ----------------------------------------------------------
        public AlphaTransformer(Shape shape, int end)
        {
            this.shape = shape;
            this.end = end;
        }


        // ----------------------------------------------------------
        public void onStart()
        {
            start = shape.getAlpha();
        }


        // ----------------------------------------------------------
        public void transform(float t)
        {
            int value = Math.max(0, Math.min(255,
                (int) (start + (end - start) * t)));
            shape.setAlpha(value);
        }
    }


    // ----------------------------------------------------------
    protected static class RotationTransformer implements PropertyTransformer
    {
        private Shape shape;
        private float start;
        private float end;


        // ----------------------------------------------------------
        public RotationTransformer(Shape shape, float end)
        {
            this.shape = shape;
            this.end = end;
        }


        // ----------------------------------------------------------
        public void onStart()
        {
            start = shape.getRotation();
        }


        // ----------------------------------------------------------
        public void transform(float t)
        {
            shape.setRotation(start + (end - start) * t);
        }
    }
}
