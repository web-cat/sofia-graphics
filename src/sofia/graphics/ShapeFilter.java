package sofia.graphics;

import java.util.Iterator;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import sofia.graphics.internal.FixtureIterator;
import android.graphics.PointF;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * A chainable filter that lets you search for shapes in a field based on a set
 * of criteria.
 *
 * @param <ShapeType> The type of shapes that the filter will return.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.3 $, $Date: 2011/06/09 15:31:24 $
 */
public abstract class ShapeFilter<ShapeType extends Shape>
    implements Iterable<ShapeType>
{
    //~ Fields ................................................................

    // Used to represent an invalid rectangle (such as the result of
    // intersecting two completely disjoint rectangles).
    private static final RectF INVALID_RECT = new RectF(
            Float.NaN, Float.NaN, Float.NaN, Float.NaN);

    private ShapeFilter<? super ShapeType> previousFilter;
    private ShapeSet<ShapeType> filteredCandidates;
    private String descriptionOfConstraint;
    private int hashCode = 0;


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Filter object.
     *
     * @param previous The previous filter in the chain of filters.
     * @param descriptionOfConstraint A description of the constraint imposed
     * by this filter (just one step in the chain).
     */
    protected ShapeFilter(ShapeFilter<? super ShapeType> previous,
                          String descriptionOfConstraint)
    {
        previousFilter = previous;
        this.descriptionOfConstraint = descriptionOfConstraint;
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Returns the set of all shapes matched by this filter. The type returned
     * is a {@link ShapeSet}, so the shapes will be returned back to front.
     *
     * @return a {@link ShapeSet} containing all the shapes matched by this
     *         filter
     */
    public ShapeSet<ShapeType> all()
    {
        filter();
        return filteredCandidates;
    }


    // ----------------------------------------------------------
    /**
     * Gets the shape that is farthest in the background that matches this
     * filter. This is the shape that has the lowest z-index, or if multiple
     * shapes have the same z-index, the one that was added least recently to
     * its field.
     *
     * @return the bottom-most shape in the set, or null if the filter matched
     *         no shapes
     */
    public ShapeType back()
    {
        return all().back();
    }


    // ----------------------------------------------------------
    /**
     * Gets the number of shapes matched by this filter.
     *
     * @return the number of shapes matched by this filter
     */
    public int count()
    {
        return all().size();
    }


    // ----------------------------------------------------------
    /**
     * Returns true if any shapes were matched by this filter. This is the
     * opposite of {@link #isEmpty()}.
     *
     * @return true if any shapes were matched by this filter, or false if no
     *         shapes were matched
     */
    public boolean exist()
    {
        return !isEmpty();
    }


    // ----------------------------------------------------------
    /**
     * Gets the shape that is farthest in the foreground that matches this
     * filter. This is the shape that has the highest z-index, or if multiple
     * shapes have the same z-index, the one that was added most recently to
     * its field.
     *
     * @return the frontmost shape in the set, or null if the filter matched no
     *         shapes
     */
    public ShapeType front()
    {
        return all().front();
    }


    // ----------------------------------------------------------
    /**
     * Returns true if no shapes were matched by this filter. This is the
     * opposite of {@link #exist()}.
     *
     * @return true if no shapes were matched by this filter
     */
    public boolean isEmpty()
    {
        return count() == 0;
    }


    // ----------------------------------------------------------
    /**
     * Gets an iterator over all shapes matched by this filter. The iterator is
     * the same as that which would be returned by the {@link ShapeSet}
     * retrieved by calling the {@link #all()} method, so the shapes are
     * iterated in back-to-front order.
     *
     * @return the iterator over the shapes matched by this filter
     */
    public Iterator<ShapeType> iterator()
    {
        return all().iterator();
    }


    // ----------------------------------------------------------
    /**
     * Removes all of the shapes matched by this filter from the field they are
     * in, and returns the set of those shapes.
     *
     * @return a {@link ShapeSet} containing the shapes that were matched and
     *         removed
     */
    public ShapeSet<ShapeType> remove()
    {
        synchronized (b2World())
        {
            ShapeSet<ShapeType> all = all();

            for (ShapeType object : all)
            {
                object.remove();
            }

            return all;
        }
    }


    // ----------------------------------------------------------
    /**
     * Get a human-readable description of this filter.
     *
     * @return A human-readable description of this filter.
     */
    public String description()
    {
        return description(false);
    }


    // ----------------------------------------------------------
    /**
     * Get a human-readable description of this filter.
     * @return A human-readable description of this filter.
     */
    public String toString()
    {
        return description();
    }


    // ----------------------------------------------------------
    @Override
    public int hashCode()
    {
        if (hashCode == 0)
        {
            hashCode = all().hashCode();
        }

        return hashCode;
    }


    // ----------------------------------------------------------
    /**
     * Determine whether this object is equal to the another.
     * @param other The object to compare against.
     * @return True if this object is equal to the other.
     */
    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        else if (other == null)
        {
            return false;
        }
        else if (other instanceof ShapeFilter)
        {
            @SuppressWarnings("unchecked")
            ShapeFilter<ShapeType> otherFilter =
                (ShapeFilter<ShapeType>) other;

            if (description().equals(otherFilter.description()))
            {
                return true;
            }
            else
            {
                return all().equals(otherFilter.all());
            }
        }
        else
        {
            return false;
        }
    }


    //~ Filter criteria methods ...............................................

    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes that intersect the specified
     * rectangle.
     *
     * @param left   the x-coordinate of the left edge of the intersection
     *               rectangle
     * @param top    the y-coordinate of the top of the intersection rectangle
     * @param right  the x-coordinate of the right edge of the intersection
     *               rectangle
     * @param bottom the y-coordinate of the bottom of the intersection
     *               rectangle
     * @return a new filter with the given restriction
     */
    public ShapeFilter<ShapeType> intersecting(
            float left, float top, float right, float bottom)
    {
        return intersecting(new RectF(left, top, right, bottom));
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes that intersect the specified
     * rectangle.
     *
     * @param bounds the intersection rectangle
     * @return a new filter with the given restriction
     */
    public ShapeFilter<ShapeType> intersecting(final RectF bounds)
    {
        if (bounds == null)
        {
            return this;
        }
        else
        {
            final RectF sorted = new RectF(bounds);
            sorted.sort();

            return new ShapeFilter<ShapeType>(this,
                    "intersecting \"" + Geometry.toString(bounds) + '"')
            {
                @Override
                protected RectF thisQueryBounds()
                {
                    return sorted;
                }

                @Override
                protected boolean thisFilterAccepts(ShapeType shape)
                {
                    Body body = shape.getB2Body();

                    if (body != null)
                    {
                        PolygonShape boundsShape = new PolygonShape();
                        boundsShape.setAsBox(
                                sorted.width() / 2, sorted.height() / 2);
                        Transform boundsTransform = new Transform();
                        boundsTransform.set(new Vec2(
                                sorted.centerX(), sorted.centerY()), 0);

                        for (Fixture fixture : new FixtureIterator(body))
                        {
                            if (body.getWorld().getPool().getCollision().
                                    testOverlap(
                                            fixture.getShape(), 0, boundsShape,
                                            0, body.getTransform(),
                                            boundsTransform))
                            {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            };
        }
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes that are located within the
     * specified radius of a point.
     *
     * @param x the x-coordinate of the center of the intersection circle
     * @param y the y-coordinate of the center of the intersection circle
     * @param radius the radius of the intersection circle
     * @return a new filter with the given restriction
     */
    public ShapeFilter<ShapeType> locatedWithin(
            final float x, final float y, final float radius)
    {
        return new ShapeFilter<ShapeType>(this,
                "within a " + radius + " unit radius of ("
                        + x + ", " + y + ')')
        {
            @Override
            protected RectF thisQueryBounds()
            {
                return new RectF(x - radius, y - radius,
                        x + radius, y + radius);
            }

            @Override
            protected boolean thisFilterAccepts(ShapeType shape)
            {
                Body body = shape.getB2Body();

                if (body != null)
                {
                    CircleShape boundsShape = new CircleShape();
                    boundsShape.setRadius(radius);
                    Transform boundsTransform = new Transform();
                    boundsTransform.set(new Vec2(x, y), 0);

                    for (Fixture fixture : new FixtureIterator(body))
                    {
                        if (body.getWorld().getPool().getCollision().
                                testOverlap(
                                        fixture.getShape(), 0, boundsShape,
                                        0, body.getTransform(),
                                        boundsTransform))
                        {
                            return true;
                        }
                    }
                }

                return false;
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes that are located within the
     * specified radius of a point.
     *
     * @param point the point at the center of the intersection circle
     * @param radius the radius of the intersection circle
     * @return a new filter with the given restriction
     */
    public ShapeFilter<ShapeType> locatedWithin(
            final PointF point, final float radius)
    {
        return locatedWithin(point.x, point.y, radius);
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes whose classes match the
     * specified predicate (such as {@link Predicate#equalTo(Object)} or
     * {@link Predicate#extending(Class)}).
     *
     * @param predicate the predicate that specifies how the class of the shape
     *                  should be compared
     * @return a new filter with the given restriction
     */
    @SuppressWarnings("unchecked")
    public <ConstrainedShapeType extends ShapeType>
        ShapeFilter<ConstrainedShapeType> withClass(
            final Predicate<Class<? extends ConstrainedShapeType>> predicate)
    {
        if (predicate == null)
        {
            return (ShapeFilter<ConstrainedShapeType>) this;
        }
        else
        {
            return new ShapeFilter<ConstrainedShapeType>(
                    this, "with class \"" + predicate + '"')
            {
                @Override
                protected boolean thisFilterAccepts(ConstrainedShapeType shape)
                {
                    return predicate.accept(
                            (Class<? extends ConstrainedShapeType>)
                            shape.getClass());
                }
            };
        }
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes whose classes are the same as,
     * or subclasses of, the specified class. This is equivalent to calling
     * {@code withClass(Predicate.extending(theClass))}. If you need to write
     * a filter that only matches the class of the shape
     * <strong>exactly</strong> and excludes subclasses, then you must call the
     * predicate version of this method directly:
     * {@code withClass(Predicate.equalTo(theClass))}.
     *
     * @param theClass the class that shapes matching the filter will be
     *                 instances (or subclasses) of
     * @return a new filter with the given restriction
     */
    public <ConstrainedShapeType extends ShapeType>
        ShapeFilter<ConstrainedShapeType> withClass(
                Class<? extends ConstrainedShapeType> theClass)
    {
        return withClass(Predicate.extending(theClass));
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes with the specified color.
     *
     * @param color the {@link Color} of shapes to be matched
     * @return a new filter with the given restriction
     */
    public ShapeFilter<ShapeType> withColor(final Color color)
    {
        if (color == null)
        {
            return this;
        }
        else
        {
            return new ShapeFilter<ShapeType>(this,
                    "with color \"" + color + '"')
            {
                @Override
                protected boolean thisFilterAccepts(ShapeType shape)
                {
                    return color.equals(shape.getColor());
                }
            };
        }
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes with the specified sensor
     * property.
     *
     * @param sensor true if the filter should only match sensors; false if it
     *               should only match shapes that are not sensors
     * @return a new filter with the given restriction
     */
    public ShapeFilter<ShapeType> withSensor(final boolean sensor)
    {
        return new ShapeFilter<ShapeType>(this,
                "with sensor \"" + sensor + '"')
        {
            @Override
            protected boolean thisFilterAccepts(ShapeType shape)
            {
                return shape.isSensor() == sensor;
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes with the specified awake
     * state.
     *
     * @param awake  true if the filter should only match shapes that are
     *               awake; false if it should only match shapes that are
     *               asleep
     * @return a new filter with the given restriction
     */
    public ShapeFilter<ShapeType> withAwake(final boolean awake)
    {
        return new ShapeFilter<ShapeType>(this,
                "with awake \"" + awake + '"')
        {
            @Override
            protected boolean thisFilterAccepts(ShapeType shape)
            {
                return shape.isAwake() == awake;
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes that are currently in motion
     * with a linear velocity whose magnitude satisfies the specified predicate
     * (less than, greater than, etc.).
     *
     * @param predicate the predicate that specifies how the linear velocity of
     *                  the shape should be compared
     * @return a new filter with the given restriction
     * @see Predicate
     */
    public ShapeFilter<ShapeType> withLinearVelocity(
            final Predicate<Number> predicate)
    {
        if (predicate == null)
        {
            return this;
        }
        else
        {
            return new ShapeFilter<ShapeType>(this,
                    "with linear velocity \"" + predicate + '"')
            {
                @Override
                protected boolean thisFilterAccepts(ShapeType shape)
                {
                    PointF velocity = shape.getLinearVelocity();
                    float magnitude = 0;

                    if (velocity != null)
                    {
                        magnitude = Geometry.magnitude(velocity);
                    }

                    return predicate.accept(magnitude);
                }
            };
        }
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes that are currently in motion
     * with a linear velocity whose magnitude satisfies the specified predicate
     * (less than, greater than, etc.).
     *
     * @param predicate the predicate that specifies how the angular velocity
     *                  of the shape should be compared
     * @return a new filter with the given restriction
     * @see Predicate
     */
    public ShapeFilter<ShapeType> withAngularVelocity(
            final Predicate<Number> predicate)
    {
        if (predicate == null)
        {
            return this;
        }
        else
        {
            return new ShapeFilter<ShapeType>(this,
                    "with angular velocity \"" + predicate + '"')
            {
                @Override
                protected boolean thisFilterAccepts(ShapeType shape)
                {
                    return predicate.accept(shape.getAngularVelocity());
                }
            };
        }
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes that occupy the specified
     * point.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return a new filter with the given restriction
     */
    public ShapeFilter<ShapeType> locatedAt(float x, float y)
    {
        return locatedAt(new PointF(x, y));
    }


    // ----------------------------------------------------------
    /**
     * Restrict this filter to only match shapes that occupy the specified
     * point.
     *
     * @param point the point to test
     * @return a new filter with the given restriction
     */
    public ShapeFilter<ShapeType> locatedAt(final PointF point)
    {
        if (point == null)
        {
            return this;
        }
        else
        {
            return new ShapeFilter<ShapeType>(this,
                    "located at \"" + Geometry.toString(point) + '"')
            {
                @Override
                protected RectF thisQueryBounds()
                {
                    return new RectF(point.x, point.y, point.x, point.y);
                }

                @Override
                protected boolean thisFilterAccepts(ShapeType shape)
                {
                    Body body = shape.getB2Body();

                    if (body != null)
                    {
                        Vec2 vec = new Vec2(point.x, point.y);

                        for (Fixture fixture : new FixtureIterator(body))
                        {
                            if (fixture.testPoint(vec))
                            {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            };
        }
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * Get a human-readable name for the type of objects to which this filter
     * applies.  The result should be in the singular form.
     * @return A human-readable version of the FilteredObjectType.
     */
    protected String filteredObjectDescription()
    {
        return "shape";
    }


    // ----------------------------------------------------------
    /**
     * Get the plural form of {@link #filteredObjectDescription()}.
     * @return A human-readable version of the plural form of
     * FilteredObjectType.
     */
    protected String filteredObjectsDescription()
    {
        return filteredObjectDescription() + "s";
    }


    // ----------------------------------------------------------
    /**
     * Get a human-readable description of this filter.
     * @param result A StringBuilder to add the description to.
     */
    protected void addDescriptionOfConstraint(StringBuilder result)
    {
        if (previousFilter != null)
        {
            previousFilter.addDescriptionOfConstraint(result);
        }
        if (descriptionOfConstraint != null)
        {
            if (result.length() > 0)
            {
                result.append(' ');
            }
            result.append(descriptionOfConstraint);
        }
    }


    // ----------------------------------------------------------
    /**
     * Get a human-readable description of this filter.
     * @param plural Whether to generate the singular (false) or
     * plural (true) form of the description.
     * @return A human-readable description of this filter.
     */
    protected String description(boolean plural)
    {
        StringBuilder result = new StringBuilder();
        result.append(plural
            ? filteredObjectsDescription()
            : filteredObjectDescription());
        addDescriptionOfConstraint(result);
        return result.toString();
    }


    // ----------------------------------------------------------
    /**
     * Get a description of the specified object, suitable for use in
     * a diagnostic message.  The default implementation just uses
     * <code>toString()</code> on the object.
     * @param object The object to describe.
     * @return a description of the object.
     */
    protected String describe(ShapeType object)
    {
        return "" + object;
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    protected ShapeFilter<? super ShapeType> previousFilter()
    {
        return previousFilter;
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @param object TODO: describe
     * @return TODO: describe
     */
    protected abstract boolean thisFilterAccepts(ShapeType object);


    // ----------------------------------------------------------
    /**
     * Gets a bounding box that indicates which subregion of the world should
     * be queried by the filter, or null to query the entire world.
     *
     * @return the bounding box where the query should be executed
     */
    protected RectF thisQueryBounds()
    {
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Gets the JBox2D world where this filter will be executed. The default
     * behavior of this method is to ask the previous filter for its world,
     *
     * @return
     */
    protected World b2World()
    {
        if (previousFilter != null)
        {
            return previousFilter.b2World();
        }
        else
        {
            throw new IllegalStateException("Consistency error: The end of "
                    + "the filter chain did not provide a world to query.");
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets a bounding box that indicates which subregion of the world should
     * be queried by the filter, or null to query the entire world.
     *
     * @return the bounding box where the query should be executed
     */
    protected RectF queryBounds()
    {
        RectF thisBounds = thisQueryBounds();

        if (previousFilter != null)
        {
            RectF prevBounds = previousFilter.queryBounds();

            if (prevBounds == null)
            {
                return thisBounds;
            }
            else if (thisBounds == null)
            {
                return prevBounds;
            }
            else
            {
                prevBounds = new RectF(prevBounds);

                if (prevBounds.intersect(thisBounds))
                {
                    return prevBounds;
                }
                else
                {
                    return INVALID_RECT;
                }
            }
        }
        else
        {
            return thisBounds;
        }
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @param object TODO: describe
     * @return TODO: describe
     */
    protected final boolean accept(ShapeType object)
    {
        boolean result = true;

        if (previousFilter != null)
        {
            result = previousFilter.accept(object);
        }

        return result && thisFilterAccepts(object);
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     */
    protected final void filter()
    {
        if (filteredCandidates != null)
        {
            return;
        }

        filteredCandidates = new ShapeSet<ShapeType>();

        RectF queryBounds = queryBounds();
        AABB queryAABB;

        if (queryBounds == null)
        {
            queryAABB = new AABB(
                    new Vec2(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY),
                    new Vec2(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
        }
        else
        {
            queryAABB = new AABB(
                    new Vec2(queryBounds.left, queryBounds.top),
                    new Vec2(queryBounds.right, queryBounds.bottom));
        }

        if (!Float.isNaN(queryAABB.lowerBound.x))
        {
            World world = b2World();

            synchronized (world)
            {
                B2QueryCallback callback = new B2QueryCallback();
                world.queryAABB(callback, queryAABB);
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * This nested class creates the callback type to handle reporting
     * the individual fixtures within a region a query is called on.
     */
    private class B2QueryCallback implements QueryCallback
    {
        //~ Methods ...........................................................

        // ------------------------------------------------------
        /**
         * This method controls what is to occur when a fixture is found,
         * if it should be added to the list of fixtures.
         *
         * @return false terminates the query.
         */
        @SuppressWarnings("unchecked")
        public boolean reportFixture(Fixture aFixture)
        {
            ShapeType shape =
                    (ShapeType) aFixture.m_userData;

            if (!filteredCandidates.contains(shape))
            {
                if (accept(shape))
                {
                    filteredCandidates.add(shape);
                }
            }

            return true;
        }
    }
}
