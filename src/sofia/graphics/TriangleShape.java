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
import sofia.graphics.FillableShape;
import sofia.graphics.Polygon;
import sofia.graphics.Drawing;

import android.graphics.PointF;
import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

//-------------------------------------------------------------------------
/**
 * The TriangleShape class is a version of the Shape object that has three
 * vertices that are bounded inside a box. The bounding box's coordinate points
 * are specified in the constructor.
 *
 * @author Robert Schofield (rjschof)
 */

public class TriangleShape extends FillableShape
{

    //~ Fields ................................................................

    private RectF bounds;

    private Polygon polygon;

    private float distanceX;
    private float distanceYtop;
    private float distanceYbottom;

    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a {@code TriangleShape} with default position and size.
     */
    public TriangleShape()
    {
        this(new RectF(0, 0, 0, 0));
    }

    // ----------------------------------------------------------
    /**
     * Creates a new {@code TriangleShape} based on four different values that
     * describe the top, left, right, and bottom coordinate points of the
     * triangle.
     *
     * @param left value for the left coordinate of the triangle
     * @param top value for the top coordinate of the triangle
     * @param right value for the right coordinate of the triangle
     * @param bottom value for the bottom coordinate of the triangle
     */
    public TriangleShape(float left, float top, float right, float bottom)
    {
        this(new RectF(left, top, right, bottom));
    }

    //-----------------------------------------------------------
    /**
     * Creates a new {@code TriangleShape} with the specified bounds.
     *
     * @param bounds the bounds of the triangle
     */
    public TriangleShape(RectF bounds)
    {
        this.bounds = bounds;
        this.distanceYtop = Math.abs(bounds.top - calculateCentroid().y);
        this.distanceYbottom = Math.abs(bounds.bottom - calculateCentroid().y);
        this.distanceX = Math.abs(bounds.left - calculateCentroid().x);

        polygon = new Polygon(
            bounds.left + Math.abs((bounds.right-bounds.left)/2), bounds.top,
            bounds.left, bounds.bottom, bounds.right, bounds.bottom);
    }

    //~ Methods ...............................................................


    //-----------------------------------------------------------
    /**
     * Creates fixtures for the JBox2D functions of the shape.
     */
    @Override
    protected void createFixtures()
    {
        PolygonShape shape = new PolygonShape();
        Vec2[] vertices = new Vec2[polygon.size()];
        for (int i = 0; i < vertices.length; i++)
        {
            PointF point = polygon.get(i);
            vertices[i] = Box2DUtils.pointFToVec2(point);
        }
        shape.set(vertices, vertices.length);
        addFixtureForShape(shape);
    }

    // ----------------------------------------------------------
    /**
     * Draws this {@code TriangleShape} on the canvas.
     *
     * @param drawing the drawing to put the shape in
     */
    @Override
    public void draw(Drawing drawing)
    {
        Canvas canvas = drawing.getCanvas();

        if (isFilled())
        {
            PointF origin = getPosition();
            getFill().fillPolygon(drawing, getAlpha(), polygon, origin);
        }
        if (!getColor().isTransparent())
        {
            // if the getColor method returns another color that is not
            // transparent, it means that there is an outline. this draws it:
            Paint paint = getPaint();
            RectF bounds = getBounds();
            Path linePath = new Path();
            if (bounds.bottom > bounds.top)
            {
                // the triangle points upward in a default coordinate system
                linePath.moveTo(bounds.left +
                    Math.abs((bounds.right - bounds.left) / 2), bounds.top);
                linePath.lineTo(bounds.left, bounds.bottom);
                linePath.moveTo(bounds.left, bounds.bottom);
                linePath.lineTo(bounds.right, bounds.bottom);
                linePath.moveTo(bounds.right, bounds.bottom);
                linePath.lineTo(bounds.left +
                    Math.abs((bounds.right - bounds.left) / 2), bounds.top);
                linePath.close();
            }
            else if (bounds.top > bounds.bottom)
            {
                // the triangle points down in a default coordinate system
                linePath.moveTo(bounds.left +
                    Math.abs((bounds.right - bounds.left) / 2), bounds.bottom);
                linePath.lineTo(bounds.left, bounds.top);
                linePath.moveTo(bounds.left, bounds.top);
                linePath.lineTo(bounds.right, bounds.top);
                linePath.moveTo(bounds.right, bounds.top);
                linePath.lineTo(bounds.left +
                    Math.abs((bounds.right - bounds.left) / 2), bounds.bottom);
                linePath.close();
            }
            canvas.drawPath(linePath, paint); // the path is drawn on the view
        }
    }

    // ----------------------------------------------------------
    /**
     * Sets the position of the triangle. The left, right, top, and bottom
     * fields are all updated because they are used to calculate different parts
     * of the {@code TriangleShape}.
     *
     * @param x the x coordinate of the position
     * @param y the y coordinate of the position
     */
    @Override
    public void setPosition(float x, float y)
    {
        bounds.left = x - distanceX;
        bounds.right = x + distanceX;

        if (bounds.bottom > bounds.top)
        {
            bounds.top = y - distanceYtop;
            bounds.bottom = y + distanceYbottom;
        }
        else
        {
            bounds.top = y + distanceYtop;
            bounds.bottom = y - distanceYbottom;
        }
        super.setPosition(x, y);
    }

    // ----------------------------------------------------------
    /**
     * Calculates the centroid of the triangle.
     *
     * @return PointF object that represents the coordinates of the centroid
     */
    public PointF calculateCentroid()
    {
        float x = (bounds.left + bounds.right +
            (bounds.left + Math.abs((bounds.left - bounds.right) / 2))) / 3;
        float y = (bounds.top + bounds.bottom + bounds.bottom) / 3;
        return new PointF(x, y);
    }

    // ----------------------------------------------------------
    /**
     * Calculates the center of the bounding box of the triangle.
     *
     * @return PointF coordinate point for the center of the bounding box of
     *     the triangle
     */
    public PointF calculateCenterOfBox()
    {
        float x = Math.abs((bounds.left + bounds.right) / 2);
        float y = Math.abs((bounds.bottom + bounds.top) / 2);
        return new PointF(x, y);
    }

    // ----------------------------------------------------------
    /**
     * Retrieves the bounding box for this shape.
     *
     * @return the bounding box for this shape
     */
    @Override
    public RectF getBounds()
    {
        // If the body has been created, update the center point using the
        // body's current position.

        PointF center = calculateCentroid();

        Body b2Body = getB2Body();
        if (b2Body != null)
        {
            Box2DUtils.vec2ToPointF(b2Body.getPosition(), center);
        }

        if (bounds.bottom > bounds.top)
        {
            return new RectF(center.x - distanceX, center.y - distanceYtop,
                center.x + distanceX, center.y + distanceYbottom);
        }
        else
        {
            return new RectF(center.x - distanceX, center.y - distanceYbottom,
                center.x + distanceX, center.y + distanceYtop);
        }
    }

    // ----------------------------------------------------------
    /**
     * Sets the bounding box for this shape.
     *
     * @param newBounds the new bounding box for this shape
     */
    @Override
    public void setBounds(RectF newBounds)
    {
        bounds.left = newBounds.left;
        bounds.right = newBounds.right;
        bounds.bottom = newBounds.bottom;
        bounds.top = newBounds.top;

        updateTransform(newBounds.centerX(), newBounds.centerY());

        this.distanceYtop = Math.abs(bounds.top - calculateCentroid().y);
        this.distanceYbottom = Math.abs(bounds.bottom - calculateCentroid().y);
        this.distanceX = Math.abs(bounds.left - calculateCentroid().x);

        polygon = new Polygon(
            bounds.left + Math.abs((bounds.right-bounds.left)/2), bounds.top,
            bounds.left, bounds.bottom, bounds.right, bounds.bottom);

        recreateFixtures();
        conditionallyRepaint();
    }
}
