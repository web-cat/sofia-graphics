/*
 * Copyright (C) 2011 Virginia Tech Department of Computer Science
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sofia.graphics;

import sofia.graphics.internal.BayazitDecomposer;
import sofia.graphics.internal.DouglasPeuckerReducer;
import android.graphics.PointF;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//-------------------------------------------------------------------------
/**
 * This class represents a polygon as a list of vertices. It only provides the
 * abstract geometric representation, and methods to alter or decompose them.
 * To actually draw a polygon on the screen, use the {@link PolygonShape}
 * class.
 *
 * @author Tony Allevato
 */
public class Polygon implements Iterable<PointF>
{
    //~ Fields ................................................................

    // The list of points that make up the polygon.
    private List<PointF> points;

    // Caches computed data so that it doesn't need to be recomputed unless
    // the polygon changes.
    private List<Polygon> cachedDecomposition;
    private PointF cachedCentroid;
    private RectF cachedBounds;


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
        recenter();
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
     * Adds a sequence of vertices from another polygon to the end of this one.
     *
     * @param source the polygon to copy the vertices from
     * @param start the starting index of the vertices to copy
     * @param end the ending index of the vertices to copy (this vertex is NOT
     *     included)
     */
    public void addFrom(Polygon source, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            add(source.get(i));
        }
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
                PointF curr = get(i);
                PointF next = get(i + 1);

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
        if (size() < 4)
        {
            cachedDecomposition = new ArrayList<Polygon>();
            cachedDecomposition.add(this);
        }

        if (cachedDecomposition == null)
        {
            BayazitDecomposer decomposer = new BayazitDecomposer(this);
            cachedDecomposition = decomposer.decomposition();
        }

        return cachedDecomposition;
    }


    // ----------------------------------------------------------
    /**
     * Gets the vertex at the specified index. This accessor is circular;
     * negative indices and indices greater than the size of the polygon are
     * wrapped appropriately.
     *
     * @param index the index of the vertex to get
     * @return the vertex at the specified index
     */
    public PointF get(int index)
    {
        int realIndex = wrap(index, size());
        return Geometry.clone(points.get(realIndex));
    }


    // ----------------------------------------------------------
    public RectF getBounds()
    {
        if (cachedBounds == null)
        {
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float maxY = Float.MIN_VALUE;

            for (int i = 0; i < size(); i++)
            {
                PointF pt = get(i);
                if (pt.x < minX) minX = pt.x;
                if (pt.y < minY) minY = pt.y;
                if (pt.x > maxX) maxX = pt.x;
                if (pt.y > maxY) maxY = pt.y;
            }

            cachedBounds = new RectF(minX, minY, maxX, maxY);
        }

        return new RectF(cachedBounds);
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
     * Recenters this polygon such that its centroid becomes (0, 0) in its
     * local coordinate space.
     */
    public void recenter()
    {
        PointF center = centroid();

        for (PointF point : points)
        {
            point.x -= center.x;
            point.y -= center.y;
        }
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
     * Replaces the vertex at the specified index with a different point. This
     * mutator is circular; negative indices and indices greater than the size
     * of the polygon are wrapped appropriately.
     *
     * @param index the index of the vertex to replace
     * @param point the new vertex
     * @return the previous vertex at the specified index
     */
    public PointF set(int index, PointF point)
    {
        flushCache();
        int realIndex = wrap(index, size());
        return points.set(realIndex, Geometry.clone(point));
    }


    // ----------------------------------------------------------
    /**
     * Computes a simplified version of this polygon, by trying to eliminate
     * vertices that are within a specified tolerance if the perpendicular
     * distance is measured between them and a hypothetical line between two
     * nearby vertices. The higher the tolerance, the fewer points the
     * resulting polygon will have. The original polygon is not modified.
     *
     * @param tolerance the maximum distance
     * @return the simplified polygon
     */
    public Polygon simplify(float tolerance)
    {
        DouglasPeuckerReducer reducer =
                new DouglasPeuckerReducer(this, tolerance);
        return reducer.simplified();
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
    public Polygon slice(int start, int end)
    {
        Polygon polygon = new Polygon();
        polygon.addFrom(this, start, end);
        return polygon;
    }


    // ----------------------------------------------------------
    /**
     * Flushes the polygon's cached data. Called when the polygon is modified.
     */
    private void flushCache()
    {
        cachedDecomposition = null;
        cachedCentroid = null;
        cachedBounds = null;
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
            PointF curr = get(i);
            PointF next = get(i + 1);
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
