package sofia.graphics;

import org.jbox2d.dynamics.World;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public abstract class AbstractJoint<
    JointType extends org.jbox2d.dynamics.joints.Joint,
    JointDefType extends org.jbox2d.dynamics.joints.JointDef>
    implements Joint
{
    //~ Fields ................................................................

    private JointType b2Joint;
    private JointDefType b2JointDef;

    private Shape firstShape;
    private Shape secondShape;
    private boolean collideConnected;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new joint with the specified shapes.
     *
     * @param firstShape  the first shape connected by this joint
     * @param secondShape the second shape connected by this joint
     */
    public AbstractJoint(Shape firstShape, Shape secondShape)
    {
        this.firstShape = firstShape;
        this.secondShape = secondShape;
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the first shape connected by this joint.
     *
     * @return the first shape connected by this joint
     */
    public Shape getFirstShape()
    {
        return firstShape;
    }


    // ----------------------------------------------------------
    /**
     * Gets the second shape connected by this joint.
     *
     * @return the second shape connected by this joint
     */
    public Shape getSecondShape()
    {
        return secondShape;
    }


    // ----------------------------------------------------------
    public boolean getCollideConnected()
    {
        return collideConnected;
    }


    // ----------------------------------------------------------
    public void setCollideConnected(boolean collide)
    {
        collideConnected = collide;
    }


    // ----------------------------------------------------------
    /**
     * Activates the joint. You must call this method after creating the joint
     * object if you want it to have any effect.
     */
    public void connect()
    {
        if (b2Joint != null)
        {
            return;
        }

        if (firstShape == null || secondShape == null)
        {
            throw new IllegalStateException("The shapes being connected by "
                    + "the joint must be non-null.");
        }

        ShapeField firstField = firstShape.getShapeField();
        ShapeField secondField = secondShape.getShapeField();

        if (firstField == null || secondField == null)
        {
            throw new IllegalStateException("The shapes being connected by "
                    + "the joint must be added to a ShapeField.");
        }
        if (firstField != secondField)
        {
            throw new IllegalStateException("The shapes being connected by "
                    + "the joint must be in the same ShapeField.");
        }
        else
        {
            createJoint();
        }
    }


    // ----------------------------------------------------------
    /**
     * Deactivates the joint, releasing the connection between the two shapes.
     */
    public void disconnect()
    {
        if (b2Joint != null)
        {
            b2Joint.m_bodyA.m_world.destroyJoint(b2Joint);
            b2Joint = null;
        }
    }


    // ----------------------------------------------------------
    public JointType getB2Joint()
    {
        return b2Joint;
    }


    // ----------------------------------------------------------
    public JointDefType getB2JointDef()
    {
        if (b2JointDef == null)
        {
            b2JointDef = createB2JointDef();
            b2JointDef.collideConnected = collideConnected;
            b2JointDef.userData = this;
        }

        return b2JointDef;
    }


    //~ Protected methods .....................................................

    // ----------------------------------------------------------
    /**
     * Subclasses must override this method to create the appropriate Box2D
     * {@code JointDef} instance that represents the specific type of joint.
     * This method should fill in all required properties of the joint,
     * including the {@code bodyA} and {@code bodyB} references.
     *
     * @return the subclass of {@code JointDef} that represents this specific
     *     type of joint
     */
    protected abstract JointDefType createB2JointDef();


    //~ Private methods .......................................................

    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    private void createJoint()
    {
        World world = firstShape.getShapeField().getB2World();
        b2Joint = (JointType) world.createJoint(getB2JointDef());
    }
}
