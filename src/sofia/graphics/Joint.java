package sofia.graphics;

//-------------------------------------------------------------------------
/**
 * <p>
 * An interface that represents joints between shapes. This allows users to
 * polymorphically refer to joints without dealing with the generic parameters
 * used by {@link AbstractJoint}.
 * </p><p>
 * Essentially, this interface only provides capabilities to retrieve the
 * shapes used by the joint, connect/disconnect them, and retrieve the
 * underlying JBox2D joint objects (for advanced usage). Any more specific
 * capabilities will require downcasting to the concrete joint type.
 * </p>
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public interface Joint
{
    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the first shape connected by this joint.
     *
     * @return the first shape connected by this joint
     */
    public Shape getFirstShape();


    // ----------------------------------------------------------
    /**
     * Gets the second shape connected by this joint.
     *
     * @return the second shape connected by this joint
     */
    public Shape getSecondShape();


    // ----------------------------------------------------------
    /**
     * Activates the joint. You must call this method after creating the joint
     * object if you want it to have any effect.
     */
    public void connect();


    // ----------------------------------------------------------
    /**
     * Deactivates the joint, releasing the connection between the two shapes.
     */
    public void disconnect();


    // ----------------------------------------------------------
    /**
     * Gets the underlying JBox2D joint object. For advanced usage only.
     *
     * @return the underlying JBox2D joint object
     */
    public org.jbox2d.dynamics.joints.Joint getB2Joint();


    // ----------------------------------------------------------
    /**
     * Gets the underlying JBox2D joint definition object. For advanced usage
     * only.
     *
     * @return the underlying JBox2D joint definition object
     */
    public org.jbox2d.dynamics.joints.JointDef getB2JointDef();
}
