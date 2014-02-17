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
 * Represents one or more of the four edges of a view, used with collision
 * detection.
 *
 * @author Tony Allevato
 */
public class ViewEdges
{
    //~ Instance/static variables .............................................

    private boolean left;
    private boolean top;
    private boolean right;
    private boolean bottom;

    private static final int TRUE_CODE  = Boolean.TRUE.hashCode()  % 256;
    private static final int FALSE_CODE = Boolean.FALSE.hashCode() % 256;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new {@code ViewEdges} object that includes the specified
     * edges.
     *
     * @param left true to include the left edge
     * @param top true to include the top edge
     * @param right true to include the right edge
     * @param bottom true to include the bottom edge
     */
    public ViewEdges(boolean left, boolean top, boolean right, boolean bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiver includes the left edge.
     *
     * @return true if the receiver includes the left edge, otherwise false
     */
    public boolean left()
    {
        return left;
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiver includes the top edge.
     *
     * @return true if the receiver includes the top edge, otherwise false
     */
    public boolean top()
    {
        return top;
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiver includes the right edge.
     *
     * @return true if the receiver includes the right edge, otherwise false
     */
    public boolean right()
    {
        return right;
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiver includes the bottom edge.
     *
     * @return true if the receiver includes the bottom edge, otherwise false
     */
    public boolean bottom()
    {
        return bottom;
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiver includes any horizontal
     * edge; that is, the top or the bottom edge.
     *
     * @return true if the receiver includes the top or bottom edge, otherwise
     *     false
     */
    public boolean horizontal()
    {
        return top || bottom;
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiver includes any vertical edge;
     * that is, the left or the right edge.
     *
     * @return true if the receiver includes the left or right edge, otherwise
     *     false
     */
    public boolean vertical()
    {
        return left || right;
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiver includes any edge.
     *
     * @return true if the receiver includes any edge, otherwise false
     */
    public boolean any()
    {
        return left || top || right || bottom;
    }
    
    
    // ----------------------------------------------------------
    /**
     * Creates a copy of the receiver that represents the same edges as the
     * original.
     * 
     * @return a copy of the receiver
     */
    public ViewEdges clone()
    {
    	return new ViewEdges(left, top, right, bottom);
    }


    // ----------------------------------------------------------
    /**
     * Compares two {@code ViewEdges} objects and returns a value indicating
     * whether or not they represent exactly the same edges.
     * 
     * @param other the other object to compare
     * @return true if the other object represents the same edges, otherwise
     *     false
     */
    @Override
    public boolean equals(Object other)
    {
        if (other == null)
        {
            return false;
        }
        else if (!(other instanceof ViewEdges))
        {
            return false;
        }
        else
        {
            ViewEdges ve = (ViewEdges) other;
            return left   == ve.left
                && top    == ve.top
                && right  == ve.right
                && bottom == ve.bottom;
        }
    }


    // ----------------------------------------------------------
    @Override
    public int hashCode()
    {
        return ((left   ? TRUE_CODE : FALSE_CODE) << 24)
            +  ((top    ? TRUE_CODE : FALSE_CODE) << 16)
            +  ((right  ? TRUE_CODE : FALSE_CODE) << 8)
            +   (bottom ? TRUE_CODE : FALSE_CODE);
    }
    
    
    // ----------------------------------------------------------
    @Override
    public String toString()
    {
    	StringBuffer buffer = new StringBuffer();

    	buffer.append("ViewEdges(");
    	
    	if (left) buffer.append("left ");
    	if (top) buffer.append("top ");
    	if (right) buffer.append("right ");
    	if (bottom) buffer.append("bottom ");

    	buffer.append(")");

    	return buffer.toString();
    }
}
