package sofia.graphics;

//-------------------------------------------------------------------------
/**
 * An access adaptor for use by the collision checking machinery to store
 * and retrieve collision-specific internal data on shapes.  These methods
 * are only intended for use by internal collision checker objects.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class ShapeAccessUtilities
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * This class contains only static utility methods and no instance
     * should ever be created.
     */
    private ShapeAccessUtilities()
    {
        // Nothing to do
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Get the internal BSP node from a shape.
     * @param shape The shape to retrieve from.
     * @return The shape's BSP node.
     */
    public static sofia.graphics.collision.ShapeNode getShapeNode(Shape shape)
    {
        return shape.getShapeNode();
    }


    // ----------------------------------------------------------
    /**
     * Store a BSP node in a shape.
     * @param shape The shape to store on.
     * @param node The node to store.
     */
    public static void setShapeNode(
        Shape shape, sofia.graphics.collision.ShapeNode node)
    {
        shape.setShapeNode(node);
    }


    // ----------------------------------------------------------
    /**
     * Tell a shape the collision checker it is stored in.
     * @param shape The shape to tell.
     * @param collisionChecker The checker watching the shape.
     */
    public static void setCollisionChecker(
        Shape shape,
        sofia.graphics.collision.CollisionChecker collisionChecker)
    {
        shape.setCollisionChecker(collisionChecker);
    }
}
