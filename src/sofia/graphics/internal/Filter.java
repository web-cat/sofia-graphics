package sofia.graphics.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sofia.graphics.Shape;
import sofia.graphics.ShapeSet;

//-------------------------------------------------------------------------
/**
 *  TODO: document.
 *
 *  @param <ConcreteFilterType> A parameter indicating the concrete subclass
 *  of this class, for use in providing more specialized return types on
 *  some methods.
 *  @param <FilteredObjectType> A parameter indicating the kind of object
 *  this filter accepts.
 *
 *  @author  Stephen Edwards
 *  @author  Last changed by $Author: stedwar2 $
 *  @version $Revision: 1.3 $, $Date: 2011/06/09 15:31:24 $
 */
public abstract class Filter<
    ConcreteFilterType,
    FilteredObjectType extends Shape>
    implements Iterable<ConcreteFilterType>
{
    //~ Fields ................................................................

    private Filter<ConcreteFilterType, FilteredObjectType> previousFilter;
    private ShapeSet<FilteredObjectType> filteredCandidates;
    private String descriptionOfConstraint;
    private int hashCode = 0;


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Filter object.
     * @param previous The previous filter in the chain of filters.
     * @param descriptionOfConstraint A description of the constraint imposed
     * by this filter (just one step in the chain).
     */
    @SuppressWarnings("unchecked")
    protected Filter(Filter<?, ?> previous, String descriptionOfConstraint)
    {
        previousFilter =
            (Filter<ConcreteFilterType, FilteredObjectType>)previous;
        this.descriptionOfConstraint = descriptionOfConstraint;
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    public int count()
    {
        filter();
        return filteredCandidates.size();
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    public boolean exists()
    {
        return guaranteesMultipleMatches() || count() > 0;
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    public boolean isUnique()
    {
        return !guaranteesMultipleMatches() && count() == 1;
    }


    // ----------------------------------------------------------
    /**
     * Get the unencapsulated (raw) object represented by this filter.
     * This operation requires there to exist a unique match for the
     * filter or a {@link ReflectionError} will be thrown.  Use
     * {@link #allMatches()} if there is not a unique match.
     * @return The raw (unencapsulated} object represented by this filter.
     */
    public FilteredObjectType raw()
    {
        return uniqueMatch();
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    public ShapeSet<FilteredObjectType> allMatches()
    {
        filter();
        return filteredCandidates;
    }


    // ----------------------------------------------------------
    /**
     * Get an iterator over all instances this filter matches, where the
     * items traversed by the iterator are ConcreteFilterType instances.
     * @return The iterator
     */
    public Iterator<ConcreteFilterType> iterator()
    {
        return new Iterator<ConcreteFilterType>()
        {
            private Iterator<FilteredObjectType> matchIterator =
                    allMatches().iterator();


            // ----------------------------------------------------------
            public boolean hasNext()
            {
                return matchIterator.hasNext();
            }


            // ----------------------------------------------------------
            public ConcreteFilterType next()
            {
                return createFreshFilter(matchIterator.next());
            }


            // ----------------------------------------------------------
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

        };
    }


    // ----------------------------------------------------------
    /**
     * Get a human-readable description of this filter.
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
        if (isUnique())
        {
            return raw().toString();
        }
        else
        {
            return description();
        }
    }


    // ----------------------------------------------------------
    @Override
    public int hashCode()
    {
        if (hashCode == 0)
        {
            hashCode =
                (new HashSet<FilteredObjectType>(allMatches())).hashCode();
        }
        return hashCode;
    }


    // ----------------------------------------------------------
    /**
     * Determine whether this object is equal to the another.
     * @param other The object to compare against.
     * @return True if this object is equal to the other.
     */
    public boolean equals(final Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (other instanceof Filter)
        {
            @SuppressWarnings("unchecked")
            Filter<ConcreteFilterType, FilteredObjectType> otherType =
                (Filter<ConcreteFilterType, FilteredObjectType>)other;
            if (description().equals(otherType.description()))
            {
                return true;
            }
            Set<FilteredObjectType> left =
                new HashSet<FilteredObjectType>(allMatches());
            Set<FilteredObjectType> right =
                new HashSet<FilteredObjectType>(otherType.allMatches());
            return left.equals(right);
        }
        else
        {
            return quantify.evaluate(new Predicate<FilteredObjectType>()
            {
                public boolean isSatisfiedBy(FilteredObjectType object)
                {
                    return other.equals(object);
                }
            });
        }
    }


    // ----------------------------------------------------------
    /**
     * Change the quantification behavior of a filter so that boolean
     * predicates are evaluated over all matches for the filter and the
     * result is "or"ed together.
     * @param filter The filter to alter.
     * @param <ConcreteFilterType> This type is deduced from the filter.
     * @param <FilteredObjectType> This type is deduced from the filter.
     * @return A new filter that behaves like the different one, but uses
     * the desired quantification strategy.
     */
    @SuppressWarnings("unchecked")
    public static <ConcreteFilterType, FilteredObjectType extends Shape>
        ConcreteFilterType atLeastOne(ConcreteFilterType filter)
    {
        ConcreteFilterType result =
            ((Filter<ConcreteFilterType, FilteredObjectType>)filter)
            .createFreshFilter(filter, null);
        Filter<ConcreteFilterType, FilteredObjectType> f =
            (Filter<ConcreteFilterType, FilteredObjectType>)result;
        f.quantify = f.AT_LEAST_ONE;
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Change the quantification behavior of a filter so that boolean
     * predicates are evaluated over all matches for the filter and the
     * result is "xor"ed together.
     * @param filter The filter to alter.
     * @param <ConcreteFilterType> This type is deduced from the filter.
     * @param <FilteredObjectType> This type is deduced from the filter.
     * @return A new filter that behaves like the different one, but uses
     * the desired quantification strategy.
     */
    @SuppressWarnings("unchecked")
    public static <ConcreteFilterType, FilteredObjectType extends Shape>
        ConcreteFilterType onlyOne(ConcreteFilterType filter)
    {
        ConcreteFilterType result =
            ((Filter<ConcreteFilterType, FilteredObjectType>)filter)
            .createFreshFilter(filter, null);
        Filter<ConcreteFilterType, FilteredObjectType> f =
            (Filter<ConcreteFilterType, FilteredObjectType>)result;
        f.quantify = f.ONLY_ONE;
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Change the quantification behavior of a filter so that boolean
     * predicates are evaluated over all matches for the filter and the
     * result is "and"ed together.
     * @param filter The filter to alter.
     * @param <ConcreteFilterType> This type is deduced from the filter.
     * @param <FilteredObjectType> This type is deduced from the filter.
     * @return A new filter that behaves like the different one, but uses
     * the desired quantification strategy.
     */
    @SuppressWarnings("unchecked")
    public static <ConcreteFilterType, FilteredObjectType extends Shape>
        ConcreteFilterType every(ConcreteFilterType filter)
    {
        ConcreteFilterType result =
            ((Filter<ConcreteFilterType, FilteredObjectType>)filter)
            .createFreshFilter(filter, null);
        Filter<ConcreteFilterType, FilteredObjectType> f =
            (Filter<ConcreteFilterType, FilteredObjectType>)result;
        f.quantify = f.EVERY;
        return result;
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * Create a new instance of the same class as "this", initialized
     * with these values.
     * @param previous The previous filter in the chain of filters.
     * @param descriptionOfThisStage A description of this stage in the
     * filter chain.
     * @return The newly created Filter instance.
     */
    protected abstract ConcreteFilterType
        createFreshFilter(
            ConcreteFilterType previous, String descriptionOfThisStage);


    // ----------------------------------------------------------
    /**
     * Create a new instance of the same class as "this", representing a
     * single object.
     * @param object The single value of the new filter.
     * @return The newly created Filter instance.
     */
    protected abstract ConcreteFilterType
        createFreshFilter(final FilteredObjectType object);


    // ----------------------------------------------------------
    /**
     * Get a human-readable name for the type of objects to which this filter
     * applies.  The result should be in the singular form.
     * @return A human-readable version of the FilteredObjectType.
     */
    protected abstract String filteredObjectDescription();


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
    protected String describe(FilteredObjectType object)
    {
        return "" + object;
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    protected boolean guaranteesMultipleMatches()
    {
        if (previousFilter() != null)
        {
            return previousFilter().guaranteesMultipleMatches();
        }
        else
        {
            return false;
        }
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    protected FilteredObjectType firstMatch()
    {
        filter();
        if (!exists())
        {
            //throw new ReflectionError(
            //    "No " + description(false) + " was found.");
            return null;
        }
        return filteredCandidates.iterator().next();
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    protected FilteredObjectType uniqueMatch()
    {
        firstMatch();
        if (filteredCandidates.size() > 1)
        {
            StringBuilder msg = new StringBuilder();
            msg.append(filteredCandidates.size());
            msg.append(' ');
            msg.append(description(true));
            msg.append(" were found: [");
            FilteredObjectType first = filteredCandidates.iterator().next();
            for (FilteredObjectType object : filteredCandidates)
            {
                if (object != first)
                {
                    msg.append(", ");
                }
                msg.append(describe(object));
            }
            msg.append("].");
            //throw new ReflectionError(msg.toString());
            return null;
        }
        return filteredCandidates.iterator().next();
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    protected Filter<ConcreteFilterType, FilteredObjectType> previousFilter()
    {
        return previousFilter;
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    protected ShapeSet<FilteredObjectType> candidatesFromThisFilter()
    {
        return null;
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @return TODO: describe
     */
    protected final ShapeSet<FilteredObjectType> allCandidates()
    {
        ShapeSet<FilteredObjectType> result = candidatesFromThisFilter();
        if (result == null)
        {
            if (previousFilter != null)
            {
                result = previousFilter.allCandidates();
            }
            else
            {
                // TODO: need a good ReflectionError message
                assert false;
            }
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @param object TODO: describe
     * @return TODO: describe
     */
    protected abstract boolean thisFilterAccepts(FilteredObjectType object);


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @param object TODO: describe
     * @return TODO: describe
     */
    protected final boolean accept(FilteredObjectType object)
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
    protected void flush()
    {
        filteredCandidates = null;
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     */
    protected final void filter()
    {
        if (filteredCandidates == null)
        {
            filteredCandidates = filter(allCandidates());
        }
    }


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @param candidates TODO: describe
     * @return TODO: describe
     */
    protected ShapeSet<FilteredObjectType> filter(
        ShapeSet<FilteredObjectType> candidates)
    {
        ShapeSet<FilteredObjectType> result =
                new ShapeSet<FilteredObjectType>();
        if (candidates != null)
        {
            for (FilteredObjectType object : candidates)
            {
                if (accept(object))
                {
                    result.add(object);
                }
            }
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * This interface represents a "strategy" for evaluating a single
     * object (eventually of the FilteredObjectType) for some property
     * and returning true or false.  It serves as the argument type for
     * {@link Filter.QuantifierBehavior#evaluate(Predicate)}.
     * @param <ObjectType> The type of object to which this predicate can
     *                     be applied.
     */
    protected static interface Predicate<ObjectType>
    {
        // ----------------------------------------------------------
        /**
         * Perform a test on an object, returning true or false.
         * @param object The object to test.
         * @return True if the object satisfies this predicate.
         */
        public boolean isSatisfiedBy(ObjectType object);
    }


    // ----------------------------------------------------------
    /**
     * I wanted to use an enum for this, but the enum keyword creates
     * constants that are static, while I need non-static instances
     * so they can access object state.
     *
     * The instances of this type represent the quantifier behaviors
     * supported by this class.  Each constant represents a parameterized
     * strategy for evaluating a predicate.
     */
    protected abstract class QuantifierBehavior
    {
        // ----------------------------------------------------------
        /**
         * Evaluate a boolean predicate over a set of the matches from this
         * filter, as determined by the filter's current mode.
         * @param predicate The predicate to evaluate
         * @return The result of applying the predicate to this filter's
         * matches, as determined by this filter's quantifier behavior.
         */
        public abstract boolean evaluate(
            Predicate<FilteredObjectType> predicate);
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    /**
     * This constant represents an evaluation strategy that
     * "or"s together the results of the predicate across all matches.
     */
    private final QuantifierBehavior AT_LEAST_ONE = new QuantifierBehavior()
    {
        public boolean evaluate(
            Predicate<FilteredObjectType> predicate)
        {
            for (FilteredObjectType object : allMatches())
            {
                if (predicate.isSatisfiedBy(object))
                {
                    return true;
                }
            }
            return false;
        }
    };


    // ----------------------------------------------------------
    /**
     * This constant represents an evaluation strategy that
     * "xor"s together the results of the predicate across all matches.
     */
    private final QuantifierBehavior ONLY_ONE = new QuantifierBehavior()
    {
        public boolean evaluate(
            Predicate<FilteredObjectType> predicate)
        {
            boolean previouslySatisfied = false;
            for (FilteredObjectType object : allMatches())
            {
                if (predicate.isSatisfiedBy(object))
                {
                    if (previouslySatisfied)
                    {
                        return false;
                    }
                    else
                    {
                        previouslySatisfied = true;
                    }
                }
            }
            return previouslySatisfied;
        }
    };


    // ----------------------------------------------------------
    /**
     * This constant represents an evaluation strategy that
     * "and"s together the results of the predicate across all matches.
     */
    private final QuantifierBehavior EVERY = new QuantifierBehavior()
    {
        public boolean evaluate(
            Predicate<FilteredObjectType> predicate)
        {
            for (FilteredObjectType object : allMatches())
            {
                if (!predicate.isSatisfiedBy(object))
                {
                    return false;
                }
            }
            return true;
        }
    };


    /**
     * Determines the quantification behavior of this filter for boolean
     * predicates (i.e, must all objects massing the filter satisfy the
     * condition, or at least one, etc.).
     *
     * This declaration has to be down here, instead of up at the top,
     * because EVERY has to be declared first before it can be used here.
     */
    protected QuantifierBehavior quantify = EVERY;
}
