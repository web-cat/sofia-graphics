package sofia.graphics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import sofia.graphics.internal.Box2DUtils;
import sofia.graphics.internal.GeometryUtils;
import sofia.graphics.internal.ShapeAnimationManager;
import sofia.graphics.internal.ShapeSorter;
import sofia.internal.events.EventDispatcher;
import sofia.internal.events.MotionEventDispatcher;
import sofia.internal.events.ReversibleEventDispatcher;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//-------------------------------------------------------------------------
/**
 * Represents a view containing drawn {@link Shape} objects.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public class ShapeView
    extends SurfaceView
    implements ShapeParent, ShapeManipulating, ShapeQuerying
{
    //~ Fields ................................................................

    private ShapeSet<Shape> shapes;
    private boolean needsLayout;
    private boolean surfaceCreated;
    private Color backgroundColor;
    private List<Object> gestureDetectors;
    //private GestureDetector gestureDetector;
    private boolean autoRepaint;
    private Set<Long> threadsBlockingRepaint;
    private ShapeAnimationManager animationManager;
    private RepaintThread repaintThread;
    private PhysicsThread physicsThread;
    private CoordinateSystem coordinateSystem;

    // JBox2D support
    private World b2World;

    // Event forwarders
    private final CoordinateRespectingMotionEventDispatcher onTouchDown =
            new CoordinateRespectingMotionEventDispatcher("onTouchDown");
    private final CoordinateRespectingMotionEventDispatcher onTouchMove =
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

    private static final EventDispatcher onCollisionWith =
            new EventDispatcher("onCollisionWith");
    private static final ReversibleEventDispatcher onCollisionBetween =
            new ReversibleEventDispatcher("onCollisionBetween");
    private static final EventDispatcher onCollisionEndedWith =
            new EventDispatcher("onCollisionEndedWith");
    private static final ReversibleEventDispatcher onCollisionEndedBetween =
            new ReversibleEventDispatcher("onCollisionEndedBetween");

    private Shape shapeBeingDragged;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ShapeView.
     *
     * @param context This view's context.
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
     * @param context This view's context.
     * @param attrs   This view's attributes.
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
     * @param context  This view's context.
     * @param attrs    This view's attributes.
     * @param defStyle This view's default style.
     */
    public ShapeView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public CoordinateSystem getCoordinateSystem()
    {
        return coordinateSystem;
    }


    // ----------------------------------------------------------
    private void init()
    {
        b2World = new World(new Vec2(0, 0));
        b2World.setContactListener(new ContactHandlers());

        threadsBlockingRepaint = new HashSet<Long>();

        getHolder().addCallback(new SurfaceHolderCallback());

        TypedArray array = getContext().getTheme().obtainStyledAttributes(
            new int[] {
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
            });

        backgroundColor = Color.fromRawColor(
                array.getColor(0, android.graphics.Color.BLACK));
        array.recycle();

        shapes = new ShapeSet<Shape>(this);
//        gestureDetector = new GestureDetector(new ShapeGestureListener());

        gestureDetectors = new ArrayList<Object>();
        coordinateSystem = new CoordinateSystem(this);

        setFocusableInTouchMode(true);
    }


    // ----------------------------------------------------------
    /**
     * <strong>This method is intended for internal and advanced usage
     * only.</strong> Gets the JBox2D {@code World} that manages the physical
     * bodies inside this shape view.
     *
     * @return the JBox2D world that manages the physical bodies inside this
     *     shape view
     */
    public World getB2World()
    {
        return b2World;
    }


    // ----------------------------------------------------------
    /**
     * Gets the gravity of the physical world represented by this shape view.
     *
     * @return a {@code PointF} object whose x and y components are the
     *     horizontal and vertical acceleration due to gravity (in units/sec^2)
     *     of the physical world represented by this shape view
     */
    public PointF getGravity()
    {
        return Box2DUtils.vec2ToPointF(b2World.getGravity());
    }


    // ----------------------------------------------------------
    /**
     * Sets the gravity of the physical world represented by this shape view.
     *
     * @param gravity a {@code PointF} whose x and y components are the
     *     horizontal and vertical acceleration due to gravity (in units/sec^2)
     *     of the physical world represented by this shape view
     */
    public void setGravity(PointF gravity)
    {
        b2World.setGravity(Box2DUtils.pointFToVec2(gravity));
    }


    // ----------------------------------------------------------
    /**
     * Sets the gravity of the physical world represented by this shape view.
     *
     * @param xGravity the horizontal acceleration due to gravity (in
     *     units/sec^2)
     * @param yGravity the vertical acceleration due to gravity (in
     *     units/sec^2)
     */
    public void setGravity(float xGravity, float yGravity)
    {
        setGravity(new PointF(xGravity, yGravity));
    }


    // ----------------------------------------------------------
    /**
     * Does this view automatically repaint, or is an explicit call needed?
     * @return True if this view automatically repaints when contained
     *         shapes are modified.
     * @see #setAutoRepaint(boolean)
     */
    public synchronized boolean doesAutoRepaint()
    {
        return autoRepaint && threadsBlockingRepaint.isEmpty();
    }


    // ----------------------------------------------------------
    /**
     * Tell this view to automatically repaint when Shapes change (or not).
     * @param value Whether or not this view should automatically repaint
     *              when shapes change.
     */
    public synchronized void setAutoRepaint(boolean value)
    {
        autoRepaint = value;
    }


    // ----------------------------------------------------------
    /**
     * Used internally to temporarily disable repainting.
     * @param value Says whether the current thread is restoring auto-painting
     *              or disabling auto-painting.
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
     * @return This view's animation manager.
     */
    public ShapeAnimationManager getAnimationManager()
    {
        return animationManager;
    }


    // ----------------------------------------------------------
    /**
     * Gets a set that represents all the shapes currently in this view. Note
     * that this set is not a copy of the view's shape set; changes to this set
     * will <em>directly affect</em> the view.
     *
     * @return a set that represents all the shapes currently in this view
     */
    public ShapeSet<Shape> getShapes()
    {
        return shapes;
    }


    // ----------------------------------------------------------
    /**
     * Get all the shapes of the specified type in this view.
     *
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return List of all the shapes of the specified type (or any of its
     *         subtypes) in the view.
     */
    public <ShapeType extends Shape> ShapeSet<ShapeType> getShapes(
            Class<ShapeType> cls)
    {
        if (cls == null)
        {
            @SuppressWarnings("unchecked")
            ShapeSet<ShapeType> result = (ShapeSet<ShapeType>) getShapes();
            return result;
        }

        synchronized (shapes)
        {
            ShapeSet<ShapeType> result = new ShapeSet<ShapeType>();

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
     * Get one shape (if any) that overlaps the specified location.  If
     * multiple shapes overlap that location, the one "in front" (drawn
     * latest) is returned.
     * @param x The x-coordinate of the location to check.
     * @param y The y-coordinate of the location to check.
     * @return The front-most shape at the specified location, or null if none.
     */
    public Shape getShapeAt(float x, float y)
    {
        return getShapeAt(x, y, null);
    }


    // ----------------------------------------------------------
    /**
     * Get one shape of the specified type (if any) that overlaps the
     * specified location.  If multiple shapes overlap that location, the
     * one "in front" (drawn latest) is returned.
     * @param x The x-coordinate of the location to check.
     * @param y The y-coordinate of the location to check.
     * @param cls Class of shape to look for (passing 'null' will find any
     *            object).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return The front-most shape at the specified location, or null if none.
     */
    public <MyShape extends Shape> MyShape getShapeAt(
        float x, float y, Class<MyShape> cls)
    {
        ShapeSet<MyShape> shapes = getShapesAt(x, y, cls);

        if (shapes.isEmpty())
        {
            return null;
        }
        else
        {
            return shapes.iterator().next();
        }
    }


    // ----------------------------------------------------------
    /**
     * Get one shape (if any) that overlaps the specified location.  If
     * multiple shapes overlap that location, the one "in front" (drawn
     * latest) is returned.
     * @param point The location to check.
     * @return The front-most shape at the specified location, or null if none.
     */
    public Shape getShapeAt(PointF point)
    {
        return getShapeAt(point, null);
    }


    // ----------------------------------------------------------
    /**
     * Get one shape of a specified type (if any) that overlaps the
     * specified location.  If multiple shapes overlap that location, the
     * one "in front" (drawn latest) is returned.
     * @param point The location to check.
     * @param cls Class of shape to look for (passing 'null' will find any
     *            object).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return The front-most shape at the specified location, or null if none.
     */
    public <MyShape extends Shape> MyShape getShapeAt(
        PointF point, Class<MyShape> cls)
    {
        return getShapeAt(point.x, point.y, cls);
    }


    // ----------------------------------------------------------
    /**
     * Get all the shapes overlapping the specified location.
     * @param x The x-coordinate of the location to check.
     * @param y The y-coordinate of the location to check.
     * @return A set of all shapes at the specified location.
     */
    public Set<Shape> getShapesAt(float x, float y)
    {
        return getShapesAt(x, y, null);
    }


    // ----------------------------------------------------------
    /**
     * Get all the shapes of the specified type overlapping the specified
     * location.
     * @param x The x-coordinate of the location to check.
     * @param y The y-coordinate of the location to check.
     * @param cls Class of shape to look for (passing 'null' will find any
     *            object).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return A set of all shapes at the specified location.
     */
    public <ShapeType extends Shape> ShapeSet<ShapeType> getShapesAt(
        float x, float y, Class<ShapeType> cls)
    {
        Vec2 point = new Vec2(x, y);
        AABB queryAABB = new AABB(point, point);

        LocationQueryCallback<ShapeType> callback =
                new LocationQueryCallback<ShapeType>(cls, point);
        b2World.queryAABB(callback, queryAABB);

        return callback.getFoundShapes();
    }


    // ----------------------------------------------------------
    /**
     * Get all the shapes overlapping the specified location.
     * @param point The location to check.
     * @return A set of all shapes at the specified location.
     */
    public Set<Shape> getShapesAt(PointF point)
    {
        return getShapesAt(point, null);
    }


    // ----------------------------------------------------------
    /**
     * Get all the shapes of the specified type overlapping the specified
     * location.
     * @param point The location to check.
     * @param cls Class of shape to look for (passing 'null' will find any
     *            object).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return A set of all shapes at the specified location.
     */
    public <MyShape extends Shape> Set<MyShape> getShapesAt(
        PointF point, Class<MyShape> cls)
    {
        return getShapesAt(point.x, point.y, cls);
    }


    // ----------------------------------------------------------
    /**
     * Return all the shapes that intersect the given shape. This takes the
     * graphical extent of objects into consideration.
     *
     * @param shape A Shape in the view.
     * @param cls Class of other shapes to find (null or Object.class will
     *            find all classes).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return A set of shapes that intersect the given shape.
     */
    public <MyShape extends Shape> Set<MyShape> getIntersectingShapes(
        Shape shape, Class<MyShape> cls)
    {
        return null; //collisionChecker.getIntersectingObjects(shape, cls);
    }


    // ----------------------------------------------------------
    /**
     * Return all the shapes that intersect the given shape. This takes the
     * graphical extent of objects into consideration.
     *
     * @param shape A Shape in the view.
     * @param cls Class of other shapes to find (null or Object.class will
     *            find all classes).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return A set of shapes that intersect the given shape.
     */
    public <MyShape extends Shape> MyShape getIntersectingShape(
        Shape shape, Class<MyShape> cls)
    {
        return null; //collisionChecker.getOneIntersectingObject(shape, cls);
    }


    // ----------------------------------------------------------
    /**
     * Returns all objects with the logical location within the specified
     * circle. In other words an object A is within the range of an object B
     * if the distance between the center of the two objects is less than r.
     *
     * @param x Center of the circle.
     * @param y Center of the circle.
     * @param r Radius of the circle.
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return A set of shapes that lie within the given circle.
     */
    public <MyShape extends Shape> Set<MyShape> getShapesInRange(
        float x, float y, float r, Class<MyShape> cls)
    {
        return null; //collisionChecker.getObjectsInRange(x, y, r, cls);
    }


    // ----------------------------------------------------------
    /**
     * Returns the neighbors to the given location. This method only looks at
     * the logical location and not the extent of objects. Hence it is most
     * useful in scenarios where objects only span one cell.
     *
     * @param shape    The shape whose neighbors will be located.
     * @param distance Distance in which to look for other objects.
     * @param diag     Is the distance also diagonal?
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return A collection of all neighbors found.
     */
    public <MyShape extends Shape> Set<MyShape> getNeighbors(
        Shape shape, float distance, boolean diag, Class<MyShape> cls)
    {
        if (distance < 0.0)
        {
            throw new IllegalArgumentException(
                "Distance must not be less than 0.0. It was: " + distance);
        }
        return null; //collisionChecker.getNeighbors(shape, distance, diag, cls);
    }


    // ----------------------------------------------------------
    /**
     * Return all objects that intersect a straight line from the location at
     * a specified angle. The angle is clockwise.
     *
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param angle The angle relative to current rotation of the object.
     *            (0-359).
     * @param length How far we want to look (in cells).
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyShape> The type of shape to look for, as specified
     *                  in the cls parameter.
     * @return A collection of all objects found.
     */
    public <MyShape extends Shape> Set<MyShape> getShapesInDirection(
        float x, float y, float angle, float length, Class<MyShape> cls)
    {
        return null;
        //return collisionChecker.getObjectsInDirection(
        //    x, y, angle, length, cls);
    }


    // ----------------------------------------------------------
    /**
     * Add a shape to this view.
     * @param shape The shape to add.
     */
    public void add(Shape shape)
    {
        shapes.add(shape);
    }


    // ----------------------------------------------------------
    /**
     * Remove a shape from this view.
     * @param shape The shape to remove.
     */
    public void remove(Shape shape)
    {
        shapes.remove(shape);
    }


    // ----------------------------------------------------------
    /**
     * Removes all shapes currently in this view.
     */
    public void clear()
    {
        shapes.clear();
    }


    // ----------------------------------------------------------
    public void onShapesAdded(Iterable<? extends Shape> addedShapes)
    {
        boolean needsRepaint = false;

        synchronized (shapes)
        {
            for (Shape shape : addedShapes)
            {
                shape.setParent(this);
                shape.createB2Body(b2World);

                if (getWidth() != 0 && getHeight() != 0)
                {
                    GeometryUtils.resolveGeometry(shape.getBounds(), shape);
                    needsRepaint = true;
                }
            }
        }

        if (needsRepaint)
        {
            conditionallyRelayout();
        }
    }


    // ----------------------------------------------------------
    public void onShapesRemoved(Iterable<? extends Shape> removedShapes)
    {
        synchronized (shapes)
        {
            for (Shape shape : removedShapes)
            {
                shape.destroyB2Body(b2World);
                shape.setParent(null);
            }
        }

        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public void onZIndexChanged(Shape shape)
    {
        remove(shape);
        add(shape);
    }


    // ----------------------------------------------------------
    public void onPositionChanged(Shape shape)
    {
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
     * @param color the desired background {@link Color}
     */
    public void setBackgroundColor(Color color)
    {
        backgroundColor = color;
        //setBackgroundColor(color.toRawColor());

        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public ShapeParent getShapeParent()
    {
        return null;
    }


    // ----------------------------------------------------------
    public void conditionallyRepaint()
    {
        if (doesAutoRepaint())
        {
            repaint();
        }
    }


    // ----------------------------------------------------------
    public void repaint()
    {
        if (repaintThread == null)
        {
            return;
        }

        repaintThread.repaintIfNecessary();
    }


    // ----------------------------------------------------------
    /**
     * The real method that performs shape drawing in response to a
     * callback from the repainting thread.
     */
    public void doRepaint()
    {
        if (surfaceCreated)
        {
            Canvas canvas = null;

            try
            {
                canvas = getHolder().lockCanvas(null);

                if (canvas != null)
                {
                    synchronized (getHolder())
                    {
                        Drawable background = getBackground();

                        if (background != null)
                        {
                            background.draw(canvas);
                        }
                        else if (backgroundColor != null)
                        {
                            canvas.drawColor(backgroundColor.toRawColor());
                        }

                        drawContents(canvas);
                    }
                }
            }
            finally
            {
                if (canvas != null)
                {
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }
    }


    // ----------------------------------------------------------
    public RectF getBounds()
    {
        return new RectF(0, 0, getWidth(), getHeight());
    }


    // ----------------------------------------------------------
    @Override
    protected void onLayout(
        boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
    }


    // ----------------------------------------------------------
    public void conditionallyRelayout()
    {
        if (doesAutoRepaint())
        {
            relayout();
        }
    }


    // ----------------------------------------------------------
    public void relayout()
    {
        if (needsLayout)
        {
            synchronized (shapes)
            {
                ShapeSorter sorter = new ShapeSorter(shapes);

                for (Shape shape : sorter.sorted())
                {
                    RectF bounds = shape.getBounds();

                    if (!GeometryUtils.isGeometryResolved(bounds))
                    {
                        GeometryUtils.resolveGeometry(bounds, shape);
                        shape.onBoundsResolved();
                    }
                }
            }

            needsLayout = false;
        }

        repaint();
    }


    // ----------------------------------------------------------
    /**
     * Draw all of this view's shapes on the given canvas.
     * @param canvas The canvas to draw on.
     */
    protected void drawContents(Canvas canvas)
    {
        canvas.save();
        coordinateSystem.applyTransform(canvas);

        synchronized (shapes)
        {
            for (Shape shape : shapes)
            {
                if (shape.isVisible() && shape.getBounds() != null)
                {
                    /*Matrix xform = shape.getTransform();

                    if (xform != null)
                    {
                        // The xform is in bounding-box-relative coords.
                        // Make a copy.
                        xform = new Matrix(xform);

                        // Before the rotation, translate the bounding box
                        // to the origin.
                        xform.preTranslate(
                            -shape.getBounds().left, -shape.getBounds().top);

                        // After the rotation, translate the bounding box
                        // back into its original position.
                        xform.postTranslate(
                            shape.getBounds().left, shape.getBounds().top);
                        canvas.save();
                        canvas.concat(xform);
                    }*/

                    canvas.save();

                    Vec2 pos = shape.getB2Body().getPosition();
                    canvas.rotate(shape.getRotation(), pos.x, pos.y);

                    shape.draw(canvas);

                    canvas.restore();
                }
            }
        }

        canvas.restore();
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

        //ScaleGestureDetector detector = new ScaleGestureDetector(
        //    getContext(), new ScaleGestureListener());
        //gestureDetectors.add(detector);
    }


    // ----------------------------------------------------------
    /**
     * Turn on support for rotation gestures.
     */
    public void enableRotateGestures()
    {
        // FIXME re-enable

        //RotateGestureDetector detector = new RotateGestureDetector(
        //    getContext(), new RotateGestureListener());
        //gestureDetectors.add(detector);
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
                Method onTouchEvent = detector.getClass().getMethod(
                    "onTouchEvent", MotionEvent.class);

                boolean thisResult = (Boolean)onTouchEvent.invoke(detector, e);

                result |= thisResult;
            }
            catch (Exception ex)
            {
                // Do nothing.
            }
        }

        /*if (gestureDetector.onTouchEvent(e))
        {
            return true;
        }
        else*/ if (action == MotionEvent.ACTION_POINTER_DOWN
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
        else if (action == MotionEvent.ACTION_POINTER_UP ||
            action == MotionEvent.ACTION_UP)
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
            Set<Shape> shapes = getShapesAt(e.getX(), e.getY());

            for (Shape shape : shapes)
            {
                eventHandled |= event.dispatch(shape, e);

                if (event == onTouchDown
                        && onTouchMove.isSupportedBy(shape, e))
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
     * @param left  The shape to check.
     * @param right The shape to check against.
     * @return True if left is drawn in front of (later than) right.
     */
    public boolean isInFrontOf(Shape left, Shape right)
    {
        return shapes.isInFrontOf(left, right);
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    private class RepaintThread extends Thread
    {
        private boolean running;
        private ArrayBlockingQueue<Boolean> queue;


        public RepaintThread()
        {
            running = true;
            queue = new ArrayBlockingQueue<Boolean>(1);
        }


        public synchronized void cancel()
        {
            running = false;
            repaintIfNecessary();
        }


        public synchronized boolean isRunning()
        {
            return running;
        }


        public void repaintIfNecessary()
        {
            queue.offer(Boolean.TRUE);
        }


        @Override
        public void run()
        {
            while (isRunning())
            {
                try
                {
                    queue.take();

                    if (isRunning())
                    {
                        doRepaint();
                    }
                }
                catch (InterruptedException e)
                {
                    // Do nothing.
                }
            }
        }
    }


    // ----------------------------------------------------------
    private class PhysicsThread extends Thread
    {
        private boolean running;
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
            while (isRunning())
            {
                long startTime = SystemClock.elapsedRealtime();

                int velIters = 3;
                int posIters = 8;

                synchronized (shapes)
                {
                    b2World.step(1f / FRAME_RATE, velIters, posIters);
                }

                repaint();

                long timeUsed = SystemClock.elapsedRealtime() - startTime;
                long remainingTime = 1000 / FRAME_RATE - timeUsed;

                if (remainingTime > 0)
                {
                    SystemClock.sleep(remainingTime);
                }
            }
        }
    }


    // ----------------------------------------------------------
    private class SurfaceHolderCallback implements SurfaceHolder.Callback
    {
        // ----------------------------------------------------------
        public void surfaceChanged(SurfaceHolder holder, int format,
            int width, int height)
        {
            needsLayout = true;
            relayout();
        }


        // ----------------------------------------------------------
        public void surfaceCreated(SurfaceHolder holder)
        {
            surfaceCreated = true;

            physicsThread = new PhysicsThread();
            repaintThread = new RepaintThread();
            animationManager = new ShapeAnimationManager(ShapeView.this);

            physicsThread.start();
            repaintThread.start();
            animationManager.start();

            autoRepaint = true;
            repaint();
        }


        // ----------------------------------------------------------
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            surfaceCreated = false;

            physicsThread.cancel();
            physicsThread = null;

            repaintThread.cancel();
            repaintThread = null;

            animationManager.cancel();
            animationManager = null;
        }
    }


    // ----------------------------------------------------------
    /*private class ScaleGestureListener
        implements ScaleGestureDetector.OnScaleGestureListener
    {
        public boolean onScale(ScaleGestureDetector detector)
        {
            onScaleForwarder.forward(detector);

            if (!onScaleForwarder.methodWasFound())
            {
                return false;
            }
            else if (onScaleForwarder.result() instanceof Boolean)
            {
                return (Boolean) onScaleForwarder.result();
            }
            else
            {
                return true;
            }
        }

        public boolean onScaleBegin(ScaleGestureDetector detector)
        {
            // TODO Auto-generated method stub
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector)
        {
            // TODO Auto-generated method stub
        }
    }


    // ----------------------------------------------------------
    private class RotateGestureListener
        implements RotateGestureDetector.OnRotateGestureListener
    {
        public boolean onRotate(RotateGestureDetector detector)
        {
            onRotateForwarder.forward(detector);

            if (!onRotateForwarder.methodWasFound())
            {
                return false;
            }
            else if (onRotateForwarder.result() instanceof Boolean)
            {
                return (Boolean) onRotateForwarder.result();
            }
            else
            {
                return true;
            }
        }

        public boolean onRotateBegin(RotateGestureDetector detector)
        {
            onRotateBeginForwarder.forward(detector);

            if (!onRotateBeginForwarder.methodWasFound())
            {
                return true;
            }
            else if (onRotateBeginForwarder.result() instanceof Boolean)
            {
                return (Boolean) onRotateBeginForwarder.result();
            }
            else
            {
                return true;
            }
        }

        public void onRotateEnd(RotateGestureDetector detector)
        {
            // TODO Auto-generated method stub

        }
    }


    // ----------------------------------------------------------
    // FIXME: Is this supposed to be used?  Because it's not
    @SuppressWarnings("unused")
    private class ShapeGestureListener
        extends GestureDetector.SimpleOnGestureListener
    {
        //~ Methods ...........................................................

        // ----------------------------------------------------------
        public boolean onFling(MotionEvent startEvent, MotionEvent endEvent,
            float velocityX, float velocityY)
        {
            onFlingForwarder.forward(
                startEvent, endEvent, velocityX, velocityY);

            if (!onFlingForwarder.methodWasFound())
            {
                return false;
            }
            else if (onFlingForwarder.result() instanceof Boolean)
            {
                return (Boolean) onFlingForwarder.result();
            }
            else
            {
                return true;
            }
        }
    }*/


    // ----------------------------------------------------------
    private class ContactHandlers implements ContactListener
    {
        // ------------------------------------------------------
        public void beginContact(Contact contact)
        {
            handleContact(contact, onCollisionBetween, onCollisionWith);
        }


        // ------------------------------------------------------
        public void endContact(Contact contact)
        {
            handleContact(contact,
                    onCollisionEndedBetween, onCollisionEndedWith);
        }


        // ------------------------------------------------------
        public void postSolve(Contact arg0, ContactImpulse arg1)
        {
        }


        // ------------------------------------------------------
        public void preSolve(Contact arg0, Manifold arg1)
        {
        }


        // ----------------------------------------------------------
        private void handleContact(Contact contact,
                ReversibleEventDispatcher betweenDispatcher,
                EventDispatcher withDispatcher)
        {
            // TODO We may want to pass the contact info to the method as
            // well...

            Shape shape = (Shape) contact.m_fixtureA.m_userData;
            Shape other = (Shape) contact.m_fixtureB.m_userData;

            // Necessary sanity check?
            if (shape != null && other != null)
            {
                boolean eventHandled =
                    // Handle event on shapes
                    withDispatcher.dispatch(shape, other)
                    || withDispatcher.dispatch(other, shape)

                    // Handled event on view
                    || betweenDispatcher.dispatch(this, shape, other);

                if (!eventHandled)
                {
                    // Handle event on screen
                    Object ctxt = getContext();
                    if (ctxt != null)
                    {
                        eventHandled = betweenDispatcher
                            .dispatch(ctxt, shape, other);
                    }
                }
            }
        }
    }


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
                xyTransformer = new MethodTransformer(float.class, float.class)
                {
                    // ----------------------------------------------------------
                    protected Object[] transform(Object... args)
                    {
                        MotionEvent e = (MotionEvent) args[0];
                        PointF pt = coordinateSystem.inverseTransform(
                                e.getX(), e.getY());
                        return new Object[] { pt.x, pt.y };
                    }
                };
            }

            return xyTransformer;
        }
    }


    // ----------------------------------------------------------
    /**
     * This nested class creates the callback type to handle reporting
     * the individual fixtures within a region a query is called on.
     */
    private static class LocationQueryCallback<ShapeType extends Shape>
        implements QueryCallback
    {
        //~ Fields ............................................................

        private Vec2 point;
        private ShapeSet<ShapeType> foundShapes;
        private Class<ShapeType> klass;


        //~ Constructors ......................................................

        // ------------------------------------------------------
        public LocationQueryCallback(Class<ShapeType> cls, Vec2 point)
        {
            this.klass = cls;
            this.point = point;
            foundShapes = new ShapeSet<ShapeType>();
        }


        //~ Methods ...........................................................

        // ------------------------------------------------------
        /**
         * This method controls what is to occur when a fixture is found,
         * if it should be added to the list of fixtures.
         *
         * @return false terminates the query.
         */
        @SuppressWarnings("unchecked")
        public boolean reportFixture(Fixture aFixture)
        {
            Shape shape = (Shape) aFixture.m_userData;
            boolean inside = aFixture.testPoint(point);

            if (inside && (klass == null
                    || klass.isAssignableFrom(shape.getClass())))
            {
                foundShapes.add((ShapeType) shape);
            }

            return true;
        }


        // ----------------------------------------------------------
        public ShapeSet<ShapeType> getFoundShapes()
        {
            return foundShapes;
        }
    }
}
