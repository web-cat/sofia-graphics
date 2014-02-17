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
import org.jbox2d.dynamics.joints.RevoluteJointDef;

//-------------------------------------------------------------------------
/**
 * A joint that forces two shapes to share a common anchor point, allowing only
 * the relative rotation between them to change.
 *
 * @author Tony Allevato
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
