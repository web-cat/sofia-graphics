package sofia.graphics;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public interface Joint
{
    // ----------------------------------------------------------
    public Shape getFirstShape();


    // ----------------------------------------------------------
    public Shape getSecondShape();


    // ----------------------------------------------------------
    public void connect();


    // ----------------------------------------------------------
    public org.jbox2d.dynamics.joints.Joint getB2Joint();


    // ----------------------------------------------------------
    public org.jbox2d.dynamics.joints.JointDef getB2JointDef();
}
