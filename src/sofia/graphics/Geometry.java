package sofia.graphics;

import android.graphics.PointF;
import android.util.FloatMath;

public class Geometry
{
	// ----------------------------------------------------------
	private Geometry()
	{
		// Do nothing.
	}
	

	// ----------------------------------------------------------
	public static float angleBetween(PointF origin, PointF extent)
	{
		return angleBetween(origin.x, origin.y, extent.x, extent.y);
	}


	// ----------------------------------------------------------
	public static float angleBetween(float x1, float y1, float x2, float y2)
	{
		float angle = (float) Math.atan2(y2 - y1, x2 - x1);
        
        return (float) (angle * 180 / Math.PI);
	}


	// ----------------------------------------------------------
	public static float distanceBetween(PointF origin, PointF extent)
	{
		return distanceBetween(origin.x, origin.y, extent.x, extent.y);
	}


	// ----------------------------------------------------------
	public static float distanceBetween(float x1, float y1, float x2, float y2)
	{
		float dx = x2 - x1;
		float dy = y2 - y1;
		return FloatMath.sqrt(dx * dx + dy * dy);
	}


	// ----------------------------------------------------------
	public static PointF polarShift(PointF origin, float angle, float distance)
	{
		float dx = (float) (distance * Math.cos(angle / 180 * Math.PI));
		float dy = (float) (distance * Math.sin(angle / 180 * Math.PI));
		
		return new PointF(origin.x + dx, origin.y + dy);
	}
}
