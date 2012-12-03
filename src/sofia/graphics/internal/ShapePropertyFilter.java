package sofia.graphics.internal;

import sofia.graphics.Color;
import sofia.graphics.Shape;

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
 *  @version $Revision: 1.2 $, $Date: 2011/06/09 15:31:24 $
 */
public abstract class ShapePropertyFilter<
    ConcreteFilterType,
    FilteredObjectType extends Shape>
    extends Filter<ConcreteFilterType, FilteredObjectType>
{
    //~ Fields ................................................................

    private Color requiredColor;


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new ShapePropertyFilter object.
     * @param previous The previous filter in the chain of filters.
     * @param descriptionOfConstraint A description of the constraint imposed
     * by this filter (just one step in the chain).
     */
    protected ShapePropertyFilter(
        ShapePropertyFilter<ConcreteFilterType, FilteredObjectType> previous,
        String descriptionOfConstraint)
    {
        super(previous, descriptionOfConstraint);
        requiredColor = null;
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Restrict this filter to only admit declarations with the specified
     * name.
     * @param name The name required by the resulting filter.
     * @return A new filter with the given restriction.
     */
    @SuppressWarnings("unchecked")
    public ConcreteFilterType withColor(Color color)
    {
        if (color == null)
        {
            ConcreteFilterType result = (ConcreteFilterType)this;
            return result;
        }

        ConcreteFilterType result = createFreshFilter(
            (ConcreteFilterType)this, "with color \"" + color + '"');

        ShapePropertyFilter<ConcreteFilterType, FilteredObjectType> filter =
            (ShapePropertyFilter<ConcreteFilterType, FilteredObjectType>)result;
        filter.requiredColor = color;

        return result;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the color of the object matching this filter.
     * @return The object's color.
     */
    public Color getColor()
    {
        if (exists())
        {
            return colorOf(uniqueMatch());
        }
        else if (requiredColor != null)
        {
            return requiredColor;
        }
        else if (previousFilter() != null
            && previousFilter() instanceof ShapePropertyFilter)
        {
            ShapePropertyFilter<ConcreteFilterType, FilteredObjectType> filter =
                (ShapePropertyFilter<ConcreteFilterType, FilteredObjectType>)
                previousFilter();
            return filter.getColor();
        }
        else
        {
            return null;
        }
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * A helper for diagnostic messages that should be used in place
     * of {@link #getColor()} when you want a printable (i.e., non-null)
     * name.
     * @return The name, if it exists, or the string "unknown" if
     * the name is null.
     */
    protected String getColorOrUnknown()
    {
        String color = getColor().toString();
        return (color == null) ? "unknown" : color;
    }


    // ----------------------------------------------------------
    /**
     * Extract the color from the specified object.
     * @param object The object to retrieve the color from
     * @return The color of the object.
     */
    protected abstract Color colorOf(FilteredObjectType object);


    // ----------------------------------------------------------
    /**
     * TODO: document.
     * @param object TODO: describe
     * @return TODO: describe
     */
    protected boolean thisFilterAccepts(FilteredObjectType object)
    {
        boolean result = true;
        Color color = colorOf(object);
        if (requiredColor != null)
        {
            result = requiredColor.equals(color);
        }
        return result;
    }
}
