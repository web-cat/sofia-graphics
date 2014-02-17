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

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import java.util.Iterator;

//-------------------------------------------------------------------------
/**
 * This class provides a convenient interface for iterating over a JBox2D
 * {@code Body}'s fixtures. Despite being named "iterator" this class also
 * implements {@code Iterable} so it can be used directly in a for-each loop.
 *
 * @author Tony Allevato
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
