package sofia.graphics;

import org.jbox2d.common.Vec2;

import sofia.graphics.internal.Box2DUtils;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

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
 * @author  Tony Allevato
 * @version 2013.03.09
 */
public class PolygonShape extends FillableShape
{
    private Polygon polygon;


    // ----------------------------------------------------------
    public PolygonShape(float... points)
    {
        this(new Polygon(points));
    }


    // ----------------------------------------------------------
    public PolygonShape(Polygon polygon)
    {
        this.polygon = polygon;
    }


    // ----------------------------------------------------------
    @Override
    public RectF getBounds()
    {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (int i = 0; i < polygon.size(); i++)
        {
            PointF pt = polygon.get(i);
            if (pt.x < minX) minX = pt.x;
            if (pt.y < minY) minY = pt.y;
            if (pt.x > maxX) maxX = pt.x;
            if (pt.y > maxY) maxY = pt.y;
        }

        PointF origin = getPosition();
        minX += origin.x;
        minY += origin.y;
        maxX += origin.x;
        maxY += origin.y;

        return new RectF(minX, minY, maxX, maxY);
    }


    // ----------------------------------------------------------
    @Override
    public void setBounds(RectF newBounds)
    {
        // TODO Auto-generated method stub

    }


    // ----------------------------------------------------------
    @Override
    public void draw(Canvas canvas)
    {
        PointF origin = getPosition();

        Path path = new Path();

        for (int i = 0; i < polygon.size(); i++)
        {
            PointF pt = polygon.get(i);
            if (i == 0)
            {
                path.moveTo(origin.x + pt.x, origin.y + pt.y);
            }
            else
            {
                path.lineTo(origin.x + pt.x, origin.y + pt.y);
            }
        }

        path.close();

        if (isFilled())
        {
            canvas.drawPath(path, getFillPaint());
        }

        Paint paint = getPaint();
        canvas.drawPath(path, paint);
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
