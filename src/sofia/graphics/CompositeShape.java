package sofia.graphics;

import java.util.Collection;

import sofia.graphics.internal.GeometryUtils;
import sofia.graphics.internal.ShapeSorter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * A composite shape composed of other shapes as pieces.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class CompositeShape
    extends Shape
    implements ShapeParent, ShapeManipulating
{
    private ShapeSet<Shape> shapes = new ShapeSet<Shape>(this);
    private boolean needsLayout = true;


    // ----------------------------------------------------------
    /**
     * Create a new object.
     */
    public CompositeShape()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Create a new object.
     * @param bounds The bounds for the composite shape.
     */
    public CompositeShape(RectF bounds)
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Add a component shape to this composite.
     * @param shape The shape to add.
     */
    public void add(Shape shape)
    {
        synchronized (shapes)
        {
            shapes.add(shape);
        }
    }


    // ----------------------------------------------------------
    /**
     * Remove a component shape from this composite.
     * @param shape The shape to remove.
     */
    public void remove(Shape shape)
    {
        synchronized (shapes)
        {
            shapes.remove(shape);
        }
    }


    // ----------------------------------------------------------
    /**
     * Clears all of the shapes from this composite.
     */
    public void clear()
    {
        synchronized (shapes)
        {
            shapes.clear();
        }
    }


    // ----------------------------------------------------------
    public void onShapesAdded(Iterable<? extends Shape> addedShapes)
    {
        // TODO
    }


    // ----------------------------------------------------------
    public void onShapesRemoved(Iterable<? extends Shape> removedShapes)
    {
        // TODO
    }


    // ----------------------------------------------------------
    public Collection<Shape> getShapes()
    {
        return shapes;
    }


    // ----------------------------------------------------------
    public void conditionallyRepaint()
    {
        super.conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public void repaint()
    {
        // TODO Auto-generated method stub
    }


    // ----------------------------------------------------------
    public void conditionallyRelayout()
    {
        super.conditionallyRepaint();
    }


    // ----------------------------------------------------------
    public void relayout()
    {
        if (needsLayout)
        {
            synchronized (shapes)
            {
                ShapeSorter sorter = new ShapeSorter(shapes);

                for (Shape shape : sorter.sorted())
                {
                    if (shape != this)
                    {
                        RectF bounds = shape.getBounds();

                        //if (!GeometryUtils.isGeometryResolved(bounds))
                        {
                            GeometryUtils.resolveGeometry(bounds, shape);
                            shape.onBoundsResolved();
                        }
                    }
                }
            }
        }
    }


    // ----------------------------------------------------------
    public void setBounds(RectF bounds)
    {
        super.setBounds(bounds);

        needsLayout = true;
        relayout();
    }


    // ----------------------------------------------------------
    public boolean contains(float x, float y)
    {
        for (Shape shape : shapes)
        {
            if (shape.contains(x, y))
            {
                return true;
            }
        }

        return false;
    }


    // ----------------------------------------------------------
    public void onZIndexChanged(Shape shape)
    {
        // TODO relative z-index
        remove(shape);
        add(shape);
    }


    // ----------------------------------------------------------
    public void onPositionChanged(Shape shape)
    {
        // TODO: check this!
        getParentView().onPositionChanged(this);
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Canvas canvas)
    {
        synchronized (shapes)
        {
            for (Shape shape : shapes)
            {
                if (shape.isVisible() && shape.getBounds() != null)
                {
                    Matrix xform = shape.getTransform();

                    if (xform != null)
                    {
                        canvas.save();
                        canvas.concat(xform);
                    }

                    shape.draw(canvas);

                    if (xform != null)
                    {
                        canvas.restore();
                    }
                }
            }
        }
    }


    // ----------------------------------------------------------
    @Override
    public void onBoundsResolved()
    {
        relayout();
    }


    // ----------------------------------------------------------
    public boolean isInFrontOf(Shape left, Shape right)
    {
        return getShapeParent().isInFrontOf(left, right);
    }


    // ----------------------------------------------------------
    @Override
    protected void createFixtures()
    {
        // TODO Auto-generated method stub
    }
}
