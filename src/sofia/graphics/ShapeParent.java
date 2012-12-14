package sofia.graphics;

import java.util.Collection;

import android.graphics.RectF;

// -------------------------------------------------------------------------
/**
 * An interface implemented by classes that can contain a list of shapes, such
 * as a {@link ShapeView}.
 *
 * @author  Tony Allevato
 * @version 2011.10.16
 */
public interface ShapeParent
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    void add(Shape shape);


    // ----------------------------------------------------------
    void remove(Shape shape);


    // ----------------------------------------------------------
    void onShapesAdded(Iterable<? extends Shape> shapes);


    // ----------------------------------------------------------
    void onShapesRemoved(Iterable<? extends Shape> shapes);


    // ----------------------------------------------------------
    /**
     * Gets the collection of shapes owned by the receiver.
     *
     * @return a {@link Collection} of {@link Shape} objects owned by the
     *     receiver
     */
    Collection<Shape> getShapes();


    // ----------------------------------------------------------
    /**
     * Gets the parent of this {@code ShapeParent}.
     *
     * @return the {@code ShapeParent} that contains the receiver
     */
    ShapeParent getShapeParent();


    // ----------------------------------------------------------
    /**
     * Notifies the receiver that the shapes it owns need to be repainted.
     */
    void conditionallyRepaint();


    // ----------------------------------------------------------
    /**
     * Notifies the receiver that the shapes it owns need to be laid out again
     * (if the bounds of one of the shapes have changed).
     */
    void conditionallyRelayout();


    // ----------------------------------------------------------
    /**
     * Notifies the receiver that the shapes it owns need to be repainted.
     */
    void repaint();


    // ----------------------------------------------------------
    /**
     * Notifies the receiver that the shapes it owns need to be laid out again
     * (if the bounds of one of the shapes have changed).
     */
    void relayout();


    // ----------------------------------------------------------
    /**
     * Gets the bounds of receiver where shapes can be contained.
     *
     * @return the bounds of the receiver
     */
    RectF getBounds();


    // ----------------------------------------------------------
    /**
     * Returns true if the left shape is drawn in front of (later than) the
     * shape on the right.
     * @param left The shape to check.
     * @param right The shape to check against.
     * @return True if left is drawn in front of (later than) right.
     */
    boolean isInFrontOf(Shape left, Shape right);


    // ----------------------------------------------------------
    /**
     * Called by a shape inside the receiver when its z-index has changed, so
     * that the receiver can rearrange its shape collection accordingly.
     *
     * @param shape the shape whose z-index was changed
     */
    void updateZIndex(Shape shape, int newZIndex);


    // ----------------------------------------------------------
    /**
     * Called by a shape inside the receiver when its position has changed, so
     * that the receiver can rearrange its shape collection accordingly.
     *
     * @param shape the shape whose position was changed
     */
    void onPositionChanged(Shape shape);
}
