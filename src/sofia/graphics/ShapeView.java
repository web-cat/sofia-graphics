package sofia.graphics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import sofia.graphics.internal.GeometryUtils;
import sofia.graphics.internal.ShapeAnimationManager;
import sofia.graphics.internal.ShapeSorter;
import sofia.internal.EventForwarder;
import sofia.internal.MethodDispatcher;
import sofia.view.RotateGestureDetector;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//-------------------------------------------------------------------------
/**
 * Represents a view containing drawn {@link Shape} objects.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class ShapeView
    extends SurfaceView
    implements ShapeParent
{
    //~ Instance/static variables .......................................

    private ShapeCollection shapes;
    private boolean needsLayout;
    private boolean surfaceCreated;
    private int defaultBgColor;
    private List<Object> gestureDetectors;
    //private GestureDetector gestureDetector;
    private boolean autoRepaint;
    private Set<Thread> threadsBlockingRepaint;
    private ShapeAnimationManager animationManager;
    private RepaintThread repaintThread;

    // Event forwarders
    private MethodDispatcher onTouchDown =
    		new MethodDispatcher("onTouchDown", 1);
    private MethodDispatcher onTouchMove =
    		new MethodDispatcher("onTouchMove", 1);
    private MethodDispatcher onTouchUp =
    		new MethodDispatcher("onTouchUp", 1);
    private EventForwarder<Object> onScaleForwarder;
    private EventForwarder<Object> onRotateBeginForwarder;
    private EventForwarder<Object> onRotateForwarder;
    private EventForwarder<Object> onFlingForwarder;

    private sofia.graphics.collision.CollisionChecker collisionChecker;
    private Shape shapeBeingDragged;
    private Set<Shape> unresolvedShapes;
    private Set<Shape> shapesWithPositionChanges;
    private Map<Shape, Set<Shape>> activeCollisions;
    private Map<Shape, ViewEdges> activeEdgeCollisions;
    private MethodDispatcher onCollisionWith =
        new MethodDispatcher("onCollisionWith", 1);
    private MethodDispatcher onCollisionBetween =
        new MethodDispatcher("onCollisionBetween", 2);


    //~ Constructors ....................................................

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
     * @param attrs This view's attributes.
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
     * @param context   This view's context.
     * @param attrs     This view's attributes.
     * @param defStyle  This view's default style.
     */
    public ShapeView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }


    //~ Methods .........................................................

    // ----------------------------------------------------------
    private void init()
    {
        threadsBlockingRepaint = new HashSet<Thread>();

        getHolder().addCallback(new SurfaceHolderCallback());

        TypedArray array = getContext().getTheme().obtainStyledAttributes(
            new int[] {
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
            });

        defaultBgColor = array.getColor(0, Color.BLACK);
        array.recycle();

        shapes = new ShapeCollection(this);
//        gestureDetector = new GestureDetector(new ShapeGestureListener());

        gestureDetectors = new ArrayList<Object>();

        onScaleForwarder =
            new EventForwarder<Object>(this, Object.class, "onScale");
        onRotateBeginForwarder =
            new EventForwarder<Object>(this, Object.class, "onRotateBegin");
        onRotateForwarder =
            new EventForwarder<Object>(this, Object.class, "onRotate");
        onFlingForwarder =
            new EventForwarder<Object>(this, Object.class, "onFling");

        repaintThread = new RepaintThread();
        animationManager = new ShapeAnimationManager(this);

        repaintThread.start();
        animationManager.start();

        collisionChecker = new sofia.graphics.collision.IBSPColChecker();
        shapesWithPositionChanges = new HashSet<Shape>();
        unresolvedShapes = new HashSet<Shape>();
        activeCollisions = new HashMap<Shape, Set<Shape>>();
        activeEdgeCollisions = new HashMap<Shape, ViewEdges>();
    }


    // ----------------------------------------------------------
    /**
     * Does this view automatically repaint, or is an explicit call needed?
     * @return True if this view automatically repaints when contained
     * shapes are modified.
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
     * or disabling auto-painting.
     */
    public synchronized void internalSetAutoRepaintForThread(boolean value)
    {
        Thread current = Thread.currentThread();

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
    public Collection<Shape> getShapes()
    {
        return shapes;
    }


    // ----------------------------------------------------------
    /**
     * Add a shape to this view.
     * @param shape The shape to add.
     */
    public void add(Shape shape)
    {
        synchronized (shapes)
        {
            shapes.add(shape);

            if (getWidth() != 0 && getHeight() != 0)
            {
                GeometryUtils.resolveGeometry(shape.getBounds(), shape);
                if (GeometryUtils.isGeometryResolved(shape.getBounds()))
                {
                    collisionChecker.addObject(shape);
                    shapesWithPositionChanges.add(shape);
                }
                else
                {
                    unresolvedShapes.add(shape);
                }
                repaint();
            }
            else
            {
                unresolvedShapes.add(shape);
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Remove a shape from this view.
     * @param shape The shape to remove.
     */
    public void remove(Shape shape)
    {
        synchronized (shapes)
        {
            collisionChecker.removeObject(shape);
            shapes.remove(shape);
            shapesWithPositionChanges.remove(shape);
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
        Shape result = null;
        for (Shape candidate :
            collisionChecker.getObjectsAt(x, y, Shape.class))
        {
            // If multiple candidates, pick the one drawn last (in front)
            if (result == null
                || isInFrontOf(candidate, result))
            {
                result = candidate;
            }
        }
        return result;
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
        return getShapeAt(point.x, point.y);
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
        return collisionChecker.getObjectsAt(x, y, Shape.class);
    }


    // ----------------------------------------------------------
    /**
     * Get all the shapes overlapping the specified location.
     * @param point The location to check.
     * @return A set of all shapes at the specified location.
     */
    public Set<Shape> getShapesAt(PointF point)
    {
        return getShapesAt(point.x, point.y);
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
        synchronized (shapes)
        {
            if (GeometryUtils.isGeometryResolved(shape.getBounds()))
            {
                shapesWithPositionChanges.add(shape);
            }
        }
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
        synchronized (shapes)
        {
            // First, deal with unresolved shapes that may now be resolved
            if (unresolvedShapes.size() > 0)
            {
                Set<Shape> stillUnresolved = new HashSet<Shape>();
                for (Shape shape : unresolvedShapes)
                {
                    if (GeometryUtils.isGeometryResolved(shape.getBounds()))
                    {
                        collisionChecker.addObject(shape);
                        shapesWithPositionChanges.add(shape);
                    }
                    else
                    {
                        stillUnresolved.add(shape);
                    }
                }
                unresolvedShapes = stillUnresolved;
            }

            if (shapesWithPositionChanges.size() > 0)
            {
                // Second, update all positions in the collision checker
                for (Shape shape : shapesWithPositionChanges)
                {
                    collisionChecker.updateObjectLocation(shape);
                }

                // Now, fire collision handlers
                RectF walls = null;
                if (getWidth() > 0 && getHeight() > 0)
                {
                    walls = new RectF(0.0f, 0.0f, getWidth(), getHeight());
                }

                for (Shape shape : shapesWithPositionChanges)
                {
                    // Make sure we don't re-fire on collisions we've
                    // fired before, until those objects separate
                    Set<Shape> oldHits = activeCollisions.get(shape);
                    Set<Shape> newHits = collisionChecker
                        .getIntersectingObjects(shape, Shape.class);

                    // Determine which collisions should fire events
                    Set<Shape> hitsToFire = newHits;
                    if (oldHits != null)
                    {
                        hitsToFire = new HashSet<Shape>(newHits);
                        hitsToFire.removeAll(oldHits);
                    }

                    // Remember current collisions as "active"
                    if (newHits.size() > 0)
                    {
                        activeCollisions.put(shape, newHits);
                    }
                    else
                    {
                        activeCollisions.remove(shape);
                    }

                    // OK, just for brand new collisions now ...
                    for (Shape other : hitsToFire)
                    {
                        // Make sure that, if the other shape is moving too,
                        // we only fire this event once by pre-marking it
                        // as currently active for the other shape
                        Set<Shape> othersHits = activeCollisions.get(other);
                        if (othersHits == null)
                        {
                            othersHits = new HashSet<Shape>();
                            activeCollisions.put(other, othersHits);
                        }
                        othersHits.add(shape);

                        boolean eventHandled =
                            // Handle event on shapes
                            onCollisionWith.callMethodOn(shape, other)
                            || onCollisionWith.callMethodOn(other, shape)

                            // Handled event on view
                            || onCollisionBetween.callMethodOn(
                                this, shape, other);
                        if (!eventHandled)
                        {
                            // Handle event on screen
                            Context ctxt = getContext();
                            if (ctxt != null)
                            {
                                eventHandled = onCollisionBetween
                                    .callMethodOn(ctxt, shape, other);
                            }

//                          ViewParent parent = getParent();
//                          while (!eventHandled && parent != null)
//                          {
//                              eventHandled = eventHandled
//                                  || onCollisionBetween.callMethodOn(
//                                      parent, shape, other);
//                              parent = parent.getParent();
//                          }
                        }
                    }

                    // Now check for collisions with walls
                    ViewEdges edgeCollision = shape.extendsOutside(walls);
                    if (edgeCollision.any())
                    {
                        // Only trigger events if the edge collision is
                        // new/different than what we have seen on the last
                        // move of this shape
                        if (!edgeCollision.equals(
                            activeEdgeCollisions.get(shape)))
                        {
                            activeEdgeCollisions.put(shape, edgeCollision);
                            boolean eventHandled =
                                // Handle event on shape
                                onCollisionWith.callMethodOn(
                                    shape, edgeCollision)

                                // Handled event on view
                                || onCollisionBetween.callMethodOn(
                                        this, shape, edgeCollision);
                            if (!eventHandled)
                            {
                                // Handle event on screen
                                Context ctxt = getContext();
                                if (ctxt != null)
                                {
                                    eventHandled = onCollisionBetween
                                        .callMethodOn(
                                            ctxt, shape, edgeCollision);
                                }

//                            ViewParent parent = getParent();
//                            while (!eventHandled && parent != null)
//                            {
//                                eventHandled = eventHandled
//                                    || onCollisionBetween.callMethodOn(
//                                        parent, shape, edgeCollision);
//                                parent = parent.getParent();
//                            }
                            }
                        }
                    }
                    else
                    {
                        activeEdgeCollisions.remove(shape);
                    }
                }

                // Finally, clear the list of pending shapes that have moved
                shapesWithPositionChanges.clear();
            }
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

                synchronized (getHolder())
                {
                    Drawable background = getBackground();

                    if (background != null)
                    {
                        background.draw(canvas);
                    }
                    else
                    {
                        canvas.drawColor(defaultBgColor);
                    }

                    drawContents(canvas);
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
        synchronized (shapes)
        {
            for (Shape shape : shapes)
            {
                if (shape.isVisible() && shape.getBounds() != null)
                {
                    Matrix xform = shape.getTransform();

                    if (xform != null)
                    {
                        canvas.save();
                        canvas.concat(xform);
                    }

                    shape.draw(canvas);

                    if (xform != null)
                    {
                        canvas.restore();
                    }
                }
            }
        }
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
        ScaleGestureDetector detector = new ScaleGestureDetector(
            getContext(), new ScaleGestureListener());
        gestureDetectors.add(detector);
    }


    // ----------------------------------------------------------
    /**
     * Turn on support for rotation gestures.
     */
    public void enableRotateGestures()
    {
        RotateGestureDetector detector = new RotateGestureDetector(
            getContext(), new RotateGestureListener());
        gestureDetectors.add(detector);
    }


    // ----------------------------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        int action = e.getActionMasked();

        boolean result = false;

        for (Object detector : gestureDetectors)
        {
            try
            {
                Method onTouchEvent = detector.getClass().getMethod(
                    "onTouchEvent", MotionEvent.class);

                boolean thisResult = (Boolean) onTouchEvent.invoke(detector, e);

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
    private void processTouchEvent(MotionEvent e, MethodDispatcher method)
    {
    	boolean eventHandled = false;

    	// TODO add "margin" for touch events to make small objects easier to
    	// touch.

    	if (method == onTouchDown)
    	{
    		shapeBeingDragged = null;
    	}

    	if ((method == onTouchMove || method == onTouchUp)
    			&& shapeBeingDragged != null)
    	{
        	eventHandled = method.callMethodOn(shapeBeingDragged, e);
    	}
    	else
    	{
    		Set<Shape> shapes = collisionChecker.getObjectsAt(
    				e.getX(), e.getY(), null);

	    	for (Shape shape : shapes)
	    	{
	        	eventHandled |= method.callMethodOn(shape, e);
	        	
	        	if (method == onTouchDown && onTouchMove.supportedBy(shape, e))
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

    	if (method == onTouchUp)
    	{
    		shapeBeingDragged = null;
    	}

    	if (!eventHandled)
    	{
    		eventHandled = method.callMethodOn(this, e);
    	}

    	if (!eventHandled)
    	{
            Context ctxt = getContext();
            if (ctxt != null)
            {
        		eventHandled = method.callMethodOn(ctxt, e);
            }
    	}
    }


    // ----------------------------------------------------------
    /**
     * Returns true if the left shape is drawn in front of (later than) the
     * shape on the right.
     * @param left The shape to check.
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


        public synchronized void setRunning(boolean value)
        {
            running = value;
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
                    doRepaint();
                }
                catch (InterruptedException e)
                {
                    // Do nothing.
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

            repaintThread.setRunning(true);
            animationManager.setRunning(true);

            repaint();
        }


        // ----------------------------------------------------------
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            surfaceCreated = false;
            repaintThread.setRunning(false);
            animationManager.setRunning(false);
        }
    }


    // ----------------------------------------------------------
    private class ScaleGestureListener
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
    }
}
