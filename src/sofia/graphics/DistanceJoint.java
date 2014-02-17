/*
 * Copyright (C) 2011 Virginia Tech Department of Computer Science
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sofia.graphics;

import sofia.graphics.internal.Box2DUtils;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJointDef;

//-------------------------------------------------------------------------
/**
 * A joint that forces the distance between two shapes to always remain
 * constant, or within a certain amount of each other (like a spring-damper).
 *
 * @author Tony Allevato
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
