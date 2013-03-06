package sofia.graphics;

import java.util.Comparator;

// ----------------------------------------------------------
/**
 * A comparator for shapes that orders them by increasing z-index, or for
 * identical z-indices, orders them by increasing insertion time (i.e., newer
 * shapes are after older shapes).
 *
 * @author  Tony Allevato, Stephen Edwards
 * @version 2012.11.04
 */
public class ZIndexComparator implements Comparator<Shape>
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public int compare(Shape shape1, Shape shape2)
    {
        if (shape1.getZIndex() != shape2.getZIndex())
        {
            return shape1.getZIndex() - shape2.getZIndex();
        }
        else
        {
            return compareTimestamps(shape1, shape2);
        }
    }


    // ----------------------------------------------------------
    /**
     * Compare the insertion times of two shapes.
     *
     * @param shape1 The first shape.
     * @param shape2 The second shape.
     * @return -1 if shape1 was added to the ShapeSet before shape2,
     *         0 if they were added at the same time, or 1 if shape2
     *         was added before shape1.
     */
    protected int compareTimestamps(Shape shape1, Shape shape2)
    {
        ShapeField field1 = shape1.getShapeField();
        ShapeField field2 = shape2.getShapeField();

        Long shape1Time = (field1 != null) ?
                field1.getShapeAddedTime(shape1) : null;
        Long shape2Time = (field2 != null) ?
                field2.getShapeAddedTime(shape2) : null;

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
