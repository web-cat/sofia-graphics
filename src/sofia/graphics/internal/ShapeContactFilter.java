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

package sofia.graphics.internal;

import sofia.graphics.Shape;

import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.dynamics.Fixture;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author Tony Allevato
 */
public class ShapeContactFilter extends ContactFilter
{
    //~ Public methods ........................................................

    // ----------------------------------------------------------
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB)
    {
        Shape shapeA = (Shape) fixtureA.m_userData;
        Shape shapeB = (Shape) fixtureB.m_userData;

        if (shapeA != null && shapeB != null)
        {
            return shapeA.canCollideWith(shapeB)
                    || shapeB.canCollideWith(shapeA);
        }

        return super.shouldCollide(fixtureA, fixtureB);
    }
}
