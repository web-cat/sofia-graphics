package sofia.graphics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJointDef;

import sofia.graphics.internal.Box2DUtils;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public class DistanceJoint extends AbstractJoint<
    org.jbox2d.dynamics.joints.DistanceJoint,
    org.jbox2d.dynamics.joints.DistanceJointDef>
{
    //~ Fields ................................................................

    private Anchor firstShapeAnchor;
    private Anchor secondShapeAnchor;
    private float frequency;
    private float dampingRatio;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * TODO document
     *
     * @param firstShape
     * @param secondShape
     * @param firstShapeAnchor
     * @param secondShapeAnchor
     */
    public DistanceJoint(Shape firstShape, Shape secondShape,
            Anchor firstShapeAnchor, Anchor secondShapeAnchor)
    {
        super(firstShape, secondShape);

        this.firstShapeAnchor = firstShapeAnchor;
        this.secondShapeAnchor = secondShapeAnchor;
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * TODO document
     *
     * @return
     */
    public Anchor getFirstShapeAnchor()
    {
        return firstShapeAnchor;
    }


    // ----------------------------------------------------------
    /**
     * TODO document
     *
     * @return
     */
    public Anchor getSecondShapeAnchor()
    {
        return secondShapeAnchor;
    }


    // ----------------------------------------------------------
    /**
     * TODO document
     *
     * @return
     */
    public float getFrequency()
    {
        return frequency;
    }


    // ----------------------------------------------------------
    /**
     * TODO document
     *
     * @return
     */
    public void setFrequency(float newFrequency)
    {
        frequency = newFrequency;
    }


    // ----------------------------------------------------------
    /**
     * TODO document
     *
     * @return
     */
    public float getDampingRatio()
    {
        return dampingRatio;
    }


    // ----------------------------------------------------------
    /**
     * TODO document
     *
     * @return
     */
    public void setDampingRatio(float newDampingRatio)
    {
        dampingRatio = newDampingRatio;
    }


    //~ Protected methods .....................................................

    // ----------------------------------------------------------
    /**
     * TODO document
     *
     * @return
     */
    @Override
    protected DistanceJointDef createB2JointDef()
    {
        Vec2 firstShapeAnchorVec = Box2DUtils.pointFToVec2(
                firstShapeAnchor.getPoint(getFirstShape().getBounds()));
        Vec2 secondShapeAnchorVec = Box2DUtils.pointFToVec2(
                secondShapeAnchor.getPoint(getSecondShape().getBounds()));

        DistanceJointDef def = new DistanceJointDef();
        def.initialize(
                getFirstShape().getB2Body(), getSecondShape().getB2Body(),
                firstShapeAnchorVec, secondShapeAnchorVec);
        def.frequencyHz = frequency;
        def.dampingRatio = dampingRatio;

        return def;
    }
}
