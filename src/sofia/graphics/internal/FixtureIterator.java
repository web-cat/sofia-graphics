package sofia.graphics.internal;

import java.util.Iterator;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

//-------------------------------------------------------------------------
/**
 * This class provides a convenient interface for iterating over a JBox2D
 * {@code Body}'s fixtures. Despite being named "iterator" this class also
 * implements {@code Iterable} so it can be used directly in a for-each loop.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public class FixtureIterator implements Iterator<Fixture>, Iterable<Fixture>
{
    //~ Fields ................................................................

    private Fixture fixture;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new iterator for the fixtures in the specified body.
     *
     * @param b2Body the JBox2D body to iterate over
     */
    public FixtureIterator(Body b2Body)
    {
        fixture = b2Body.m_fixtureList;
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Returns this iterator. Satisfies the {@code Iterable} interface so that
     * it can be used in a for-each loop.
     *
     * @return this iterator
     */
    public Iterator<Fixture> iterator()
    {
        return this;
    }


    // ----------------------------------------------------------
    /**
     * Returns true if there are any more fixtures to iterate over.
     *
     * @return true if there are any more fixtures to iterate over
     */
    public boolean hasNext()
    {
        return fixture != null;
    }


    // ----------------------------------------------------------
    /**
     * Gets the next fixture in the iteration.
     *
     * @return the next fixture in the iteration
     */
    public Fixture next()
    {
        Fixture result = fixture;
        fixture = fixture.m_next;
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Not supported.
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
