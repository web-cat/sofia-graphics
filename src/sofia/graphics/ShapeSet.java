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
public class ShapeSet
    implements Set<Shape>
{
    //~ Fields ................................................................

    private ShapeParent      parent;
    private TreeSet<Shape>   treeSet;
    private ZIndexComparator zorder;


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
        treeSet = new TreeSet<Shape>(zorder);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public boolean add(Shape shape)
    {
        shape.updateTimeAddedToParent();

        boolean result = treeSet.add(shape);
        parent.onShapesAdded(Collections.singleton(shape));
        return result;
    }


    // ----------------------------------------------------------
    public boolean addAll(Collection<? extends Shape> collection)
    {
        for (Shape shape : collection)
        {
            shape.updateTimeAddedToParent();
        }

        boolean result = treeSet.addAll(collection);
        parent.onShapesAdded(collection);
        return result;
    }


    // ----------------------------------------------------------
    public void clear()
    {
        TreeSet<Shape> oldTreeSet = treeSet;
        treeSet = new TreeSet<Shape>(zorder);
        parent.onShapesRemoved(oldTreeSet);
    }


    // ----------------------------------------------------------
    public boolean contains(Object object)
    {
        return treeSet.contains(object);
    }


    // ----------------------------------------------------------
    public boolean containsAll(Collection<?> collection)
    {
        return treeSet.contains(collection);
    }


    // ----------------------------------------------------------
    public boolean isEmpty()
    {
        return treeSet.isEmpty();
    }


    // ----------------------------------------------------------
    public Iterator<Shape> iterator()
    {
        return new WrappingIterator(treeSet.iterator(), true);
    }


    // ----------------------------------------------------------
    /**
     * Access an iterator that traverses the collection from "front" (top) to
     * "back" (bottom) in terms of drawing order.
     * @return An iterator representing this traversal order.
     */
    public Iterator<Shape> frontToBackIterator()
    {
        Shape[] array = new Shape[size()];
        treeSet.toArray(array);

        return new WrappingIterator(Reversed.reversed(array).iterator(), true);
    }


    // ----------------------------------------------------------
    public boolean remove(Object object)
    {
        boolean result = treeSet.remove(object);

        if (result)
        {
            parent.onShapesRemoved(Collections.singleton((Shape) object));
        }

        return result;
    }


    // ----------------------------------------------------------
    public boolean removeAll(Collection<?> collection)
    {
        boolean modified = false;

        Iterator<Shape> it = iterator();
        TreeSet<Shape> removedShapes = new TreeSet<Shape>(zorder);
        while (it.hasNext())
        {
            Shape shape = it.next();

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
            parent.onShapesRemoved(removedShapes);
        }

        return modified;
    }


    // ----------------------------------------------------------
    public boolean retainAll(Collection<?> collection)
    {
        boolean modified = false;

        Iterator<Shape> it = iterator();
        TreeSet<Shape> removedShapes = new TreeSet<Shape>(zorder);
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
            parent.onShapesRemoved(removedShapes);
        }

        return modified;
    }


    // ----------------------------------------------------------
    public int size()
    {
        return treeSet.size();
    }


    // ----------------------------------------------------------
    public Shape[] toArray()
    {
        Shape[] result = new Shape[treeSet.size()];
        return toArray(result);
    }


    // ----------------------------------------------------------
    public <T> T[] toArray(T[] array)
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
    public void setDrawingOrder(ZIndexComparator order)
    {
        TreeSet<Shape> newSet = new TreeSet<Shape>(order);
        newSet.addAll(treeSet);
        zorder = order;
        treeSet = newSet;
    }


    // ----------------------------------------------------------
    /*package*/ void updateZIndex(Shape shape, int newZIndex)
    {
        treeSet.remove(shape);
        shape.rawSetZIndex(newZIndex);
        treeSet.add(shape);
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    private class WrappingIterator implements Iterator<Shape>
    {
        private Iterator<Shape> iterator;
        private boolean notifyParent;
        private Shape lastShape;


        // ----------------------------------------------------------
        public WrappingIterator(Iterator<Shape> iterator, boolean notifyParent)
        {
            this.iterator = iterator;
            this.notifyParent = notifyParent;
        }


        // ----------------------------------------------------------
        public boolean hasNext()
        {
            return iterator.hasNext();
        }


        // ----------------------------------------------------------
        public Shape next()
        {
            lastShape = iterator.next();
            return lastShape;
        }


        // ----------------------------------------------------------
        public void remove()
        {
            iterator.remove();

            if (lastShape != null)
            {
                if (notifyParent)
                {
                    parent.onShapesRemoved(Collections.singleton(lastShape));
                }

                lastShape = null;
            }
        }
    }
}
