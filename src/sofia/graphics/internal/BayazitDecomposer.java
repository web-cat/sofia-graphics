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

package sofia.graphics.internal;

import sofia.graphics.Geometry;
import sofia.graphics.Polygon;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

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
 *
 * @author Tony Allevato
 */
public class BayazitDecomposer
{
    //~ Fields ............................................................

    // JBox2D has a limit of 8 vertices per polygon, so we have to make sure
    // that our decompositions are no larger than that.
    private final static int MAX_VERTICES = 8;

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
                    if (left(poly.get(i - 1), poly.get(i), poly.get(j))
                        && rightOn(poly.get(i - 1), poly.get(i),
                                poly.get(j - 1)))
                    {
                        // find the point of intersection...
                        p = Geometry.intersection(poly.get(i - 1), poly.get(i),
                                poly.get(j), poly.get(j - 1));

                        // make sure it's inside the polygon...
                        if (right(poly.get(i + 1), poly.get(i), p))
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

                    if (left(poly.get(i + 1), poly.get(i), poly.get(j + 1))
                        && rightOn(poly.get(i + 1), poly.get(i), poly.get(j)))
                    {
                        p = Geometry.intersection(poly.get(i + 1), poly.get(i),
                                poly.get(j), poly.get(j + 1));

                        if (left(poly.get(i - 1), poly.get(i), p))
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
                        if (leftOn(poly.get(i - 1), poly.get(i), poly.get(j))
                            && rightOn(poly.get(i + 1), poly.get(i),
                                    poly.get(j)))
                        {
                            d = sqdist(poly.get(i), poly.get(j));

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
                    newPoly.add(poly.get(vertex));
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
        return Geometry.isPointToLeft(c, a, b);
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
        return Geometry.isPointToLeftOrOn(c, a, b);
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
        return Geometry.isPointToRight(c, a, b);
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
        return Geometry.isPointToRightOrOn(c, a, b);
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
        return right(poly.get(i - 1), poly.get(i), poly.get(i + 1));
    }
}
