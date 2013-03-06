package sofia.graphics;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import sofia.internal.Reversed;

//-------------------------------------------------------------------------
/**
 * <p>
 * Represents a collection of {@link Shape} objects held in drawing order,
 * based on z-index.
 * </p><p>
 * This class merely represents a generic, ordered collection of shapes; it
 * provides no physical simulation or collision detection. Refer to the
 * {@link ShapeField} class, which provides this added behavior.
 * </p>
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class ShapeSet<ShapeType extends Shape>
    implements Set<ShapeType>
{
    //~ Fields ................................................................

    private TreeSet<ShapeType> treeSet;
    private ZIndexComparator   drawingOrder;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Constructs a new, empty shape set, sorted by their drawing order.
     */
    public ShapeSet()
    {
        drawingOrder = new ZIndexComparator();
        treeSet = new TreeSet<ShapeType>(drawingOrder);
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Adds the specified shape to this set if it is not already present.
     *
     * @param shape the shape to be added to this set
     * @return true if this set did not already contain the specified shape
     */
    public boolean add(ShapeType shape)
    {
        return treeSet.add(shape);
    }


    // ----------------------------------------------------------
    /**
     * Adds all of the shapes in the specified collection to this set if
     * they're not already present.
     *
     * @param collection the collection containing shapes to be added to this
     *                   set
     * @return true if the set changed as a result of this operation
     */
    public boolean addAll(
            Collection<? extends ShapeType> collection)
    {
        return treeSet.addAll(collection);
    }


    // ----------------------------------------------------------
    /**
     * Gets the backmost shape in the shape set. This is the shape that has
     * the lowest z-index, or if multiple shapes have the same z-index, the
     * one that was added least recently to its field.
     *
     * @return the backmost shape in the set, or null if the set is empty
     */
    public ShapeType back()
    {
        if (treeSet.size() > 0)
        {
            return treeSet.first();
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * Removes all of the shapes from this set. The set will be empty after
     * this call returns.
     */
    public void clear()
    {
        treeSet = new TreeSet<ShapeType>(drawingOrder);
    }


    // ----------------------------------------------------------
    /**
     * Returns true if this set contains the specified shape.
     *
     * @param object the shape whose presence in this set is to be tested
     * @return true if this set contains the specified shape
     */
    public boolean contains(Object object)
    {
        return treeSet.contains(object);
    }


    // ----------------------------------------------------------
    /**
     * Returns true if this set contains all of the shapes of the specified
     * collection.
     *
     * @param collection the collection to be checked for containment in this
     *                   set
     * @return true if this set contains all of the shapes of the specified
     *         collection
     */
    public boolean containsAll(Collection<?> collection)
    {
        return treeSet.contains(collection);
    }


    // ----------------------------------------------------------
    /**
     * Gets the frontmost shape in the shape set. This is the shape that has
     * the highest z-index, or if multiple shapes have the same z-index, the
     * one that was added most recently to its field.
     *
     * @return the frontmost shape in the set, or null if the set is empty
     */
    public ShapeType front()
    {
        if (treeSet.size() > 0)
        {
            return treeSet.last();
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * Access an iterator that traverses the collection from "front" (top) to
     * "back" (bottom) in terms of drawing order.
     *
     * @return An iterator representing this traversal order.
     */
    public Iterator<ShapeType> frontToBackIterator()
    {
        @SuppressWarnings("unchecked")
        ShapeType[] array = (ShapeType[]) new Shape[size()];
        treeSet.toArray(array);

        return Reversed.reversed(array).iterator();
    }


    // ----------------------------------------------------------
    /**
     * Returns true if this set contains no shapes.
     *
     * @return true if this set contains no shapes
     */
    public boolean isEmpty()
    {
        return treeSet.isEmpty();
    }


    // ----------------------------------------------------------
    /**
     * Returns an iterator over the shapes in this set. The shapes are returned
     * in order from farthest back (lowest z-index) to farthest forward
     * (highest z-index).
     *
     * @return an iterator over the shapes in this set
     */
    public Iterator<ShapeType> iterator()
    {
        return treeSet.iterator();
    }


    // ----------------------------------------------------------
    /**
     * Removes the specified shape from this set if it is present.
     *
     * @param object the shape to be removed from this set, if present
     * @return true if this set contained the specified shape
     */
    public boolean remove(Object object)
    {
        return treeSet.remove(object);
    }


    // ----------------------------------------------------------
    /**
     * Removes from this set all of its shapes that are contained in the
     * specified collection.
     *
     * @param collection the collection containing shapes to be removed from
     *                   this set
     * @return true if this set changed as a result of the call
     */
    public boolean removeAll(Collection<?> collection)
    {
        return treeSet.removeAll(collection);
    }


    // ----------------------------------------------------------
    /**
     * Retains only the shapes in this set that are contained in the specified
     * collection. In other words, removes from this set all of its shapes that
     * are not contained in the specified collection.
     *
     * @param collection the collection containing shapes to be retained in
     *                   this set
     * @return true if this set changed as a result of the call
     */
    public boolean retainAll(Collection<?> collection)
    {
        return treeSet.retainAll(collection);
    }


    // ----------------------------------------------------------
    /**
     * Returns the number of shapes in this set (its cardinality).
     *
     * @return the number of shapes in this set (its cardinality)
     */
    public int size()
    {
        return treeSet.size();
    }


    // ----------------------------------------------------------
    /**
     * Returns an array containing all of the shapes in this set. The shapes
     * are returned in order from farthest back (lowest z-index) to farthest
     * forward (highest z-index).
     *
     * @returns an array containing all the shapes in the set
     */
    public Shape[] toArray()
    {
        Shape[] result = new Shape[treeSet.size()];
        return toArray(result);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Returns an array containing all of the shapes in this set; the runtime
     * type of the returned array is that of the specified array. If the set
     * fits in the specified array, it is returned therein. Otherwise, a new
     * array is allocated with the runtime type of the specified array and the
     * size of this set.
     * </p><p>
     * If this set fits in the specified array with room to spare (i.e., the
     * array has more elements than this set), the element in the array
     * immediately following the end of the set is set to null. (This is useful
     * in determining the length of this set only if the caller knows that this
     * set does not contain any null elements.)
     * </p><p>
     * The shapes are returned in order from farthest back (lowest z-index) to
     * farthest forward (highest z-index).
     * </p>
     *
     * @param array the array into which the shapes of this set are to be
     *              stored, if it is big enough; otherwise, a new array of the
     *              same runtime type is allocated for this purpose.
     * @returns an array containing all the shapes in the set
     */
    public <T> T[] toArray(T[] array)
    {
        return treeSet.toArray(array);
    }


    // ----------------------------------------------------------
    /**
     * Returns the hash code value for this set.
     *
     * @return the hash code value for this set
     */
    @Override
    public int hashCode()
    {
        return treeSet.hashCode();
    }


    // ----------------------------------------------------------
    /**
     * Compares the specified object with this set for equality.
     *
     * @param other the object to be compared for equality with this set
     * @return true if the specified object is equal to this set
     */
    @Override
    public boolean equals(Object other)
    {
        if (other instanceof ShapeSet)
        {
            ShapeSet<?> otherSet = (ShapeSet<?>) other;
            return treeSet.equals(otherSet.treeSet);
        }
        else
        {
            return false;
        }
    }


    // ----------------------------------------------------------
    /**
     * Returns true if the left shape is drawn in front of (later than) the
     * shape on the right.
     *
     * @param left  the shape to check
     * @param right the shape to check against
     * @return true if left is drawn above (later than) right
     */
    public boolean isInFrontOf(Shape left, Shape right)
    {
        return drawingOrder.compare(left, right) > 0;
    }


    // ----------------------------------------------------------
    /**
     * Get the shape order for this shape set.
     *
     * @return The current shape ordering, in the form of a comparator
     */
    public ZIndexComparator getDrawingOrder()
    {
        return drawingOrder;
    }


    // ----------------------------------------------------------
    /**
     * Change the shape order for this shape set.
     *
     * @param order the new ordering to use
     */
    public void setDrawingOrder(ZIndexComparator order)
    {
        TreeSet<ShapeType> newSet = new TreeSet<ShapeType>(order);
        newSet.addAll(treeSet);
        drawingOrder = order;
        treeSet = newSet;
    }


    // ----------------------------------------------------------
    /**
     * Gets the {@code TreeSet} underlying this shape set.
     *
     * @return the {@code TreeSet} underlying this shape set
     */
    protected TreeSet<ShapeType> rawSet()
    {
        return treeSet;
    }
}
