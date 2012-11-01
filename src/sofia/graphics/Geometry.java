package sofia.graphics;

import android.graphics.PointF;
import android.util.FloatMath;

//-------------------------------------------------------------------------
/**
 * This class contains various geometry-related static helper methods.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Date$
 */
public class Geometry
{
	//~ Constructors ..........................................................

	// ----------------------------------------------------------
	/**
	 * Prevent instantiation.
	 */
	private Geometry()
	{
		// Do nothing.
	}
	

	//~ Public methods ........................................................

	// ----------------------------------------------------------
	/**
	 * Returns the angle, in degrees, between the two points origin and extent.
	 * Angles increase clockwise since y-coordinates increase in the downward
	 * direction; this is the opposite of a standard Cartesian coordinate
	 * system. The returned angle will be between -180 and 180 degrees.
	 * 
	 * @param origin the first point (the origin)
	 * @param extent the other point
	 * 
	 * @return the angle between origin and extent, in degrees clockwise,
	 *     between -180 and 180
	 */
	public static float angleBetween(PointF origin, PointF extent)
	{
		return angleBetween(origin.x, origin.y, extent.x, extent.y);
	}


	// ----------------------------------------------------------
	/**
	 * Returns the angle, in degrees, between the two points (x1, y1) and
	 * (x2, y2). Angles increase clockwise since y-coordinates increase in the
	 * downward direction; this is the opposite of a standard Cartesian
	 * coordinate system. The returned angle will be between -180 and 180
	 * degrees.
	 * 
	 * @param x1 the x-coordinate of the first point (the origin)
	 * @param y1 the y-coordinate of the first point (the origin)
	 * @param x2 the x-coordinate of the other point
	 * @param y2 the y-coordinate of the other point
	 * 
	 * @return the angle between (x1, y1) and (x2, y2), in degrees clockwise,
	 *     between -180 and 180
	 */
	public static float angleBetween(float x1, float y1, float x2, float y2)
	{
		float angle = (float) Math.atan2(y2 - y1, x2 - x1);
        
        return (float) (angle * 180 / Math.PI);
	}


	// ----------------------------------------------------------
	/**
	 * Calculates the distance between two points.
	 * 
	 * @param origin the first point
	 * @param extent the second point
	 * 
	 * @return the distance between origin and extent
	 */
	public static float distanceBetween(PointF origin, PointF extent)
	{
		return distanceBetween(origin.x, origin.y, extent.x, extent.y);
	}


	// ----------------------------------------------------------
	/**
	 * Calculates the distance between two points.
	 * 
	 * @param x1 the x-coordinate of the origin
	 * @param y1 the y-coordinate of the origin
	 * @param x2 the x-coordinate of the extent
	 * @param y2 the y-coordinate of the extent
	 * 
	 * @return the distance between (x1, y1) and (x2, y2)
	 */
	public static float distanceBetween(float x1, float y1, float x2, float y2)
	{
		float dx = x2 - x1;
		float dy = y2 - y1;

		return FloatMath.sqrt(dx * dx + dy * dy);
	}


	// ----------------------------------------------------------
	/**
	 * Calculates the translation of a point based on the specified angle and
	 * distance.
	 * 
	 * @param origin the point to be translated
	 * @param angle the angle by which to move the point, in degrees clockwise
	 * @param distance the distance to move the point
	 * 
	 * @return the translated point
	 */
	public static PointF polarShift(PointF origin, float angle, float distance)
	{
		float dx = (float) (distance * Math.cos(angle / 180 * Math.PI));
		float dy = (float) (distance * Math.sin(angle / 180 * Math.PI));
		
		return new PointF(origin.x + dx, origin.y + dy);
	}
}
