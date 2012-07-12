package sofia.graphics;

import sofia.internal.Reversed;
import java.util.Iterator;
import java.util.Collection;
import java.util.Comparator;
import java.util.WeakHashMap;
import java.util.TreeSet;

//-------------------------------------------------------------------------
/**
 * Represents a collection of {@link Shape} objects held in drawing order,
 * based on z-index and insertion time.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class ShapeCollection
    implements Collection<Shape>
{
    private static long SHAPE_ADD_COUNTER = 0;

    private ShapeParent parent;
    private TreeSet<Shape> treeSet;
    private WeakHashMap<Shape, Long> shapeAddTimes;
    private final Comparator<Shape> zorder = new ZIndexComparator();


    // ----------------------------------------------------------
    /**
     * Create a new object.
     * @param parent The shape parent associated with this shape collection.
     */
    public ShapeCollection(ShapeParent parent)
    {
        this.parent = parent;

        treeSet = new TreeSet<Shape>(zorder);
        shapeAddTimes = new WeakHashMap<Shape, Long>();
    }


    // ----------------------------------------------------------
    public boolean add(Shape shape)
    {
        shape.setParent(parent);
        shapeAddTimes.put(shape, SHAPE_ADD_COUNTER++);

        boolean result = treeSet.add(shape);
        parent.conditionallyRelayout();
        return result;
    }


    // ----------------------------------------------------------
    public boolean addAll(Collection<? extends Shape> collection)
    {
        for (Shape shape : collection)
        {
            shape.setParent(parent);
            shapeAddTimes.put(shape, SHAPE_ADD_COUNTER++);
        }

        boolean result = treeSet.addAll(collection);
        parent.conditionallyRelayout();
        return result;
    }


    // ----------------------------------------------------------
    public void clear()
    {
        for (Shape shape : this)
        {
            shape.setParent(null);
            shapeAddTimes.remove(shape);
        }

        treeSet.clear();
        parent.conditionallyRelayout();
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
        return new WrappingIterator(treeSet.iterator());
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

        return new WrappingIterator(Reversed.reversed(array).iterator());
    }


    // ----------------------------------------------------------
    public boolean remove(Object object)
    {
        boolean result = treeSet.remove(object);

        if (result)
        {
            ((Shape) object).setParent(null);
            shapeAddTimes.remove(object);
        }

        parent.conditionallyRelayout();
        return result;
    }


    // ----------------------------------------------------------
    public boolean removeAll(Collection<?> collection)
    {
        boolean modified = false;

        Iterator<Shape> it = iterator();
        while (it.hasNext())
        {
            Shape shape = it.next();

            if (collection.contains(shape))
            {
                // Since we're using the wrapping iterator here, the parent
                // will be unset properly.
                it.remove();
                modified = true;
            }
        }

        parent.conditionallyRelayout();
        return modified;
    }


    // ----------------------------------------------------------
    public boolean retainAll(Collection<?> collection)
    {
        boolean modified = false;

        Iterator<Shape> it = iterator();
        while (it.hasNext())
        {
            Shape shape = it.next();

            if (!collection.contains(shape))
            {
                it.remove();
                modified = true;
            }
        }

        parent.conditionallyRelayout();
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
        return (Shape[]) treeSet.toArray();
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


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    private class ZIndexComparator implements Comparator<Shape>
    {
        // ----------------------------------------------------------
        public int compare(Shape shape1, Shape shape2)
        {
            if (shape1.getZIndex() != shape2.getZIndex())
            {
                return shape1.getZIndex() - shape2.getZIndex();
            }
            else
            {
                Long shape1Time = shapeAddTimes.get(shape1);
                Long shape2Time = shapeAddTimes.get(shape2);

                if (shape1Time == null && shape2Time == null)
                {
                    return 0;
                }
                else if (shape1Time == null)
                {
                    return -1;
                }
                else if (shape2Time == null)
                {
                    return 1;
                }
                else
                {
                    return shape1Time.compareTo(shape2Time);
                }
            }
        }
    }


    // ----------------------------------------------------------
    private class WrappingIterator implements Iterator<Shape>
    {
        private Iterator<Shape> iterator;
        private Shape lastShape;


        // ----------------------------------------------------------
        public WrappingIterator(Iterator<Shape> iterator)
        {
            this.iterator = iterator;
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
                lastShape.setParent(null);
                shapeAddTimes.remove(lastShape);
                parent.conditionallyRelayout();

                lastShape = null;
            }
        }
    }
}
