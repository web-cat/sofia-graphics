package sofia.graphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.PointF;

//-------------------------------------------------------------------------
/**
 * This class represents a polygon as a list of vertices. It only provides the
 * abstract geometric representation, and methods to alter or decompose them.
 * To actually draw a polygon on the screen, use the {@link PolygonShape}
 * class.
 *
 * @author  Tony Allevato
 * @version 2013.03.09
 */
public class Polygon implements Iterable<PointF>
{
    //~ Fields ................................................................

    // JBox2D has a limit of 8 vertices per polygon, so we have to make sure
    // that our decompositions are no larger than that.
    private final static int MAX_VERTICES = 8;

    // The list of points that make up the polygon.
    private List<PointF> points;

    // Caches computed data so that it doesn't need to be recomputed unless
    // the polygon changes.
    private List<Polygon> cachedDecomposition;
    private PointF cachedCentroid;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new, empty polygon.
     */
    public Polygon()
    {
        points = new ArrayList<PointF>();
    }


    // ----------------------------------------------------------
    /**
     * Initializes a new polygon from a list of vertices. The polygon is
     * automatically closed.
     *
     * @param xyArray a list of floats that represent the x- and y-coordinates
     *     of the vertices of the polygon
     * @throws IllegalArgumentException if an odd number of floats is provided
     */
    public Polygon(float... xyArray)
    {
        points = floatArrayToPointList(xyArray);
    }


    // ----------------------------------------------------------
    /**
     * Initializes a new polygon that is a copy of the specified polygon.
     *
     * @param source the polygon to copy
     */
    public Polygon(Polygon source)
    {
        // Iterating over source.points directly here instead of just source
        // eliminates an unnecessary copy that VertexIterator would make for
        // each point.

        for (PointF point : source.points)
        {
            add(point);
        }
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Adds a vertex to the end of the polygon.
     *
     * @param point the vertex to add to the polygon
     * @return always true
     */
    public boolean add(PointF point)
    {
        flushCache();
        return points.add(Geometry.clone(point));
    }


    // ----------------------------------------------------------
    /**
     * Inserts a vertex at the specified location in the polygon.
     *
     * @param index the index where the vertex should be inserted
     * @param point the vertex to insert
     */
    public void add(int index, PointF point)
    {
        flushCache();
        points.add(index, Geometry.clone(point));
    }


    // ----------------------------------------------------------
    /**
     * Gets the area of the polygon.
     *
     * @return the area of the polygon
     */
    public float area()
    {
        return Math.abs(signedArea());
    }


    // ----------------------------------------------------------
    /**
     * Gets the centroid of the polygon.
     *
     * @return the centroid of the polygon
     */
    public PointF centroid()
    {
        if (cachedCentroid == null)
        {
            PointF centroid = new PointF();

            for (int i = 0; i < size(); i++)
            {
                PointF curr = at(i);
                PointF next = at(i + 1);

                float mult = curr.x * next.y - next.x * curr.y;
                centroid.x += (curr.x + next.x) * mult;
                centroid.y += (curr.y + next.y) * mult;
            }

            centroid.x /= 6 * signedArea();
            centroid.y /= 6 * signedArea();

            cachedCentroid = centroid;
        }

        return Geometry.clone(cachedCentroid);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Computes the convex decomposition of this polygon, with the added
     * restriction that each polygon in the decomposition contains no more than
     * eight vertices.
     * </p><p>
     * The result is cached once it is computed, so multiple calls to this
     * method will not incur performance penalties unless the vertices in the
     * polygon change.
     * </p>
     *
     * @return the {@code List} of polygons that make up the receiver's convex
     *     decomposition
     */
    public List<Polygon> convexDecomposition()
    {
        if (cachedDecomposition == null)
        {
            BayazitDecomposer decomposer = new BayazitDecomposer(this);
            cachedDecomposition = decomposer.decomposition();
        }

        return cachedDecomposition;
    }


    // ----------------------------------------------------------
    /**
     * Gets the vertex at the specified index.
     *
     * @param index the index of the vertex to get
     * @return the vertex at the specified index
     */
    public PointF get(int index)
    {
        return Geometry.clone(points.get(index));
    }


    // ----------------------------------------------------------
    /**
     * Gets an iterator that can be used to iterate over the vertices in the
     * polygon.
     *
     * @return an {@link Iterator} that can be used to iterate over the
     *     veritces in the polygon
     */
    public Iterator<PointF> iterator()
    {
        return new VertexIterator(points.iterator());
    }


    // ----------------------------------------------------------
    /**
     * Removes the vertex at the specified index from the polygon.
     *
     * @param index the index of the vertex to remove
     * @return the vertex that was removed
     */
    public PointF remove(int index)
    {
        flushCache();
        return points.remove(index);
    }


    // ----------------------------------------------------------
    /**
     * Replaces the vertex at the specified index with a different point.
     *
     * @param index the index of the vertex to replace
     * @param point the new vertex
     * @return the previous vertex at the specified index
     */
    public PointF set(int index, PointF point)
    {
        flushCache();
        return points.set(index, Geometry.clone(point));
    }


    // ----------------------------------------------------------
    /**
     * Gets the number of vertices in this polygon.
     *
     * @return the number of vertices in this polygon
     */
    public int size()
    {
        return points.size();
    }


    // ----------------------------------------------------------
    /**
     * Adds a sequence of vertices from another polygon to the receiver. Used
     * by the Bayazit decomposer below.
     *
     * @param source the polygon to copy the vertices from
     * @param start the starting index of the vertices to copy
     * @param end the ending index of the vertices to copy (this vertex is NOT
     *     included)
     */
    private void addFrom(Polygon source, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            add(source.get(i));
        }
    }


    // ----------------------------------------------------------
    /**
     * An internally used accessor for vertices that wraps around safely when
     * the index is negative or greater than the size of the polygon.
     *
     * @param index the index of the vertex to get
     * @return the vertex at that index
     */
    private PointF at(int index)
    {
        return get(wrap(index, size()));
    }


    // ----------------------------------------------------------
    /**
     * Flushes the polygon's cached data. Called when the polygon is modified.
     */
    private void flushCache()
    {
        cachedDecomposition = null;
        cachedCentroid = null;
    }


    // ----------------------------------------------------------
    /**
     * Converts an array of alternating x- and y-coordinates into a
     * {@code List} of {@code PointF} objects.
     *
     * @param floats the array of float coordinates
     * @return the list of points
     * @throws IllegalArgumentException if the array has an odd number of
     *     elements
     */
    private static List<PointF> floatArrayToPointList(float[] floats)
    {
        if (floats.length % 2 != 0)
        {
            throw new IllegalArgumentException("You must provide an even "
                    + "number of floats to PolygonShape.");
        }

        List<PointF> pointList = new ArrayList<PointF>();

        for (int i = 0; i < floats.length; i += 2)
        {
            pointList.add(new PointF(floats[i], floats[i + 1]));
        }

        return pointList;
    }


    // ----------------------------------------------------------
    /**
     * Computes the signed area of the polygon. If the points are ordered
     * clockwise, the sign will be negative; otherwise, it will be positive.
     *
     * @return the signed area of the polygon
     */
    private float signedArea()
    {
        float twiceArea = 0.0f;

        for (int i = 0; i < size(); i++)
        {
            PointF curr = at(i);
            PointF next = at(i + 1);
            twiceArea += curr.x * next.y - next.x * curr.y;
        }

        return twiceArea / 2;
    }


    // ----------------------------------------------------------
    /**
     * A safe modulus function that returns the mathematically correct result
     * for negative numbers.
     *
     * @param a the dividend
     * @param b the divisor
     * @return the modulus
     */
    private static int wrap(int a, int b)
    {
        return (a < 0) ? (a % b + b) : (a % b);
    }


    // ----------------------------------------------------------
    /**
     * This helper class computes the convex decomposition of a polygon. This
     * algorithm is courtesy of Mark Bayazit, and a deeper description of the
     * process can be found here:
     *
     * http://mnbayazit.com/406/bayazit
     *
     * The decomposition has time complexity O(nr), where n is the number of
     * vertices in the polygon and r is the number of reflex vertices.
     */
    private static class BayazitDecomposer
    {
        //~ Fields ............................................................

        private List<Polygon> decomposition;
        private List<PointF> reflexVertices;
        private List<PointF> steinerPoints;


        //~ Constructors ......................................................

        // ----------------------------------------------------------
        /**
         * Initializes a new Bayazit decomposer and decomposes the specified
         * polygon.
         *
         * @param polygon the polygon to decompose
         */
        public BayazitDecomposer(Polygon polygon)
        {
            decomposition = new ArrayList<Polygon>();
            reflexVertices = new ArrayList<PointF>();
            steinerPoints = new ArrayList<PointF>();

            decompose(polygon);
        }


        //~ Public methods ....................................................

        // ----------------------------------------------------------
        /**
         * Gets the list of convex polygons that make up the decomposition.
         *
         * @return the list of polygons
         */
        public List<Polygon> decomposition()
        {
            return decomposition;
        }


        // ----------------------------------------------------------
        /**
         * Recursively decomposes the polygon.
         *
         * @param poly the polygon to decompose
         */
        private void decompose(Polygon poly)
        {
            PointF upperInt = new PointF();
            PointF lowerInt = new PointF();
            PointF p = new PointF();

            float upperDist = 0, lowerDist = 0, d = 0, closestDist = 0;
            int upperIndex = 0, lowerIndex = 0, closestIndex = 0;

            Polygon lowerPoly = new Polygon();
            Polygon upperPoly = new Polygon();

            for (int i = 0; i < poly.size(); i++)
            {
                if (isReflex(poly, i))
                {
                    reflexVertices.add(poly.get(i));

                    upperDist = lowerDist = Float.MAX_VALUE;

                    for (int j = 0; j < poly.size(); ++j)
                    {
                        // If the line intersects with an edge...
                        if (left(poly.at(i - 1), poly.at(i), poly.at(j))
                            && rightOn(poly.at(i - 1), poly.at(i),
                                    poly.at(j - 1)))
                        {
                            // find the point of intersection...
                            p = intersection(poly.at(i - 1), poly.at(i),
                                    poly.at(j), poly.at(j - 1));

                            // make sure it's inside the polygon...
                            if (right(poly.at(i + 1), poly.at(i), p))
                            {
                                d = sqdist(poly.get(i), p);

                                // and keep only the closest intersection.
                                if (d < lowerDist)
                                {
                                    lowerDist = d;
                                    lowerInt = p;
                                    lowerIndex = j;
                                }
                            }
                        }

                        if (left(poly.at(i + 1), poly.at(i), poly.at(j + 1))
                            && rightOn(poly.at(i + 1), poly.at(i), poly.at(j)))
                        {
                            p = intersection(poly.at(i + 1), poly.at(i),
                                    poly.at(j), poly.at(j + 1));

                            if (left(poly.at(i - 1), poly.at(i), p))
                            {
                                d = sqdist(poly.get(i), p);

                                if (d < upperDist)
                                {
                                    upperDist = d;
                                    upperInt = p;
                                    upperIndex = j;
                                }
                            }
                        }
                    }

                    // If there are no vertices to connect to, choose a point
                    // in the middle.
                    if (lowerIndex == (upperIndex + 1) % poly.size())
                    {
                        p.x = (lowerInt.x + upperInt.x) / 2;
                        p.y = (lowerInt.y + upperInt.y) / 2;
                        steinerPoints.add(p);

                        if (i < upperIndex)
                        {
                            lowerPoly.addFrom(poly, i, upperIndex + 1);
                            lowerPoly.add(p);
                            upperPoly.add(p);
                            if (lowerIndex != 0)
                            {
                                upperPoly.addFrom(
                                        poly, lowerIndex, poly.size());
                            }
                            upperPoly.addFrom(poly, 0, i + 1);
                        }
                        else
                        {
                            if (i != 0)
                            {
                                lowerPoly.addFrom(poly, i, poly.size());
                            }
                            lowerPoly.addFrom(poly, 0, upperIndex + 1);
                            lowerPoly.add(p);
                            upperPoly.add(p);
                            upperPoly.addFrom(poly, lowerIndex, i + 1);
                        }
                    }
                    else
                    {
                        // Connect to the closest point within the triangle.
                        if (lowerIndex > upperIndex)
                        {
                            upperIndex += poly.size();
                        }

                        closestDist = Float.MAX_VALUE;

                        for (int j = lowerIndex; j <= upperIndex; ++j)
                        {
                            if (leftOn(poly.at(i - 1), poly.at(i), poly.at(j))
                                && rightOn(poly.at(i + 1), poly.at(i),
                                        poly.at(j)))
                            {
                                d = sqdist(poly.at(i), poly.at(j));

                                if (d < closestDist)
                                {
                                    closestDist = d;
                                    closestIndex = j % poly.size();
                                }
                            }
                        }

                        if (i < closestIndex)
                        {
                            lowerPoly.addFrom(poly, i, closestIndex + 1);
                            if (closestIndex != 0)
                            {
                                upperPoly.addFrom(
                                        poly, closestIndex, poly.size());
                            }
                            upperPoly.addFrom(poly, 0, i + 1);
                        }
                        else
                        {
                            if (i != 0)
                            {
                                lowerPoly.addFrom(poly, i, poly.size());
                            }
                            lowerPoly.addFrom(poly, 0, closestIndex + 1);
                            upperPoly.addFrom(poly, closestIndex, i + 1);
                        }
                    }

                    // Recursively decompose the smallest polygon first.
                    if (lowerPoly.size() < upperPoly.size())
                    {
                        decompose(lowerPoly);
                        decompose(upperPoly);
                    }
                    else
                    {
                        decompose(upperPoly);
                        decompose(lowerPoly);
                    }

                    return;
                }
            }

            // If we reach this point, the polygon is convex. The only
            // potential further decomposition we need to do is if the polygon
            // has more than 8 vertices, which the following method handles.

            addEnsuringMaxVertices(poly);
        }


        // ----------------------------------------------------------
        /**
         * Adds the specified polygon (which we know to already be convex) to
         * the decomposed list, splitting it into multiple polygons until they
         * are all eight vertices or fewer.
         *
         * @param poly the polygon to add
         */
        private void addEnsuringMaxVertices(Polygon poly)
        {
            // If the polygon has more than 8 vertices, we partition it into
            // smaller polygons by computing the centroid and slicing it around
            // that point so that each slice has no more than 8 vertices.

            int size = poly.size();

            if (size > MAX_VERTICES)
            {
                PointF centroid = poly.centroid();
                int vertex = 0;

                while (vertex < size)
                {
                    Polygon newPoly = new Polygon();
                    newPoly.add(centroid);

                    int startVertex = vertex;

                    for (int i = 0; i < MAX_VERTICES - 1
                            && startVertex + i <= size; i++, vertex++)
                    {
                        newPoly.add(poly.at(vertex));
                    }

                    decomposition.add(newPoly);

                    if (vertex != size)
                    {
                        // Roll back one so that the slices touch each other.
                        vertex--;
                    }
                }
            }
            else
            {
                // If it has 8 vertices or less, just add it.

                decomposition.add(poly);
            }
        }


        // ----------------------------------------------------------
        private static PointF intersection(
                PointF p1, PointF p2, PointF q1, PointF q2)
        {
            PointF intersection = new PointF();
            float a1, b1, c1, a2, b2, c2, det;
            a1 = p2.y - p1.y;
            b1 = p1.x - p2.x;
            c1 = a1 * p1.x + b1 * p1.y;
            a2 = q2.y - q1.y;
            b2 = q1.x - q2.x;
            c2 = a2 * q1.x + b2 * q1.y;
            det = a1 * b2 - a2 * b1;

            if (Math.abs(det) > 1e-8)
            {
                // The lines are not parallel.
                intersection.x = (b2 * c1 - b1 * c2) / det;
                intersection.y = (a1 * c2 - a2 * c1) / det;
            }

            return intersection;
        }


        // ----------------------------------------------------------
        /**
         * Gets twice the signed area of the triangle represented by the
         * specified three points. (The result is not multiplied by 1/2 to get
         * the true area since it is only used internally and only the sign
         * matters.)
         *
         * @param a the first point on the triangle
         * @param b the second point on the triangle
         * @param c the third point on the triangle
         * @return twice the signed area of the triangle
         */
        private static float area(PointF a, PointF b, PointF c)
        {
            return (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
        }


        // ----------------------------------------------------------
        /**
         * Returns true if point C lies to the left of the line formed by
         * points A and B.
         *
         * @param a the first point on the line
         * @param b the second point on the line
         * @param c the point to test
         * @return true if point C is to the left of the line between A and B
         */
        private static boolean left(PointF a, PointF b, PointF c)
        {
            return area(a, b, c) > 0;
        }


        // ----------------------------------------------------------
        /**
         * Returns true if point C lies to the left of the line formed by
         * points A and B or if it is on that line.
         *
         * @param a the first point on the line
         * @param b the second point on the line
         * @param c the point to test
         * @return true if point C is to the left of the line between A and B
         *     or if it is on that line
         */
        private static boolean leftOn(PointF a, PointF b, PointF c)
        {
            return area(a, b, c) >= 0;
        }


        // ----------------------------------------------------------
        /**
         * Returns true if point C lies to the right of the line formed by
         * points A and B.
         *
         * @param a the first point on the line
         * @param b the second point on the line
         * @param c the point to test
         * @return true if point C is to the right of the line between A and B
         */
        private static boolean right(PointF a, PointF b, PointF c)
        {
            return area(a, b, c) < 0;
        }


        // ----------------------------------------------------------
        /**
         * Returns true if point C lies to the right of the line formed by
         * points A and B or if it is on that line.
         *
         * @param a the first point on the line
         * @param b the second point on the line
         * @param c the point to test
         * @return true if point C is to the right of the line between A and B
         *     or if it is on that line
         */
        private static boolean rightOn(PointF a, PointF b, PointF c)
        {
            return area(a, b, c) <= 0;
        }


        // ----------------------------------------------------------
        /**
         * Gets the squared distance between two points.
         *
         * @param a the first point
         * @param b the second point
         * @return the squared distance between the two points
         */
        private static float sqdist(PointF a, PointF b)
        {
            float dx = b.x - a.x;
            float dy = b.y - a.y;
            return dx * dx + dy * dy;
        }


        // ----------------------------------------------------------
        /**
         * Gets a value indicating whether the specified vertex is a reflex
         * vertex on the polygon.
         *
         * @param poly the polygon
         * @param i the index of the vertex to test
         * @return true if the vertex is a reflex vertex; false if it is not
         */
        private static boolean isReflex(Polygon poly, int i)
        {
            return right(poly.at(i - 1), poly.at(i), poly.at(i + 1));
        }
    }


    // ----------------------------------------------------------
    /**
     * A vertex iterator for the polygon that returns copies of points (so that
     * the polygon's internal representation cannot be changed by touching the
     * point's fields), and that flushes the cache when {@code remove} is
     * called.
     */
    private class VertexIterator implements Iterator<PointF>
    {
        //~ Fields ............................................................

        private Iterator<PointF> source;


        //~ Constructors ......................................................

        // ----------------------------------------------------------
        /**
         * Initializes a new vertex iterator.
         *
         * @param source the original iterator
         */
        public VertexIterator(Iterator<PointF> source)
        {
            this.source = source;
        }


        //~ Public methods ....................................................

        // ----------------------------------------------------------
        @Override
        public boolean hasNext()
        {
            return source.hasNext();
        }


        // ----------------------------------------------------------
        @Override
        public PointF next()
        {
            return Geometry.clone(source.next());
        }


        // ----------------------------------------------------------
        @Override
        public void remove()
        {
            flushCache();
            source.remove();
        }
    }
}
