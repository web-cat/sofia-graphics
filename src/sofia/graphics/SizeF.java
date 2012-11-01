package sofia.graphics;

import android.graphics.PointF;

//-------------------------------------------------------------------------
/**
 * A class used elsewhere in Sofia that holds two float coordinates
 * representing the width and height of an object. 
 * 
 * @author  Tony Allevato
 * @version 2012.09.29
 */
public class SizeF
{
	//~ Fields ................................................................

	/**
	 * The width.
	 */
    public float width;
    
    /**
     * The height.
     */
    public float height;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a {@code SizeF} with a width and height of zero.
     */
    public SizeF()
    {
    	set(0, 0);
    }


    // ----------------------------------------------------------
    /**
     * Creates a {@code SizeF} with the specified width and height.
     * 
     * @param width the width
     * @param height the height
     */
    public SizeF(float width, float height)
    {
    	set(width, height);
    }


    // ----------------------------------------------------------
    /**
     * Creates a {@code SizeF} with values copied from the specified size.
     * 
     * @param size the size
     */
    public SizeF(SizeF size)
    {
    	set(size);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the Euclidean distance from (0, 0) to the point indicated by the
     * width and height of the receiver.
     * 
     * @return the Euclidean distance from (0, 0) to (width, height) 
     */
    public float length()
    {
    	return PointF.length(width, height);
    }


    // ----------------------------------------------------------
    /**
     * Sets the size's width and height to those from the specified size.
     * 
     * @param size the size
     */
    public void set(SizeF size)
    {
    	set(size.width, size.height);
    }

    
    // ----------------------------------------------------------
    /**
     * Sets the width and height of the size.
     * 
     * @param newWidth the new width
     * @param newHeight the new height
     */
    public void set(float newWidth, float newHeight)
    {
        this.width = newWidth;
        this.height = newHeight;
    }


    // ----------------------------------------------------------
    @Override
    public boolean equals(Object other)
    {
    	if (other instanceof SizeF)
    	{
    		SizeF otherSize = (SizeF) other;
    		
    		return equals(otherSize.width, otherSize.height);
    	}
    	else
    	{
    		return false;
    	}
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiver has the specified width and
     * height.
     * 
     * @param otherWidth the width to compare the receiver with
     * @param otherHeight the height to compare the receiver with
     * @return true if the receiver has the specified width and height,
     *     otherwise false
     */
    public boolean equals(float otherWidth, float otherHeight)
    {
    	return width == otherWidth && height == otherHeight;
    }


    // ----------------------------------------------------------
    @Override
    public int hashCode()
    {
    	return 0x50F1A513 ^ (Float.valueOf(width).hashCode()
    			| Float.valueOf(height).hashCode());
    }


    // ----------------------------------------------------------
    /**
     * Gets a human-readable string representation of the size.
     * 
     * @return a human-readable string representation of the size
     */
    @Override
    public String toString()
    {
        return "(" + width + ", " + height + ")";
    }
}
