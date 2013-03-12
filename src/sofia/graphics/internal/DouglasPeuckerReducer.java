package sofia.graphics.internal;

import android.graphics.PointF;
import sofia.graphics.Geometry;
import sofia.graphics.Polygon;

//-------------------------------------------------------------------------
/**
 * <p>
 * This class implements the Douglas-Peucker algorithm, which simplifies a
 * polygon by trying to fit new lines that skip certain vertices and
 * eliminating those vertices if they are still within a tolerable distance
 * of the line.
 * </p><p>
 * This algorithm has an expected complexity of Theta(n log n), with the worst
 * case performance being O(n^2).
 * </p>
 *
 * @author  Tony Allevato
 * @version 2013.03.10
 */
public class DouglasPeuckerReducer
{
    //~ Fields ................................................................

    private Polygon polygon;
    private float tolerance;

    private Polygon simplified;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new instance of the Douglas-Peucker algorithm for the
     * specified polygon and tolerance.
     *
     * @param polygon the polygon to simplify
     * @param tolerance the tolerance
     */
    public DouglasPeuckerReducer(Polygon polygon, float tolerance)
    {
        this.polygon = polygon;
        this.tolerance = tolerance;
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the simplified polygon. The original polygon is not modified.
     *
     * @return the simplified polygon
     */
    public Polygon simplified()
    {
        if (simplified == null)
        {
            simplified = simplify(polygon);
        }

        return simplified;
    }


    //~ Private methods .......................................................

    // ----------------------------------------------------------
    /**
     * The recursive implementation of the Douglas-Peucker algorithm. The
     * algorithm starts by approximating the polygon as a line between the
     * first and last vertex. The perpendicular distances of the remaining
     * points are computed; if any are greater than the desired tolerance, the
     * one with the largest distance is added back to the polygon, and then
     * the two halves on each side of the polygon are recursively simplified
     * using the same approach.
     *
     * @param points the polygon to simplify
     * @return the simplified polygon
     */
    private Polygon simplify(Polygon points)
    {
        // Find the point with the maximum distance
        float dmax = 0;
        int index = 0;

        PointF start = points.get(0);
        PointF end = points.get(-1);

        for (int i = 1; i < points.size() - 1; i++)
        {
            float d = Geometry.perpendicularDistance(
                    points.get(i), start, end);

            if (d > dmax)
            {
                index = i;
                dmax = d;
            }
        }

        Polygon result = new Polygon();

        // If max distance is greater than epsilon, recursively simplify
        if (dmax >= tolerance)
        {
            Polygon firstHalf = simplify(points.slice(0, index + 1));
            Polygon secondHalf = simplify(points.slice(index, points.size()));

            result.addFrom(firstHalf, 0, firstHalf.size() - 1);
            result.addFrom(secondHalf, 0, secondHalf.size());
        }
        else
        {
            result.add(start);
            result.add(end);
        }

        return result;
    }
}
