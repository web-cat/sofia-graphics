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

import sofia.graphics.Drawing;

import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.PointF;

// -------------------------------------------------------------------------
/**
 *  The {@code TriangleShape} class is a version of the {@link PolygonShape} class that
 *  only takes three vertices.
 *
 *  @author Robert Schofield (rjschof@vt.edu)
 *  @version 2015.05.23
 */

public class TriangleShape extends PolygonShape
{
    private PointF[] vertices;

    // ----------------------------------------------------------
    /**
     * Creates a new {@code TriangleShape} object with a default size and
     * location.
     */
    public TriangleShape()
    {
        this(0, 0, 0, 0, 0, 0);
    }

    // ----------------------------------------------------------
    /**
     * Creates a new {@code TriangleShape} object with the specified locations
     * for the vertices.
     *
     * @param vert1x x coordinate of the first vertex
     * @param vert1y y coordinate of the first vertex
     * @param vert2x x coordinate of the second vertex
     * @param vert2y y coordinate of the second vertex
     * @param vert3x x coordinate of the third vertex
     * @param vert3y y coordinate of the third vertex
     */
    public TriangleShape(float vert1x, float vert1y, float vert2x,
        float vert2y, float vert3x, float vert3y)
    {
        super(vert1x, vert1y, vert2x, vert2y, vert3x, vert3y);
        vertices = new PointF[] { new PointF(vert1x, vert1y),
            new PointF(vert2x, vert2y), new PointF(vert3x, vert3y)
        };
    }

    // ----------------------------------------------------------
    /**
     * The draw method draws a {@code TriangleShape} object on the screen. If a
     * color was specified for the fill, the shape will be drawn on the screen
     * and filled in with the specified color. If a color was specified for the
     * outline of the shape, the shape will be drawn with an outline of the
     * specified color.
     *
     * @param drawing the drawing in which to draw this {@code TriangleShape}
     */
    @Override
    public void draw(Drawing drawing)
    {
        PointF origin = getPosition();
        Canvas canvas = drawing.getCanvas();

        sofia.graphics.Polygon poly = new sofia.graphics.Polygon();
        for (PointF point: polygon)
        {
            poly.add(point);
        }

        if (isFilled())
        {
            getFill().fillPolygon(drawing, getAlpha(), poly, origin);
        }

        if (!getColor().isTransparent())
        {
            Path outline = new Path();
            outline.moveTo(vertices[0].x, vertices[0].y);
            outline.lineTo(vertices[1].x, vertices[1].y);
            outline.moveTo(vertices[1].x, vertices[1].y);
            outline.lineTo(vertices[2].x, vertices[2].y);
            outline.moveTo(vertices[2].x, vertices[2].y);
            outline.lineTo(vertices[0].x, vertices[0].y);
            outline.close();
            canvas.drawPath(outline, getPaint());
        }
    }
}
