package sofia.graphics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import sofia.graphics.internal.ShapeAnimationManager;
import sofia.internal.events.EventDispatcher;
import sofia.internal.events.MotionEventDispatcher;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// -------------------------------------------------------------------------
/**
 * Represents a view containing drawn {@link Shape} objects.
 *
 * @author Tony Allevato
 * @author Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public class ShapeView
    extends SurfaceView
{
    // ~ Fields ................................................................

    private ShapeField                                      shapeField;
    private CanvasDrawing                                   drawing;
    private boolean                                         surfaceCreated;
    private Color                                           backgroundColor;
    private List<Object>                                    gestureDetectors;
    // private GestureDetector gestureDetector;
    private boolean                                         autoRepaint;
    private Set<Long>                                       threadsBlockingRepaint;
    private ShapeAnimationManager                           animationManager;
    // private RepaintThread repaintThread;
    private PhysicsThread                                   physicsThread;
    private CoordinateSystem                                coordinateSystem;

    // Event forwarders
    private final CoordinateRespectingMotionEventDispatcher onTouchDown =
        new CoordinateRespectingMotionEventDispatcher("onTouchDown");

    private final CoordinateRespectingMotionEventDispatcher onTouchMove  =
        new CoordinateRespectingMotionEventDispatcher("onTouchMove");

    private final CoordinateRespectingMotionEventDispatcher onTouchUp =
        new CoordinateRespectingMotionEventDispatcher("onTouchUp");

    private static final EventDispatcher onKeyDown =
        new EventDispatcher("onKeyDown");

    private static final EventDispatcher onScaleGesture =
        new EventDispatcher("onScaleGesture");

    private static final EventDispatcher onRotateGestureBegin =
        new EventDispatcher("onRotateGestureBegin");

    private static final EventDispatcher onRotateGesture =
        new EventDispatcher("onRotateGesture");

    private static final EventDispatcher onFlingGesture =
        new EventDispatcher("onFlingGesture");

    private Shape                                           shapeBeingDragged;


    // ~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ShapeView.
     *
     * @param context
     *            This view's context.
     */
    public ShapeView(Context context)
    {
        super(context);
        init();
    }


    // ----------------------------------------------------------
    /**
     * Creates a new ShapeView.
     *
     * @param context
     *            This view's context.
     * @param attrs
     *            This view's attributes.
     */
    public ShapeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }


    // ----------------------------------------------------------
    /**
     * Creates a new ShapeView.
     *
     * @param context
     *            This view's context.
     * @param attrs
     *            This view's attributes.
     * @param defStyle
     *            This view's default style.
     */
    public ShapeView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }


    // ~ Methods ...............................................................

    // ----------------------------------------------------------
    public CoordinateSystem getCoordinateSystem()
    {
        return coordinateSystem;
    }


    // ----------------------------------------------------------
    private void init()
    {
        drawing = new CanvasDrawing();
        threadsBlockingRepaint = new HashSet<Long>();

        getHolder().addCallback(new SurfaceHolderCallback());

        TypedArray array =
            getContext().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.colorBackground,
                    android.R.attr.textColorPrimary, });

        backgroundColor =
            Color.fromRawColor(array.getColor(0, android.graphics.Color.BLACK));
        array.recycle();

        shapeField = new ShapeField();
        shapeField.setView(this);
// gestureDetector = new GestureDetector(new ShapeGestureListener());

        gestureDetectors = new ArrayList<Object>();
        coordinateSystem = new CoordinateSystem(this);

        setFocusableInTouchMode(true);
    }


    // ----------------------------------------------------------
    /**
     * Gets the gravity of the physical world represented by this shape view.
     *
     * @return a {@code PointF} object whose x and y components are the
     *         horizontal and vertical acceleration due to gravity (in
     *         units/sec^2) of the physical world represented by this shape view
     */
    public PointF getGravity()
    {
        return shapeField.getGravity();
    }


    // ----------------------------------------------------------
    /**
     * Sets the gravity of the physical world represented by this shape view.
     *
     * @param gravity
     *            a {@code PointF} whose x and y components are the horizontal
     *            and vertical acceleration due to gravity (in units/sec^2) of
     *            the physical world represented by this shape view
     */
    public void setGravity(PointF gravity)
    {
        shapeField.setGravity(gravity);
    }


    // ----------------------------------------------------------
    /**
     * Sets the gravity of the physical world represented by this shape view.
     *
     * @param xGravity
     *            the horizontal acceleration due to gravity (in units/sec^2)
     * @param yGravity
     *            the vertical acceleration due to gravity (in units/sec^2)
     */
    public void setGravity(float xGravity, float yGravity)
    {
        shapeField.setGravity(xGravity, yGravity);
    }


    // ----------------------------------------------------------
    /**
     * Does this view automatically repaint, or is an explicit call needed?
     *
     * @return True if this view automatically repaints when contained shapes
     *         are modified.
     * @see #setAutoRepaint(boolean)
     */
    public synchronized boolean doesAutoRepaint()
    {
        return autoRepaint && threadsBlockingRepaint.isEmpty();
    }


    // ----------------------------------------------------------
    /**
     * Tell this view to automatically repaint when Shapes change (or not).
     *
     * @param value
     *            Whether or not this view should automatically repaint when
     *            shapes change.
     */
    public synchronized void setAutoRepaint(boolean value)
    {
        autoRepaint = value;
    }


    // ----------------------------------------------------------
    /**
     * Used internally to temporarily disable repainting.
     *
     * @param value
     *            Says whether the current thread is restoring auto-painting or
     *            disabling auto-painting.
     */
    public synchronized void internalSetAutoRepaintForThread(boolean value)
    {
        long current = Thread.currentThread().getId();

        if (value)
        {
            threadsBlockingRepaint.remove(current);
        }
        else
        {
            threadsBlockingRepaint.add(current);
        }
    }


    // ----------------------------------------------------------
    /**
     * Get the animation manager for this view.
     *
     * @return This view's animation manager.
     */
    public ShapeAnimationManager getAnimationManager()
    {
        return animationManager;
    }


    // ----------------------------------------------------------
    /**
     * Gets the {@link ShapeField} that the view is currently displaying and
     * simulating.
     *
     * @return the {@link ShapeField} currently in use by the view
     */
    public ShapeField getShapeField()
    {
        return shapeField;
    }


    // ----------------------------------------------------------
    /**
     * Sets the {@link ShapeField} that the view is currently displaying and
     * simulating. When developing games or simulations that involve multiple
     * "levels" or other complex multiple shape layouts, this method can be used
     * to quickly and easily swap out the entire set of shapes used by the view.
     *
     * @param newField
     *            the {@link ShapeField} to be used by the view
     */
    public void setShapeField(ShapeField newField)
    {
        if (newField == null)
        {
            throw new IllegalArgumentException("A ShapeView cannot have a "
                + "null ShapeField.");
        }

        shapeField = newField;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Gets a filter that can be used to find shapes that match certain
     * criteria. This method is a shortcut for
     * {@code getShapeField().getShapes()}.
     *
     * @return a filter that can be used to find shapes that match certain
     *         criteria
     */
    public ShapeFilter<Shape> getShapes()
    {
        return shapeField.getShapes();
    }


    /**
     * Get all the shapes of the specified type in this view.
     *
     * @param cls
     *            Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyShape>
     *            The type of shape to look for, as specified in the cls
     *            parameter.
     * @return List of all the shapes of the specified type (or any of its
     *         subtypes) in the view.
     */
    public <MyShape extends Shape> Set<MyShape> getShapes(Class<MyShape> cls)
    {
        if (cls == null)
        {
            @SuppressWarnings("unchecked")
            Set<MyShape> result = (Set<MyShape>)getShapes();
            return result;
        }

        synchronized (shapeField)
        {
            Set<MyShape> result =
                new java.util.TreeSet<MyShape>(shapeField.getDrawingOrder());
            for (Shape shape : getShapes())
            {
                if (cls.isInstance(shape))
                {
                    result.add(cls.cast(shape));
                }
            }
            return result;
        }
    }

    // ----------------------------------------------------------
    /**
     * Returns all objects with the logical location within the specified
     * circle. In other words an object A is within the range of an object B if
     * the distance between the center of the two objects is less than r.
     *
     * @param x
     *            Center of the circle.
     * @param y
     *            Center of the circle.
     * @param r
     *            Radius of the circle.
     * @param cls
     *            Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyShape>
     *            The type of shape to look for, as specified in the cls
     *            parameter.
     * @return A set of shapes that lie within the given circle.
     */
    public <MyShape extends Shape> Set<MyShape> getShapesInRange(
        float x,
        float y,
        float r,
        Class<MyShape> cls)
    {
        return getShapes().locatedWithin(new PointF(x, y), r).withClass(cls).all();
    }


    // ----------------------------------------------------------
    /**
     * Returns the neighbors to the given location. This method only looks at
     * the logical location and not the extent of objects. Hence it is most
     * useful in scenarios where objects only span one cell.
     *
     * @param shape
     *            The shape whose neighbors will be located.
     * @param distance
     *            Distance in which to look for other objects.
     * @param diag
     *            Is the distance also diagonal?
     * @param cls
     *            Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyShape>
     *            The type of shape to look for, as specified in the cls
     *            parameter.
     * @return A collection of all neighbors found.
     */
    public <MyShape extends Shape> Set<MyShape> getNeighbors(
        Shape shape,
        float distance,
        boolean diag,
        Class<MyShape> cls)
    {
        if (distance < 0.0)
        {
            throw new IllegalArgumentException(
                "Distance must not be less than 0.0. It was: " + distance);
        }
        return null;
        // use shape filter to get shapes
    }


    // ----------------------------------------------------------
    /**
     * Return all objects that intersect a straight line from the location at a
     * specified angle. The angle is clockwise.
     *
     * @param x
     *            x-coordinate.
     * @param y
     *            y-coordinate.
     * @param angle
     *            The angle relative to current rotation of the object. (0-359).
     * @param length
     *            How far we want to look (in cells).
     * @param cls
     *            Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyShape>
     *            The type of shape to look for, as specified in the cls
     *            parameter.
     * @return A collection of all objects found.
     */
    public <MyShape extends Shape> Set<MyShape> getShapesInDirection(
        float x,
        float y,
        float angle,
        float length,
        Class<MyShape> cls)
    {
        return null;
        // use shape filter to get shapes
    }


    // ----------------------------------------------------------
    /**
     * Adds a shape to the {@link ShapeField} currently in use by this view.
     * This method is a shortcut for {@code getShapeField().add(shape)}.
     *
     * @param shape
     *            the shape to add
     */
    public void add(Shape shape)
    {
        shapeField.add(shape);
    }


    // ----------------------------------------------------------
    /**
     * Removes a shape from the {@link ShapeField} currently in use by this
     * view. This method is a shortcut for {@code getShapeField().remove(shape)}
     * .
     *
     * @param shape
     *            the shape to remove
     */
    public void remove(Shape shape)
    {
        shapeField.remove(shape);
    }


    // ----------------------------------------------------------
    /**
     * Removes all shapes from the {@link ShapeField} currently in use by this
     * view. This method is a shortcut for {@code getShapeField().clear()}.
     */
    public void clear()
    {
        shapeField.clear();
    }

    /**
     * Sets the boolean that is used for whether the fps (frames per second)
     * should be displayed on the screen.
     *
     * @param showFps boolean for determining if the fps should be displayed
     */
    public void showFps(boolean showFps)
    {
        this.showFps = showFps;
    }


    // ----------------------------------------------------------
    /**
     * Gets the background color of the view.
     *
     * @return the background {@link Color} of the view
     */
    public Color getBackgroundColor()
    {
        return backgroundColor;
    }


    // ----------------------------------------------------------
    /**
     * Sets the background color of the view.
     *
     * @param color
     *            the desired background {@link Color}
     */
    public void setBackgroundColor(Color color)
    {
        backgroundColor = color;
        // setBackgroundColor(color.toRawColor());

        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public void conditionallyRepaint()
    {
        conditionallyRepaint(null);
    }


    // ----------------------------------------------------------
    public void conditionallyRepaint(RectF bounds)
    {
        if (physicsThread == null && shapeField.hasNonstaticShapes())
        {
            startPhysicsSimulation();
        }

        if (doesAutoRepaint())
        {
            repaint(bounds);
        }
    }


    // ----------------------------------------------------------
    public void repaint()
    {
        repaint(null);
    }


    // ----------------------------------------------------------
    public void repaint(RectF bounds)
    {
        /*
         * if (repaintThread == null) { return; }
         * repaintThread.repaintIfNecessary(bounds);
         */
    }

    private long   lastFrameStart = 0;
    private long   framesThusFar  = 0;
    private double fps            = 0;
    private boolean showFps       = false;


    // ----------------------------------------------------------
    /**
     * The real method that performs shape drawing in response to a callback
     * from the repainting thread.
     */
    private void doRepaint(RectF bounds)
    {
        if (surfaceCreated)
        {
            try
            {
                drawing.canvas = getHolder().lockCanvas(null);

                if (drawing.canvas != null)
                {
                    synchronized (getHolder())
                    {
                        Drawable background = getBackground();

                        if (background != null)
                        {
                            background.draw(drawing.canvas);
                        }
                        else if (backgroundColor != null)
                        {
                            drawing.canvas.drawColor(backgroundColor
                                .toRawColor());
                        }

                        drawContents(bounds);

                        framesThusFar++;

                        if (framesThusFar == 60)
                        {
                            long thisFrame = SystemClock.elapsedRealtime();
                            double duration = thisFrame - lastFrameStart;
                            lastFrameStart = thisFrame;
                            fps = (framesThusFar / (duration / 1000));
                            framesThusFar = 0;
                        }

                        if (showFps)
                        {
                            String fpsStr = "fps: " + String.format("%.1f", fps);
                            drawing.canvas.drawText(fpsStr, 3, 12, new Paint());
                        }
                    }
                }
            }
            finally
            {
                if (drawing.canvas != null)
                {
                    getHolder().unlockCanvasAndPost(drawing.canvas);
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Draw all of this view's shapes on the given canvas.
     *
     * @param repaintBounds
     *            Bounds that are used for drawing.
     */
    protected void drawContents(RectF repaintBounds)
    {
        drawing.canvas.save();
        coordinateSystem.applyTransform(drawing.canvas);

        synchronized (shapeField.getB2World())
        {
            for (Shape shape : shapeField)
            {
                if (shape.getParentView() != null && shape.isVisible()
                    && shape.getBounds() != null)
                {
                    drawing.canvas.save();

                    PointF pos = shape.getPosition();
                    drawing.canvas.rotate(shape.getRotation(), pos.x, pos.y);

                    shape.draw(drawing);

                    drawing.canvas.restore();
                }
            }
        }

        drawing.canvas.restore();
    }


    // ----------------------------------------------------------
    @Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {
        internalSetAutoRepaintForThread(false);
        boolean result = super.dispatchTouchEvent(e);
        internalSetAutoRepaintForThread(true);
        repaint();

        return result;
    }


    // ----------------------------------------------------------
    /**
     * Turn on support for pinching/zoom gestures.
     */
    public void enableScaleGestures()
    {
        // FIXME re-enable

        // ScaleGestureDetector detector = new ScaleGestureDetector(
        // getContext(), new ScaleGestureListener());
        // gestureDetectors.add(detector);
    }


    // ----------------------------------------------------------
    /**
     * Turn on support for rotation gestures.
     */
    public void enableRotateGestures()
    {
        // FIXME re-enable

        // RotateGestureDetector detector = new RotateGestureDetector(
        // getContext(), new RotateGestureListener());
        // gestureDetectors.add(detector);
    }


    // ----------------------------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        int action = e.getAction() & MotionEvent.ACTION_MASK;

        boolean result = false;

        for (Object detector : gestureDetectors)
        {
            try
            {
                Method onTouchEvent =
                    detector.getClass().getMethod(
                        "onTouchEvent",
                        MotionEvent.class);

                boolean thisResult = (Boolean)onTouchEvent.invoke(detector, e);

                result |= thisResult;
            }
            catch (Exception ex)
            {
                // Do nothing.
            }
        }

        /*
         * if (gestureDetector.onTouchEvent(e)) { return true; } else
         */if (action == MotionEvent.ACTION_POINTER_DOWN
            || action == MotionEvent.ACTION_DOWN)
        {
            processTouchEvent(e, onTouchDown);
            return true;
        }
        else if (action == MotionEvent.ACTION_MOVE)
        {
            processTouchEvent(e, onTouchMove);
            return true;
        }
        else if (action == MotionEvent.ACTION_POINTER_UP
            || action == MotionEvent.ACTION_UP)
        {
            processTouchEvent(e, onTouchUp);
            return true;
        }
        else
        {
            return result;
        }
    }


    // ----------------------------------------------------------
    private void processTouchEvent(MotionEvent e, EventDispatcher event)
    {
        boolean eventHandled = false;

        // TODO add "margin" for touch events to make small objects easier to
        // touch.

        if (event == onTouchDown)
        {
            shapeBeingDragged = null;
        }

        if ((event == onTouchMove || event == onTouchUp)
            && shapeBeingDragged != null)
        {
            eventHandled = event.dispatch(shapeBeingDragged, e);
        }
        else
        {
            // FIXME Need a better way to figure out "fuzzy touches"
            PointF worldPt = coordinateSystem.deviceToLocal(e.getX(), e.getY());
            PointF otherPt =
                coordinateSystem.deviceToLocal(e.getX() + 10, e.getY());
            float radius = Math.abs(otherPt.x - worldPt.x);

            Iterable<Shape> shapes = getShapes().locatedWithin(worldPt, radius);

            for (Shape shape : shapes)
            {
                eventHandled |= event.dispatch(shape, e);

                if (event == onTouchDown && onTouchMove.isSupportedBy(shape, e))
                {
                    shapeBeingDragged = shape;
                    break;
                }

                if (eventHandled)
                {
                    break;
                }
            }
        }

        if (event == onTouchUp)
        {
            shapeBeingDragged = null;
        }

        if (!eventHandled)
        {
            eventHandled = event.dispatch(this, e);
        }

        if (!eventHandled)
        {
            Context ctxt = getContext();
            if (ctxt != null)
            {
                eventHandled = event.dispatch(ctxt, e);
            }
        }
    }


    // ----------------------------------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)
    {
        Context ctxt = getContext();
        if (ctxt != null)
        {
            onKeyDown.dispatch(ctxt, e);
        }

        return super.onKeyDown(keyCode, e);
    }


    // ----------------------------------------------------------
    /**
     * Returns true if the left shape is drawn in front of (later than) the
     * shape on the right.
     *
     * @param left
     *            The shape to check.
     * @param right
     *            The shape to check against.
     * @return True if left is drawn in front of (later than) right.
     */
    public boolean isInFrontOf(Shape left, Shape right)
    {
        return shapeField.isInFrontOf(left, right);
    }

    /**
     * Returns the canvas in the current view.
     *
     * @return drawing canvas
     */
    protected Canvas getCanvas()
    {
        return drawing.canvas;
    }


    // ----------------------------------------------------------
    /**
     * Creates the physics thread and starts the simulation.
     */
    private void startPhysicsSimulation()
    {
        if (physicsThread == null)
        {
            physicsThread = new PhysicsThread();
            physicsThread.start();
        }
    }


    // ----------------------------------------------------------
    /**
     * Stops the physics simulation and destroys the thread.
     */
    private void stopPhysicsSimulation()
    {
        if (physicsThread != null)
        {
            physicsThread.cancel();
            physicsThread = null;
        }
    }


    // ~ Inner classes .........................................................

    // ----------------------------------------------------------
    private class CanvasDrawing
        implements Drawing
    {
        public Canvas canvas;


        // ----------------------------------------------------------
        @Override
        public Context getContext()
        {
            return ShapeView.this.getContext();
        }


        // ----------------------------------------------------------
        @Override
        public Canvas getCanvas()
        {
            return canvas;
        }


        // ----------------------------------------------------------
        @Override
        public CoordinateSystem getCoordinateSystem()
        {
            return coordinateSystem;
        }


        // ----------------------------------------------------------
        @Override
        public int getFrameNumber()
        {
            // TODO
            return 0;
        }
    }


    // ----------------------------------------------------------
    private class PhysicsThread
        extends Thread
    {
        private boolean          running;
        private static final int FRAME_RATE = 30;


        public PhysicsThread()
        {
            running = true;
        }


        public synchronized void cancel()
        {
            running = false;
        }


        public synchronized boolean isRunning()
        {
            return running;
        }


        @Override
        public void run()
        {
            lastFrameStart = SystemClock.elapsedRealtime();

            while (isRunning())
            {
                long startTime = SystemClock.elapsedRealtime();

                int velIters = 10;
                int posIters = 8;

                World world = shapeField.getB2World();
                synchronized (world)
                {
                    world.step(1f / FRAME_RATE, velIters, posIters);
                }

                animationManager.step(startTime);

                shapeField.runDeferredOperations();
                shapeField.notifySleepRecipients();
                doRepaint(null);

                long timeUsed = SystemClock.elapsedRealtime() - startTime;
                long remainingTime = 1000 / FRAME_RATE - timeUsed;

                if (remainingTime > 0)
                {
                    // SystemClock.sleep(remainingTime);
                }
            }
        }
    }


    // ----------------------------------------------------------
    private class SurfaceHolderCallback
        implements SurfaceHolder.Callback
    {
        // ----------------------------------------------------------
        public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height)
        {
            repaint();
        }


        // ----------------------------------------------------------
        public void surfaceCreated(SurfaceHolder holder)
        {
            surfaceCreated = true;

            animationManager = new ShapeAnimationManager(ShapeView.this);
            // animationManager.start();

            // if (shapeField != null && shapeField.hasNonstaticShapes())
            {
                startPhysicsSimulation();
            }

            autoRepaint = true;
            repaint();
        }


        // ----------------------------------------------------------
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            surfaceCreated = false;

            stopPhysicsSimulation();

            animationManager.cancel();
            animationManager = null;
        }
    }


    // ----------------------------------------------------------
    /*
     * private class ScaleGestureListener implements
     * ScaleGestureDetector.OnScaleGestureListener { public boolean
     * onScale(ScaleGestureDetector detector) {
     * onScaleForwarder.forward(detector); if
     * (!onScaleForwarder.methodWasFound()) { return false; } else if
     * (onScaleForwarder.result() instanceof Boolean) { return (Boolean)
     * onScaleForwarder.result(); } else { return true; } } public boolean
     * onScaleBegin(ScaleGestureDetector detector) { // TODO Auto-generated
     * method stub return true; } public void onScaleEnd(ScaleGestureDetector
     * detector) { // TODO Auto-generated method stub } } //
     * ---------------------------------------------------------- private class
     * RotateGestureListener implements
     * RotateGestureDetector.OnRotateGestureListener { public boolean
     * onRotate(RotateGestureDetector detector) {
     * onRotateForwarder.forward(detector); if
     * (!onRotateForwarder.methodWasFound()) { return false; } else if
     * (onRotateForwarder.result() instanceof Boolean) { return (Boolean)
     * onRotateForwarder.result(); } else { return true; } } public boolean
     * onRotateBegin(RotateGestureDetector detector) {
     * onRotateBeginForwarder.forward(detector); if
     * (!onRotateBeginForwarder.methodWasFound()) { return true; } else if
     * (onRotateBeginForwarder.result() instanceof Boolean) { return (Boolean)
     * onRotateBeginForwarder.result(); } else { return true; } } public void
     * onRotateEnd(RotateGestureDetector detector) { // TODO Auto-generated
     * method stub } } //
     * ---------------------------------------------------------- // FIXME: Is
     * this supposed to be used? Because it's not
     * @SuppressWarnings("unused") private class ShapeGestureListener extends
     * GestureDetector.SimpleOnGestureListener { //~ Methods
     * ........................................................... //
     * ---------------------------------------------------------- public boolean
     * onFling(MotionEvent startEvent, MotionEvent endEvent, float velocityX,
     * float velocityY) { onFlingForwarder.forward( startEvent, endEvent,
     * velocityX, velocityY); if (!onFlingForwarder.methodWasFound()) { return
     * false; } else if (onFlingForwarder.result() instanceof Boolean) { return
     * (Boolean) onFlingForwarder.result(); } else { return true; } } }
     */

    private class CoordinateRespectingMotionEventDispatcher
        extends MotionEventDispatcher
    {
        private MethodTransformer xyTransformer;


        public CoordinateRespectingMotionEventDispatcher(String method)
        {
            super(method);
        }


        // ----------------------------------------------------------
        /**
         * Transforms an event with signature (MouseEvent event) to one with
         * signature (float x, float y).
         */
        protected MethodTransformer getXYTransformer()
        {
            if (xyTransformer == null)
            {
                xyTransformer =
                    new MethodTransformer(float.class, float.class) {
                        // ----------------------------------------------------------
                        protected Object[] transform(Object... args)
                        {
                            MotionEvent e = (MotionEvent)args[0];
                            PointF pt =
                                coordinateSystem.deviceToLocal(
                                    e.getX(),
                                    e.getY());
                            return new Object[] { pt.x, pt.y };
                        }
                    };
            }

            return xyTransformer;
        }
    }
}
