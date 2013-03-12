package sofia.graphics;

import android.graphics.PointF;
import android.graphics.RectF;

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

        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    // ----------------------------------------------------------
    /**
     * Calculates the magnitude of a vector given by its x- and y-coordinates.
     *
     * @param vector the vector
     *
     * @return the magnitude of the vector (x, y)
     */
    public static float magnitude(PointF vector)
    {
        return magnitude(vector.x, vector.y);
    }


    // ----------------------------------------------------------
    /**
     * Calculates the magnitude of a vector given by its x- and y-coordinates.
     *
     * @param x the x-coordinate of the vector
     * @param y the y-coordinate of the vector
     *
     * @return the magnitude of the vector (x, y)
     */
    public static float magnitude(float x, float y)
    {
        return (float) Math.sqrt(x * x + y * y);
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


    // ----------------------------------------------------------
    /**
     * Computes the midpoint between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the midpoint of the two points
     */
    public static PointF midpoint(PointF p1, PointF p2)
    {
        return midpoint(p1.x, p1.y, p2.x, p2.y);
    }


    // ----------------------------------------------------------
    /**
     * Computes the midpoint between two points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the midpoint of the two points
     */
    public static PointF midpoint(float x1, float y1, float x2, float y2)
    {
        return new PointF(x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2);
    }


    // ----------------------------------------------------------
    /**
     * Computes the distance between a point and a line, measured perpendicular
     * to that line.
     *
     * @param p the point whose distance from the line should be computed
     * @param q1 a point along the line
     * @param q2 another point along the line
     * @return the perpendicular distance between the point and the line
     */
    public static float perpendicularDistance(
            PointF p, PointF q1, PointF q2)
    {
        if (Math.abs(q1.x - q2.x) > 1e-8)
        {
            // The line isn't vertical, so we can use the usual formula.

            float m = (q1.y - q2.y) / (q1.x - q2.x);
            float b = q1.y - m * q1.x;
            return (float) (Math.abs(m * p.x - p.y + b)
                    / Math.sqrt(m * m + 1));
        }
        else
        {
            // The line is vertical, so just subtract the x-coordinates.

            return Math.abs(p.x - q1.x);
        }
    }


    // ----------------------------------------------------------
    /**
     * Returns true if point {@code p} lies to the left of the line formed by
     * points {@code q1} and {@code q2}.
     *
     * @param p the point to test
     * @param q1 the first point on the line
     * @param q2 the second point on the line
     * @return true if the point is to the left of the line
     */
    public static boolean isPointToLeft(PointF p, PointF q1, PointF q2)
    {
        return sArea2(q1, q2, p) > 0;
    }


    // ----------------------------------------------------------
    /**
     * Returns true if point {@code p} lies to the left of or is on the line
     * formed by points {@code q1} and {@code q2}.
     *
     * @param p the point to test
     * @param q1 the first point on the line
     * @param q2 the second point on the line
     * @return true if the point is to the left of or on the line
     */
    public static boolean isPointToLeftOrOn(PointF p, PointF q1, PointF q2)
    {
        return sArea2(q1, q2, p) >= 0;
    }


    // ----------------------------------------------------------
    /**
     * Returns true if point {@code p} lies to the right of the line formed by
     * points {@code q1} and {@code q2}.
     *
     * @param p the point to test
     * @param q1 the first point on the line
     * @param q2 the second point on the line
     * @return true if the point is to the right of the line
     */
    public static boolean isPointToRight(PointF p, PointF q1, PointF q2)
    {
        return sArea2(q1, q2, p) < 0;
    }


    // ----------------------------------------------------------
    /**
     * Returns true if point {@code p} lies to the right of or is on the line
     * formed by points {@code q1} and {@code q2}.
     *
     * @param p the point to test
     * @param q1 the first point on the line
     * @param q2 the second point on the line
     * @return true if the point is to the right of or is on the line
     */
    public static boolean isPointToRightOrOn(PointF p, PointF q1, PointF q2)
    {
        return sArea2(q1, q2, p) <= 0;
    }


    // ----------------------------------------------------------
    /**
     * Returns true if point {@code p} is on the line formed by points
     * {@code q1} and {@code q2} (in other words, all three points are
     * collinear).
     *
     * @param p the point to test
     * @param q1 the first point on the line
     * @param q2 the second point on the line
     * @return true if the point is on the line
     */
    public static boolean isPointOn(PointF p, PointF q1, PointF q2)
    {
        return sArea2(q1, q2, p) == 0;
    }


    // ----------------------------------------------------------
    /**
     * Computes the point of intersection between the lines (p1, p2) and
     * (q1, q2). If the lines are parallel, then this method returns null.
     *
     * @param p1 a point on the first line
     * @param p2 a point on the first line
     * @param q1 a point on the second line
     * @param q2 a point on the second line
     * @return the point of intersection of the two lines, or null if they are
     *     parallel
     */
    public static PointF intersection(
            PointF p1, PointF p2, PointF q1, PointF q2)
    {
        PointF intersection = new PointF();
        float a1, b1, c1, a2, b2, c2, det;
        a1 = p2.y - p1.y;
        b1 = p1.x - p2.x;
        c1 = a1 * p1.x + b1 * p1.y;
        a2 = q2.y - q1.y;
        b2 = q1.x - q2.x;
        c2 = a2 * q1.x + b2 * q1.y;
        det = a1 * b2 - a2 * b1;

        if (Math.abs(det) > 1e-8)
        {
            // The lines are not parallel.
            intersection.x = (b2 * c1 - b1 * c2) / det;
            intersection.y = (a1 * c2 - a2 * c1) / det;
            return intersection;
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * Converts a point to a string of the form "(x, y)". This method is
     * necessary because the Android {@code PointF} class does not override
     * {@code toString} in a meaningful way.
     *
     * @param point the point
     * @return a string of the form "(x, y)"
     */
    public static String toString(PointF point)
    {
        return "(" + point.x + ", " + point.y + ")";
    }


    // ----------------------------------------------------------
    /**
     * Converts a rectangle to a string of the form
     * "(left, top)-(right, bottom)". This method mainly just exists for
     * symmetry with the one for {@code PointF} above.
     *
     * @param rect the rectangle
     * @return a string of the form "(left, top)-(right, bottom)"
     */
    public static String toString(RectF rect)
    {
        return "(" + rect.left + ", " + rect.top + ")-("
                + rect.right + ", " + rect.bottom + ")";
    }


    // ----------------------------------------------------------
    /**
     * Creates a copy of the specified point.
     *
     * @param point the point to copy
     * @return a new point with the same coordinates as the original
     */
    public static PointF clone(PointF point)
    {
        // Google, why is there a copy constructor for RectF but not for
        // PointF?
        return new PointF(point.x, point.y);
    }


    //~ Private methods .......................................................

    // ----------------------------------------------------------
    /**
     * Gets twice the signed area of the triangle represented by the
     * specified three points. (The result is not multiplied by 1/2 to get
     * the true area since certain algorithms that use this only need the
     * sign, saving an extra floating point operation.)
     *
     * @param a the first point on the triangle
     * @param b the second point on the triangle
     * @param c the third point on the triangle
     * @return twice the signed area of the triangle
     */
    private static float sArea2(PointF a, PointF b, PointF c)
    {
        return (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
    }
}
