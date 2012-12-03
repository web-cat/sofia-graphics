package sofia.graphics.internal;

import sofia.graphics.Color;
import sofia.graphics.Shape;

//-------------------------------------------------------------------------
/**
 *  TODO: document.
 *
 *  TODO: add annotation support.
 *
 *  @param <ReturnType> If present, this is a constraint on the return type
 *  of the method that this object represents.
 *
 *  @author  Stephen Edwards
 *  @author  Last changed by $Author: stedwar2 $
 *  @version $Revision: 1.2 $, $Date: 2011/06/09 15:31:24 $
 */
public class ShapeFilter
    extends ShapePropertyFilter<ShapeFilter, Shape>
{
    //~ Fields ................................................................

    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Method object that represents a method filter.
     * @param previous The previous filter in the chain of filters.
     * @param descriptionOfThisStage A description of this stage in the
     * filter chain.
     */
    protected ShapeFilter(ShapeFilter previous, String descriptionOfThisStage)
    {
        super(previous, descriptionOfThisStage);
    }


    //~ Public Methods ........................................................


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    @Override
    protected ShapeFilter createFreshFilter(
        ShapeFilter previous,
        String descriptionOfThisStage)
    {
        return new ShapeFilter(previous, descriptionOfThisStage);
    }


    // ----------------------------------------------------------
    @Override
    protected ShapeFilter createFreshFilter(Shape object)
    {
        // TODO Auto-generated method stub
        return null;
    }


    // ----------------------------------------------------------
    @Override
    protected Color colorOf(Shape object)
    {
        return object.getColor();
    }


    // ----------------------------------------------------------
    @Override
    protected String filteredObjectDescription()
    {
        return "shape";
    }
}
