package sofia.graphics;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import sofia.internal.Reversed;

//-------------------------------------------------------------------------
/**
 * Represents a collection of {@link Shape} objects held in drawing order,
 * based on z-index and insertion time.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class ShapeSet<ShapeType extends Shape>
    implements Set<ShapeType>
{
    //~ Fields ................................................................

    private ShapeParent        parent;
    private TreeSet<ShapeType> treeSet;
    private ZIndexComparator   zorder;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new shape set that is not associated with a shape parent.
     */
    public ShapeSet()
    {
        this(null);
    }


    // ----------------------------------------------------------
    /**
     * Create a new shape set that notifies the specified shape parent when
     * shapes are added to it or removed from it.
     *
     * @param parent The shape parent associated with this shape collection.
     */
    public ShapeSet(ShapeParent parent)
    {
        this.parent = parent;

        zorder  = new ZIndexComparator();
        treeSet = new TreeSet<ShapeType>(zorder);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public synchronized boolean add(ShapeType shape)
    {
        shape.updateTimeAddedToParent();

        boolean result = treeSet.add(shape);
        sendOnShapesAdded(Collections.singleton(shape));
        return result;
    }


    // ----------------------------------------------------------
    public synchronized boolean addAll(
            Collection<? extends ShapeType> collection)
    {
        for (Shape shape : collection)
        {
            shape.updateTimeAddedToParent();
        }

        boolean result = treeSet.addAll(collection);
        sendOnShapesAdded(collection);
        return result;
    }


    // ----------------------------------------------------------
    public synchronized void clear()
    {
        TreeSet<ShapeType> oldTreeSet = treeSet;
        treeSet = new TreeSet<ShapeType>(zorder);
        sendOnShapesRemoved(oldTreeSet);
    }


    // ----------------------------------------------------------
    public synchronized boolean contains(Object object)
    {
        return treeSet.contains(object);
    }


    // ----------------------------------------------------------
    public synchronized boolean containsAll(Collection<?> collection)
    {
        return treeSet.contains(collection);
    }


    // ----------------------------------------------------------
    public synchronized boolean isEmpty()
    {
        return treeSet.isEmpty();
    }


    // ----------------------------------------------------------
    public Iterator<ShapeType> iterator()
    {
        return new WrappingIterator(treeSet.iterator(), true);
    }


    // ----------------------------------------------------------
    /**
     * Access an iterator that traverses the collection from "front" (top) to
     * "back" (bottom) in terms of drawing order.
     * @return An iterator representing this traversal order.
     */
    public synchronized Iterator<ShapeType> frontToBackIterator()
    {
        @SuppressWarnings("unchecked")
        ShapeType[] array = (ShapeType[]) new Shape[size()];
        treeSet.toArray(array);

        return new WrappingIterator(Reversed.reversed(array).iterator(), true);
    }


    // ----------------------------------------------------------
    public synchronized boolean remove(Object object)
    {
        boolean result = treeSet.remove(object);

        if (result)
        {
            sendOnShapesRemoved(Collections.singleton((Shape) object));
        }

        return result;
    }


    // ----------------------------------------------------------
    public synchronized boolean removeAll(Collection<?> collection)
    {
        boolean modified = false;

        Iterator<ShapeType> it = iterator();
        TreeSet<ShapeType> removedShapes = new TreeSet<ShapeType>(zorder);
        while (it.hasNext())
        {
            ShapeType shape = it.next();

            if (collection.contains(shape))
            {
                // Since we're using the wrapping iterator here, the parent
                // will be unset properly.

                removedShapes.add(shape);
                it.remove();
                modified = true;
            }
        }

        if (modified)
        {
            sendOnShapesRemoved(removedShapes);
        }

        return modified;
    }


    // ----------------------------------------------------------
    public synchronized boolean retainAll(Collection<?> collection)
    {
        boolean modified = false;

        Iterator<ShapeType> it = iterator();
        TreeSet<ShapeType> removedShapes = new TreeSet<ShapeType>(zorder);
        while (it.hasNext())
        {
            ShapeType shape = it.next();

            if (!collection.contains(shape))
            {
                removedShapes.add(shape);
                it.remove();
                modified = true;
            }
        }

        if (modified)
        {
            sendOnShapesRemoved(removedShapes);
        }

        return modified;
    }


    // ----------------------------------------------------------
    public synchronized int size()
    {
        return treeSet.size();
    }


    // ----------------------------------------------------------
    public synchronized Shape[] toArray()
    {
        Shape[] result = new Shape[treeSet.size()];
        return toArray(result);
    }


    // ----------------------------------------------------------
    public synchronized <T> T[] toArray(T[] array)
    {
        return treeSet.toArray(array);
    }


    // ----------------------------------------------------------
    /**
     * Returns true if the left shape is drawn in front of (later than) the
     * shape on the right.
     * @param left The shape to check.
     * @param right The shape to check against.
     * @return True if left is drawn above (later than) right.
     */
    public boolean isInFrontOf(Shape left, Shape right)
    {
        return zorder.compare(left, right) > 0;
    }


    // ----------------------------------------------------------
    /**
     * Get the shape order for this shape set.
     * @return The current shape ordering, in the form of a comparator.
     */
    public ZIndexComparator getDrawingOrder()
    {
        return zorder;
    }


    // ----------------------------------------------------------
    /**
     * Change the shape order for this shape set.
     * @param order The new ordering to use.
     */
    public synchronized void setDrawingOrder(ZIndexComparator order)
    {
        TreeSet<ShapeType> newSet = new TreeSet<ShapeType>(order);
        newSet.addAll(treeSet);
        zorder = order;
        treeSet = newSet;
    }


    // ----------------------------------------------------------
    private void sendOnShapesAdded(Iterable<? extends ShapeType> shapesAdded)
    {
        if (parent != null)
        {
            parent.onShapesAdded(shapesAdded);
        }
    }


    // ----------------------------------------------------------
    private void sendOnShapesRemoved(Iterable<? extends Shape> shapesRemoved)
    {
        if (parent != null)
        {
            parent.onShapesRemoved(shapesRemoved);
        }
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    private class WrappingIterator implements Iterator<ShapeType>
    {
        private Iterator<ShapeType> iterator;
        private boolean notifyParent;
        private ShapeType lastShape;


        // ----------------------------------------------------------
        public WrappingIterator(
                Iterator<ShapeType> iterator, boolean notifyParent)
        {
            this.iterator = iterator;
            this.notifyParent = notifyParent;
        }


        // ----------------------------------------------------------
        public boolean hasNext()
        {
            synchronized (ShapeSet.this)
            {
                return iterator.hasNext();
            }
        }


        // ----------------------------------------------------------
        public ShapeType next()
        {
            synchronized (ShapeSet.this)
            {
                lastShape = iterator.next();
                return lastShape;
            }
        }


        // ----------------------------------------------------------
        public void remove()
        {
            synchronized (ShapeSet.this)
            {
                iterator.remove();

                if (lastShape != null)
                {
                    if (notifyParent)
                    {
                        sendOnShapesRemoved(Collections.singleton(lastShape));
                    }

                    lastShape = null;
                }
            }
        }
    }
}
