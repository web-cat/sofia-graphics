package sofia.graphics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import sofia.graphics.internal.Box2DUtils;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public class RevoluteJoint extends AbstractJoint<
    org.jbox2d.dynamics.joints.RevoluteJoint,
    org.jbox2d.dynamics.joints.RevoluteJointDef>
{
    //~ Fields ................................................................

    private Anchor anchor;
    private float lowerAngle;
    private float upperAngle;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new revolute joint.
     *
     * @param firstShape  the first shape to connect by the joint
     * @param secondShape the second shape to connect by the joint
     * @param anchor      the anchor on the first shape about which the second
     *                    shape will be rotated
     */
    public RevoluteJoint(Shape firstShape, Shape secondShape, Anchor anchor)
    {
        super(firstShape, secondShape);

        this.anchor = anchor;
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * TODO document
     *
     * @return
     */
    public Anchor getAnchor()
    {
        return anchor;
    }


    // ----------------------------------------------------------
    public float getLowerAngle()
    {
        return lowerAngle;
    }


    // ----------------------------------------------------------
    public void setLowerAngle(float angle)
    {
        lowerAngle = angle;
    }


    // ----------------------------------------------------------
    public float getUpperAngle()
    {
        return upperAngle;
    }


    // ----------------------------------------------------------
    public void setUpperAngle(float angle)
    {
        upperAngle = angle;
    }


    //~ Protected methods .....................................................

    // ----------------------------------------------------------
    @Override
    protected RevoluteJointDef createB2JointDef()
    {
        Vec2 anchorVec = Box2DUtils.pointFToVec2(
                anchor.getPoint(getFirstShape().getBounds()));

        RevoluteJointDef def = new RevoluteJointDef();
        def.initialize(
                getFirstShape().getB2Body(),
                getSecondShape().getB2Body(), anchorVec);

        if (lowerAngle != 0 && upperAngle != 0)
        {
            def.enableLimit = true;
            def.lowerAngle = (float) Math.toRadians(lowerAngle);
            def.upperAngle = (float) Math.toRadians(upperAngle);
        }

        return def;
    }
}
