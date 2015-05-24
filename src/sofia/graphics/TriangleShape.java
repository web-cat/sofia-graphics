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

// -------------------------------------------------------------------------
/**
 *  The {@code TriangleShape} class is a version of the {@link PolygonShape}
 *  class that only takes three vertices.
 *
 *  @author Robert Schofield (rjschof@vt.edu)
 *  @version 2015.05.23
 */

public class TriangleShape extends PolygonShape
{
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
    }
}
