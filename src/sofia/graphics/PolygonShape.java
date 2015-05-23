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

import sofia.graphics.internal.Box2DUtils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import org.jbox2d.common.Vec2;

//-------------------------------------------------------------------------
/**
 * <p>
 * A shape that is drawn using an arbitrary polygon.
 * </p><p>
 * Unlike the underlying JBox2D physics engine, Sofia supports polygons with
 * any number of vertices as well as those that are concave. Polygons created
 * with {@code PolygonShape} will be automatically decomposed as necessary to
 * satisfy the restrictions imposed by JBox2D. As such, a {@code PolygonShape}
 * represents one rigid body that is potentially made up of many juxtaposed
 * fixtures.
 * </p>
 *
 * @author Tony Allevato
 */
public class PolygonShape extends FillableShape
{
    //~ Fields ................................................................

    /**
     * The Polygon object that represents this PolygonShape.
     */
    protected Polygon polygon;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * <p>
     * Initializes a new polygon shape from a list of vertices. The polygon is
     * automatically closed.
     * </p><p>
     * This method processes the coordinates passed in so that the position of
     * the shape is set to the centroid of the polygon. This means that the
     * underlying {@link Polygon} object that gets created will have different
     * points than those passed in here. The reason for this is so that
     * polygon shapes can be created easily at arbitrary locations on the
     * screen, but that still behave properly when rotated about their origin.
     * If you need a polygon that is intentionally positioned in such a way
     * that its centroid is not (0, 0) in its local coordinate space, then you
     * should use the {@link PolygonShape#PolygonShape(Polygon)} constructor
     * directly.
     * </p>
     *
     * @param xyArray a list of floats that represent the x- and y-coordinates
     *     of the vertices of the polygon
     * @throws IllegalArgumentException if an odd number of floats is provided
     */
    public PolygonShape(float... points)
    {
        this(new Polygon(points));
        setPosition(polygon.centroid());
    }


    // ----------------------------------------------------------
    /**
     * Initializes a new polygon shape that will draw the specified polygon.
     *
     * @param polygon the {@link Polygon} that this shape will draw
     */
    public PolygonShape(Polygon polygon)
    {
        this.polygon = polygon;
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    @Override
    public RectF getBounds()
    {
        RectF bounds = polygon.getBounds();
        PointF origin = getPosition();

        bounds.offset(origin.x, origin.y);
        return bounds;
    }


    // ----------------------------------------------------------
    @Override
    public void setBounds(RectF newBounds)
    {
        throw new UnsupportedOperationException("Not yet implemented.");
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Drawing drawing)
    {
        PointF origin = getPosition();
        Canvas canvas = drawing.getCanvas();

        if (isFilled())
        {
            getFill().fillPolygon(drawing, getAlpha(), polygon, origin);
        }

        // TODO factor out stroke
        if (!getColor().isTransparent())
        {
            Paint paint = getPaint();
            //canvas.drawPath(path, paint);
        }
    }


    // ----------------------------------------------------------
    @Override
    protected void createFixtures()
    {
        Vec2[] vertices = new Vec2[8];

        for (Polygon part : polygon.convexDecomposition())
        {
            org.jbox2d.collision.shapes.PolygonShape b2Shape =
                    new org.jbox2d.collision.shapes.PolygonShape();

            int numVertices = part.size();
            for (int i = 0; i < numVertices; i++)
            {
                PointF pt = part.get(i);
                vertices[i] = Box2DUtils.pointFToVec2(pt);
            }

            b2Shape.set(vertices, numVertices);
            addFixtureForShape(b2Shape);
        }
    }
}
