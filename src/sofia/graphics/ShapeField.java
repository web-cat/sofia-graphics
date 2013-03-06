package sofia.graphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import sofia.graphics.internal.Box2DUtils;
import sofia.internal.events.EventDispatcher;
import sofia.internal.events.ReversibleEventDispatcher;
import android.graphics.PointF;

//-------------------------------------------------------------------------
/**
 * A shape field is a shape set that also provides (optional) real-time physics
 * simulation. By default, a {@link ShapeView} creates an empty
 * {@link ShapeField} to hold its shapes, but you can also have multiple
 * shape fields in an application and swap them in and out of the view as
 * needed (for example, to represent different levels of a game).
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class ShapeField extends ShapeSet<Shape>
{
    //~ Fields ................................................................

    private long SHAPE_ADD_COUNTER = 1;

    private ShapeView view;
    private IdentityHashMap<Shape, Long> shapeAddTimes;
    private World b2World;

    private HashMap<Shape, Boolean> sleepRecipients;
    private EventDispatcher onSleep = new EventDispatcher("onSleep");
    private EventDispatcher onWake = new EventDispatcher("onWake");

    private List<Runnable> deferredOperations;


    //~ Events ................................................................

    private static final EventDispatcher onCollisionWith =
            new EventDispatcher("onCollisionWith");
    private static final ReversibleEventDispatcher onCollisionBetween =
            new ReversibleEventDispatcher("onCollisionBetween");
    private static final EventDispatcher onCollisionEndedWith =
            new EventDispatcher("onCollisionEndedWith");
    private static final ReversibleEventDispatcher onCollisionEndedBetween =
            new ReversibleEventDispatcher("onCollisionEndedBetween");


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new, empty shape field with no gravity.
     */
    public ShapeField()
    {
        view = null;
        shapeAddTimes = new IdentityHashMap<Shape, Long>();

        deferredOperations = new ArrayList<Runnable>();

        b2World = new World(new Vec2(0, 0));
        b2World.setContactListener(new ContactHandlers());

        sleepRecipients = new HashMap<Shape, Boolean>();
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * <p>
     * Gets a filter that can be used to find shapes that match a certain set
     * of criteria. The search criteria are specified by chaining method calls
     * to the returned filter. For example, to find all the red shapes that
     * intersect with a particular rectangular area on the screen, one can
     * write:
     * </p>
     * <pre>
     *     getShapes().withColor(Color.red).intersecting(0, 0, 50, 50)
     * </pre>
     * <p>
     * The {@link ShapeFilter} object returned by one of these filter chains
     * implements the {@code Iterable} interface so you can easily use it in a
     * for-each loop to process the matching shapes. It also supports other
     * convenience methods; see the documentation for {@link ShapeFilter} for
     * more information.
     * </p>
     *
     * @return a {@link ShapeFilter} that matches and allows iteration over the
     *     shapes that match a set of criteria
     */
    public ShapeFilter<Shape> getShapes()
    {
        return new ShapeFilter<Shape>(null, null)
        {
            @Override
            protected World b2World()
            {
                return b2World;
            }

            @Override
            protected boolean thisFilterAccepts(Shape shape)
            {
                return true;
            }
        };
    }


    // ----------------------------------------------------------
    @Override
    public boolean add(Shape shape)
    {
        synchronized (b2World)
        {
            if (!shapeAddTimes.containsKey(shape))
            {
                // These two lines have to come before the shape is added to
                // the tree set (by calling super) because the ordering of the
                // tree set depends on being able to look up the add-time of
                // the shape from its field.

                shapeAddTimes.put(shape, SHAPE_ADD_COUNTER++);
                shape.setShapeField(this);

                super.add(shape);

                handleShapesAdded(Collections.singleton(shape));
                return true;
            }
            else
            {
                return false;
            }
        }
    }


    // ----------------------------------------------------------
    @Override
    public boolean addAll(Collection<? extends Shape> collection)
    {
        synchronized (b2World)
        {
            boolean result = false;

            for (Shape shape : collection)
            {
                result |= add(shape);
            }

            return result;
        }
    }


    // ----------------------------------------------------------
    @Override
    public void clear()
    {
        synchronized (b2World)
        {
            TreeSet<Shape> rawSet = rawSet();

            super.clear();
            shapeAddTimes.clear();

            handleShapesRemoved(rawSet);
        }
    }


    // ----------------------------------------------------------
    @Override
    public boolean contains(Object object)
    {
        synchronized (b2World)
        {
            return super.contains(object);
        }
    }


    // ----------------------------------------------------------
    @Override
    public boolean containsAll(Collection<?> collection)
    {
        synchronized (b2World)
        {
            return super.containsAll(collection);
        }
    }


    // ----------------------------------------------------------
    @Override
    public Shape front()
    {
        synchronized (b2World)
        {
            return super.front();
        }
    }


    // ----------------------------------------------------------
    @Override
    public Shape back()
    {
        synchronized (b2World)
        {
            return super.back();
        }
    }


    // ----------------------------------------------------------
    @Override
    public Iterator<Shape> frontToBackIterator()
    {
        return new WrappingIterator(super.frontToBackIterator(), true);
    }


    // ----------------------------------------------------------
    @Override
    public boolean isEmpty()
    {
        synchronized (b2World)
        {
            return super.isEmpty();
        }
    }


    // ----------------------------------------------------------
    @Override
    public Iterator<Shape> iterator()
    {
        return new WrappingIterator(super.iterator(), true);
    }


    // ----------------------------------------------------------
    @Override
    public boolean remove(Object object)
    {
        synchronized (b2World)
        {
            boolean result = super.remove(object);

            if (result)
            {
                shapeAddTimes.remove(object);
                handleShapesRemoved(Collections.singleton((Shape) object));
            }

            return result;
        }
    }


    // ----------------------------------------------------------
    @Override
    public boolean removeAll(Collection<?> collection)
    {
        synchronized (b2World)
        {
            boolean modified = false;

            Iterator<Shape> it = iterator();
            TreeSet<Shape> removedShapes =
                    new TreeSet<Shape>(getDrawingOrder());
            while (it.hasNext())
            {
                Shape shape = it.next();

                if (collection.contains(shape))
                {
                    removedShapes.add(shape);
                    it.remove();
                    modified = true;
                }
            }

            if (modified)
            {
                handleShapesRemoved(removedShapes);
            }

            return modified;
        }
    }


    // ----------------------------------------------------------
    @Override
    public boolean retainAll(Collection<?> collection)
    {
        synchronized (b2World)
        {
            boolean modified = false;

            Iterator<Shape> it = iterator();
            TreeSet<Shape> removedShapes =
                    new TreeSet<Shape>(getDrawingOrder());
            while (it.hasNext())
            {
                Shape shape = it.next();

                if (!collection.contains(shape))
                {
                    removedShapes.add(shape);
                    it.remove();
                    modified = true;
                }
            }

            if (modified)
            {
                handleShapesRemoved(removedShapes);
            }

            return modified;
        }
    }


    // ----------------------------------------------------------
    @Override
    public int size()
    {
        synchronized (b2World)
        {
            return super.size();
        }
    }


    // ----------------------------------------------------------
    @Override
    public Shape[] toArray()
    {
        synchronized (b2World)
        {
            return super.toArray();
        }
    }


    // ----------------------------------------------------------
    @Override
    public <T> T[] toArray(T[] array)
    {
        synchronized (b2World)
        {
            return super.toArray(array);
        }
    }


    // ----------------------------------------------------------
    @Override
    public int hashCode()
    {
        synchronized (b2World)
        {
            return super.hashCode();
        }
    }


    // ----------------------------------------------------------
    @Override
    public boolean equals(Object object)
    {
        synchronized (b2World)
        {
            return super.equals(object);
        }
    }


    // ----------------------------------------------------------
    @Override
    public void setDrawingOrder(ZIndexComparator order)
    {
        synchronized (b2World)
        {
            super.setDrawingOrder(order);
        }
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
     * <strong>This method is intended for internal and advanced usage
     * only.</strong> Gets the JBox2D {@code World} that manages the physical
     * bodies inside this shape view.
     *
     * @return the JBox2D world that manages the physical bodies inside this
     *     shape view
     */
    public final World getB2World()
    {
        return b2World;
    }


    // ----------------------------------------------------------
    public final ShapeView getView()
    {
        return view;
    }


    // ----------------------------------------------------------
    /*package*/ final void setView(ShapeView newView)
    {
        view = newView;
    }


    // ----------------------------------------------------------
    /*package*/ final void updateZIndex(Shape shape, int newZIndex)
    {
        rawSet().remove(shape);
        shape.rawSetZIndex(newZIndex);
        rawSet().add(shape);
    }


    // ----------------------------------------------------------
    /*package*/ final long getShapeAddedTime(Shape shape)
    {
        return shapeAddTimes.get(shape);
    }


    // ----------------------------------------------------------
    /*package*/ final void notifySleepRecipients()
    {
        for (Map.Entry<Shape, Boolean> entry : sleepRecipients.entrySet())
        {
            Shape shape = entry.getKey();
            Body b2Body = shape.getB2Body();
            boolean previouslyAsleep = entry.getValue();

            if (b2Body != null)
            {
                boolean nowAsleep = !b2Body.isAwake();

                if (previouslyAsleep != nowAsleep)
                {
                    if (nowAsleep)
                    {
                        onSleep.dispatch(shape);
                    }
                    else
                    {
                        onWake.dispatch(shape);
                    }

                    entry.setValue(nowAsleep);
                }
            }
        }
    }


    // ----------------------------------------------------------
    private void registerSleepRecipient(Shape shape)
    {
        try
        {
            if (shape.getClass().getMethod("onSleep") != null
                    || shape.getClass().getMethod("onWake") != null)
            {
                sleepRecipients.put(shape, false);
            }
        }
        catch (NoSuchMethodException e)
        {
            // Do nothing.
        }
    }


    // ----------------------------------------------------------
    private void handleShapesAdded(Iterable<? extends Shape> addedShapes)
    {
        for (Shape shape : addedShapes)
        {
            shape.createB2Body(this);
            registerSleepRecipient(shape);
        }

        if (view != null)
        {
            view.conditionallyRepaint();
        }
    }


    // ----------------------------------------------------------
    private void handleShapesRemoved(Iterable<? extends Shape> removedShapes)
    {
        for (Shape shape : removedShapes)
        {
            sleepRecipients.remove(shape);
            shape.destroyB2Body(this);
            shape.setShapeField(null);
        }

        if (view != null)
        {
            view.conditionallyRepaint();
        }
    }


    // ----------------------------------------------------------
    /*package*/ void runOrDefer(Runnable runnable)
    {
        World world = getB2World();

        synchronized (world)
        {
            if (world.isLocked())
            {
                deferredOperations.add(runnable);
            }
            else
            {
                runnable.run();
            }
        }
    }


    // ----------------------------------------------------------
    /*package*/ void runDeferredOperations()
    {
        for (Runnable runnable : deferredOperations)
        {
            runnable.run();
        }

        deferredOperations.clear();
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    private class WrappingIterator implements Iterator<Shape>
    {
        private Iterator<Shape> iterator;
        private boolean notifyParent;
        private Shape lastShape;


        // ----------------------------------------------------------
        public WrappingIterator(
                Iterator<Shape> iterator, boolean notifyParent)
        {
            this.iterator = iterator;
            this.notifyParent = notifyParent;
        }


        // ----------------------------------------------------------
        public boolean hasNext()
        {
            synchronized (b2World)
            {
                return iterator.hasNext();
            }
        }


        // ----------------------------------------------------------
        public Shape next()
        {
            synchronized (b2World)
            {
                lastShape = iterator.next();
                return lastShape;
            }
        }


        // ----------------------------------------------------------
        public void remove()
        {
            synchronized (b2World)
            {
                iterator.remove();

                if (lastShape != null)
                {
                    if (notifyParent)
                    {
                        handleShapesRemoved(Collections.singleton(lastShape));
                    }

                    shapeAddTimes.remove(lastShape);
                    lastShape = null;
                }
            }
        }
    }


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
                    // Handled event on field itself
                    || betweenDispatcher.dispatch(ShapeField.this, shape, other);

                if (!eventHandled)
                {
                    if (view != null)
                    {
                        eventHandled = betweenDispatcher
                                .dispatch(view, shape, other);

                        // Handle event on screen
                        Object ctxt = view.getContext();
                        if (ctxt != null)
                        {
                            eventHandled = betweenDispatcher
                                .dispatch(ctxt, shape, other);
                        }
                    }
                }
            }
        }
    }
}
