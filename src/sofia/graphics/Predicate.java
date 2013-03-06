package sofia.graphics;

//-------------------------------------------------------------------------
/**
 * <p>
 * Predicates are used to specify numeric (and other) criteria when filtering
 * shapes. For example, it doesn't make sense to filter shapes that have a
 * velocity of <strong>exactly</strong> 3 (because velocity is a rapidly
 * changing floating-point value), but rather to find all the shapes with a
 * velocity of 3 or higher.
 * </p><p>
 * This class defines static factory methods for several commonly used numeric
 * comparisons. The easiest way to use them is to do a {@code static import}:
 * </p>
 * <pre>
 *     import static sofia.graphics.Predicate.*;
 * </pre>
 * <p>
 * Then, you can call the methods inside a filter chain without having to use
 * the class name as a prefix:
 * </p>
 * <pre>
 *     getShapes().withLinearVelocity(greaterThan(3)).withColor(...);
 * </pre>
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Revision$, $Date$
 */
public abstract class Predicate<T>
{
    //~ Fields ................................................................

    private static final double EPSILON = 1e-6;


    //~ Factory methods .......................................................

    // ----------------------------------------------------------
    /**
     * Returns a predicate that evaluates to true for values equal to a
     * specified target value. If the values being compared are floating-point
     * numbers, then they return equal if the difference between them is within
     * a tolerance of 10<sup>-6</sup>.
     *
     * @param  target the target value
     * @return a predicate that evaluates to true for values equal to a
     *         specified target value
     */
    public static final <T> Predicate<T> equalTo(final T target)
    {
        if (target instanceof Number)
        {
            final Number targetNumber = (Number) target;

            return new Predicate<T>() {
                @Override
                public boolean accept(T value)
                {
                    return compareNumbers(
                            (Number) value, targetNumber) == 0;
                }

                @Override
                public String toString()
                {
                    return "equal to " + target;
                }
            };
        }
        else
        {
            return new Predicate<T>() {
                @Override
                public boolean accept(T value)
                {
                    return safeEquals(value, target);
                }

                @Override
                public String toString()
                {
                    return "equal to " + target;
                }
            };
        }
    }


    // ----------------------------------------------------------
    /**
     * Returns a predicate that evaluates to true for values not equal to a
     * specified target value. If the values being compared are floating-point
     * numbers, then they return not equal if the difference between them is
     * greater than a tolerance of 10<sup>-6</sup>.
     *
     * @param  target the target value
     * @return a predicate that evaluates to true for values not equal to a
     *         specified target value
     */
    public static final <T> Predicate<T> notEqualTo(final T target)
    {
        if (target instanceof Number)
        {
            final Number targetNumber = (Number) target;

            return new Predicate<T>() {
                @Override
                public boolean accept(T value)
                {
                    return compareNumbers(
                            (Number) value, targetNumber) != 0;
                }

                @Override
                public String toString()
                {
                    return "not equal to " + target;
                }
            };
        }
        else
        {
            return new Predicate<T>() {
                @Override
                public boolean accept(T value)
                {
                    return !safeEquals(value, target);
                }

                @Override
                public String toString()
                {
                    return "not equal to " + target;
                }
            };
        }
    }


    // ----------------------------------------------------------
    /**
     * Returns a predicate that evaluates to true for numeric values less than
     * the specified upper bound.
     *
     * @param  upperBound the upper bound
     * @return a predicate that evaluates to true for numeric values less than
     *         the specified upper bound
     */
    public static final Predicate<Number> lessThan(final Number upperBound)
    {
        return new Predicate<Number>() {
            @Override
            public boolean accept(Number value)
            {
                return compareNumbers(value, upperBound) < 0;
            }

            @Override
            public String toString()
            {
                return "less than " + upperBound;
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * Returns a predicate that evaluates to true for numeric values less than
     * or equal to the specified upper bound.
     *
     * @param  upperBound the upper bound
     * @return a predicate that evaluates to true for numeric values less than
     *         or equal to the specified upper bound
     */
    public static final Predicate<Number> lessThanOrEqualTo(
            final Number upperBound)
    {
        return new Predicate<Number>() {
            @Override
            public boolean accept(Number value)
            {
                return compareNumbers(value, upperBound) <= 0;
            }

            @Override
            public String toString()
            {
                return "less than or equal to " + upperBound;
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * Returns a predicate that evaluates to true for numeric values greater
     * than the specified lower bound.
     *
     * @param  lowerBound the lower bound
     * @return a predicate that evaluates to true for numeric values greater
     *         than the specified lower bound
     */
    public static final Predicate<Number> greaterThan(final Number lowerBound)
    {
        return new Predicate<Number>() {
            @Override
            public boolean accept(Number value)
            {
                return compareNumbers(value, lowerBound) > 0;
            }

            @Override
            public String toString()
            {
                return "greater than " + lowerBound;
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * Returns a predicate that evaluates to true for numeric values greater
     * than or equal to the specified lower bound.
     *
     * @param  lowerBound the lower bound
     * @return a predicate that evaluates to true for numeric values greater
     *         than or equal to the specified lower bound
     */
    public static final Predicate<Number> greaterThanOrEqualTo(
            final Number lowerBound)
    {
        return new Predicate<Number>() {
            @Override
            public boolean accept(Number value)
            {
                return compareNumbers(value, lowerBound) >= 0;
            }

            @Override
            public String toString()
            {
                return "greater than or equal to " + lowerBound;
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * Returns a predicate that evaluates to true for numeric values between
     * (and including) the specified lower and upper bounds.
     *
     * @param  lowerBound the lower bound
     * @param  upperBound the upper bound
     * @return a predicate that evaluates to true for numeric values between
     *         (and including) the specified lower and upper bounds
     */
    public static final Predicate<Number> between(
            final Number lowerBound, final Number upperBound)
    {
        return new Predicate<Number>() {
            @Override
            public boolean accept(Number value)
            {
                return compareNumbers(value, lowerBound) >= 0
                        && compareNumbers(value, upperBound) <= 0;
            }

            @Override
            public String toString()
            {
                return "between " + lowerBound + " and " + upperBound;
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Returns a predicate that evaluates to true for classes that extend (or
     * are the same as) the specified class. Note that allowing matches of the
     * class type itself and not just "strict" subclasses is consistent with
     * the behavior of Java generics, where the type parameter
     * {@code <T extends X>} can be satisfied not only by subclasses of
     * {@code X} but also by {@code X} itself.
     * </p><p>
     * To find only <strong>exact</strong> matches and exclude subclasses, use
     * the {@link #equalTo(Object)} predicate instead.
     * </p>
     *
     * @param  klass the class that acts as an "upper bound" for type matching
     * @return a predicate that evaluates to true for class that extend (or are
     *         the same as) the specified class
     */
    public static final <T> Predicate<Class<? extends T>> extending(
            final Class<? extends T> theClass)
    {
        return new Predicate<Class<? extends T>>() {
            @Override
            public boolean accept(Class<? extends T> value)
            {
                return theClass.isAssignableFrom(value);
            }

            @Override
            public String toString()
            {
                return "the same as or extending "
                        + theClass.getCanonicalName();
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Returns a predicate that evaluates to true for classes that extend (or
     * are the same as) the specified class. Note that allowing matches of the
     * class type itself and not just "strict" subclasses is consistent with
     * the behavior of Java generics, where the type parameter
     * {@code <T extends X>} can be satisfied not only by subclasses of
     * {@code X} but also by {@code X} itself.
     * </p><p>
     * To find only <strong>exact</strong> matches and exclude subclasses, use
     * the {@link #equalTo(Object)} predicate instead.
     * </p>
     *
     * @param  klass the class that acts as an "upper bound" for type matching
     * @return a predicate that evaluates to true for class that extend (or are
     *         the same as) the specified class
     */
    public static final <T> Predicate<Class<? extends T>> equalTo(
            final Class<? extends T> theClass)
    {
        return new Predicate<Class<? extends T>>() {
            @Override
            public boolean accept(Class<? extends T> value)
            {
                return theClass.equals(value);
            }

            @Override
            public String toString()
            {
                return "the same as or extending "
                        + theClass.getCanonicalName();
            }
        };
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Subclasses of {@code Predicate} must override this method and return
     * true if the predicate accepts the specified value or false if it rejects
     * it.
     *
     * @param  value the value to accept or reject
     * @return true if the predicate accepts the value, otherwise false
     */
    public abstract boolean accept(T value);


    // ----------------------------------------------------------
    /**
     * Subclasses <strong>must</strong> override {@code toString} to provide a
     * human readable description of the predicate.
     *
     * @return a human readable description of the predicate
     */
    public abstract String toString();


    //~ Private methods .......................................................

    // ----------------------------------------------------------
    /**
     * A "safe" equals method that handles null arguments correctly.
     *
     * @param  lhs the first argument
     * @param  rhs the second argument
     * @return true if the objects are equal (as determined by calling the
     *         {@code equals} method), or if both arguments are null
     */
    private static final boolean safeEquals(Object lhs, Object rhs)
    {
        if (lhs == null && rhs == null)
        {
            return true;
        }
        else if (lhs == null && rhs != null || lhs != null && rhs == null)
        {
            return false;
        }
        else
        {
            return lhs.equals(rhs);
        }
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Compares two numbers using the same kind of comparison that the
     * {@code compareTo} method would use. This method treats both numbers as
     * doubles if either of them is a double (in which case equality means
     * within a tolerance of 10<sup>-6</sup>); otherwise, it treats them as
     * longs.
     * </p><p>
     * Caveat: This method currently does not support {@code BigDecimal} or
     * {@code BigInteger}.
     * </p>
     *
     * @param  lhs the first number to compare
     * @param  rhs the second number to compare
     * @return 0 if the numbers are equal, -1 if {@code lhs} is less than
     *         {@code rhs}, or 1 if {@code lhs} is greater than {@code rhs}
     */
    private static final int compareNumbers(Number lhs, Number rhs)
    {
        if (lhs instanceof Float || lhs instanceof Double
                || rhs instanceof Float || rhs instanceof Double)
        {
            double lhsValue = lhs.doubleValue();
            double rhsValue = rhs.doubleValue();

            if (Math.abs(lhsValue - rhsValue) < EPSILON)
            {
                return 0;
            }
            else if (lhsValue > rhsValue)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            long lhsValue = lhs.longValue();
            long rhsValue = rhs.longValue();

            if (lhsValue > rhsValue)
            {
                return 1;
            }
            else if (lhsValue < rhsValue)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }
}
