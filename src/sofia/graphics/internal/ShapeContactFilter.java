package sofia.graphics.internal;

import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.dynamics.Fixture;

import sofia.graphics.Shape;

//-------------------------------------------------------------------------
/**
 * TODO document
 *
 * @author  Tony Allevato
 * @version 2013.03.21
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
