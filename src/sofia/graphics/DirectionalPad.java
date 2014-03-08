package sofia.graphics;

import android.util.SparseIntArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.RectF;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;

//-------------------------------------------------------------------------
/**
 * An on-screen directional pad (d-pad) suitable for games, based on the
 * cross-shaped d-pad used on classic console game systems. The d-pad responds
 * to touch events and translates them as key events (with key codes
 * {@code KeyEvent#KEYCODE_DPAD_*}) that get sent to the {@link ShapeView} that
 * contains the shape. The d-pad also fades out slightly when it is not being
 * touched, so that it does not completely obscure the rest of the screen
 * content.
 *
 * @author  Tony Allevato
 * @author  Brian Bowden
 * @version $Date$
 */
public class DirectionalPad extends RectangleShape
{
    //~ Fields ................................................................

    private static final Logger log = LoggerFactory.getLogger(
            DirectionalPad.class);

    private static final int DEFAULT_INACTIVE_ALPHA = 128;

    private static final SparseIntArray touchToKeyActions =
            new SparseIntArray();

    private static final SparseArray<int[]> keysForZones =
            new SparseArray<int[]>();

    static
    {
        touchToKeyActions.put(MotionEvent.ACTION_DOWN, KeyEvent.ACTION_DOWN);
        touchToKeyActions.put(MotionEvent.ACTION_UP, KeyEvent.ACTION_UP);

        keysForZones.put(1, new int[] {
                KeyEvent.KEYCODE_DPAD_RIGHT });
        keysForZones.put(2, new int[] {
                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN });
        keysForZones.put(3, new int[] {
                KeyEvent.KEYCODE_DPAD_DOWN });
        keysForZones.put(4, new int[] {
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_DOWN });
        keysForZones.put(5, new int[] {
                KeyEvent.KEYCODE_DPAD_LEFT });
        keysForZones.put(6, new int[] {
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP });
        keysForZones.put(7, new int[] {
                KeyEvent.KEYCODE_DPAD_UP });
        keysForZones.put(8, new int[] {
                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_UP });
    }

    private final Runnable fadeOutRunnable = new Runnable() {
        @Override
        public void run()
        {
            log.info("Fading out D-pad");
            stopAnimation();
            animate(500).alpha(inactiveAlpha).play();
        }
    };

    private boolean isFadingIn;
    private int inactiveAlpha;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new directional pad with the specified bounds. For best
     * results, the bounds should be square, but no checking is done to verify
     * this.
     *
     * @param bounds the bounds of the directional pad
     */
    public DirectionalPad(RectF bounds)
    {
        super(bounds);

        setActive(false);
        setImage(new Image(DirectionalPad.class));

        inactiveAlpha = DEFAULT_INACTIVE_ALPHA;
        setAlpha(inactiveAlpha);
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the alpha value of the directional pad when it is inactive (has not
     * been touched for one second).
     *
     * @return the alpha value of the directional pad when it is inactive
     */
    public int getInactiveAlpha()
    {
        return inactiveAlpha;
    }


    // ----------------------------------------------------------
    /**
     * Sets the alpha value of the directional pad when it is inactive (has not
     * been touched for one second). Be careful not to set this value too low,
     * or it will make the directional pad difficult to see when it is not in
     * use.
     *
     * @param newAlpha the new alpha value of the directional pad when it is
     *     inactive
     */
    public void setInactiveAlpha(int newAlpha)
    {
        inactiveAlpha = newAlpha;

        if (!isFadingIn)
        {
            setAlpha(inactiveAlpha);
        }
    }


    // ----------------------------------------------------------
    /**
     * Handles a touch down event on the directional pad, mapping it to
     * appropriate key events.
     *
     * @param e a {@link MotionEvent} describing the touch
     */
    public void onTouchDown(MotionEvent e)
    {
        processTouch(e);
    }


    // ----------------------------------------------------------
    /**
     * Handles a touch up event on the directional pad, mapping it to
     * appropriate key events.
     *
     * @param e a {@link MotionEvent} describing the touch
     */
    public void onTouchUp(MotionEvent e)
    {
        processTouch(e);
    }


    // ----------------------------------------------------------
    /**
     * Implemented to handle the fading in/out logic of the d-pad. Not intended
     * to be called by clients.
     *
     * @param animator the animator
     */
    public void onAnimationDone(Shape.Animator<?> animator)
    {
        isFadingIn = false;
    }

    /**
     * An alternative process touch, mostly used for the micro world since it
     * handles motion events differently.
     *
     * @param touchAction action code for the motion event
     * @param tx x coordinate for the event
     * @param ty y coordinate for the event
     */
    public void processTouch(int touchAction, float tx, float ty)
    {
        float x0 = getBounds().centerX();
        float y0 = getBounds().centerY();

        // Compute the "zone" that was tapped; 0 is east, 1 is southeast, 2
        // is south, and so on.
        float angle =
                mod(Geometry.angleBetween(x0, y0, tx, ty) + 45.0f / 2, 360.0f);

        // Check the distance to make sure it's actually close to the dpad
        float distance = Geometry.distanceBetween(x0, y0, tx, ty);
        if (distance > getWidth() / 2 || distance > getHeight() / 2)
        {
            return;
        }

        int zone = (int) (angle / 45) + 1;
        sendDpadEvents(touchAction, keysForZones.get(zone));
        animateDpad(touchAction);
    }

    //~ Private methods .......................................................

    // ----------------------------------------------------------
    /**
     * Determines what part of the d-pad was touched and dispatches the
     * appropriate key event to the shape's view.
     *
     * @param e a {@link MotionEvent} describing the touch
     */
    private void processTouch(MotionEvent e)
    {
        float tx = e.getX();
        float ty = e.getY();

        float x0 = getBounds().centerX();
        float y0 = getBounds().centerY();

        // Compute the "zone" that was tapped; 0 is east, 1 is southeast, 2
        // is south, and so on.
        float angle =
                mod(Geometry.angleBetween(x0, y0, tx, ty) + 45.0f / 2, 360.0f);

        // Check the distance to make sure it's actually close to the dpad
        float distance = Geometry.distanceBetween(x0, y0, tx, ty);
        if (distance > getWidth() / 2 || distance > getHeight() / 2)
        {
            return;
        }

        int zone = (int) (angle / 45) + 1;
        int touchAction = e.getActionMasked();
        sendDpadEvents(touchAction, keysForZones.get(zone));
        animateDpad(touchAction);
    }


    // ----------------------------------------------------------
    /**
     * Sends the specified d-pad key codes to the view.
     *
     * @param touchAction the touch action that occurred on the shape
     * @param keys the key codes to send to the view
     */
    private void sendDpadEvents(int touchAction, int[] keys)
    {
        if (touchToKeyActions.get(touchAction) == 0
         || getParentView() == null)
        {
            return;
        }

        int keyAction = touchToKeyActions.get(touchAction);

        // Combine the keycodes into a single int using bitmasking
        int keyCode = 0;
        for (int key : keys)
        {
            keyCode <<= 8;
            keyCode |= key;
        }

        KeyEvent e = new KeyEvent(keyAction, keyCode);
        getParentView().dispatchKeyEvent(e);
    }

    /**
     * Handles fading in or fading out the dpad.
     *
     * @param touchAction action code for the motion event
     */
    private void animateDpad(int touchAction)
    {
        // Fade in or out the d-pad.
        if (touchAction == MotionEvent.ACTION_DOWN || touchAction == MotionEvent.ACTION_MOVE)
        {
            if (!isFadingIn)
            {
                //log.info("Fading in D-pad");
                animate(500).alpha(255).play();
                isFadingIn = true;
            }
            else
            {
                if (getParentView() != null)
                {
                    getParentView().removeCallbacks(fadeOutRunnable);
                }
            }
        }
        else if (touchAction == MotionEvent.ACTION_UP)
        {
            //log.info("Posting fade-out with 1000 msec delay");
            if (getParentView() != null)
            {
                getParentView().postDelayed(fadeOutRunnable, 1000);
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Computes a % b, with the correct result for negative values of a.
     *
     * @param a the dividend
     * @param b the divisor
     * @return a % b
     */
    private float mod(float a, float b)
    {
        return (a % b + b) % b;
    }
}
