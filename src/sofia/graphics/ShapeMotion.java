package sofia.graphics;

import org.jbox2d.dynamics.BodyType;

//-------------------------------------------------------------------------
/**
 * Determines the physical nature of a shape and how it is simulated and how it
 * responds to forces.
 *
 * TODO elaborate
 *
 * @author  Tony Allevato
 * @version 2012.11.05
 */
public enum ShapeMotion
{
    //~ Constants .............................................................

    // ----------------------------------------------------------
    /**
     * <p>
     * Static shapes do not move under the physics simulation, behave as if
     * they have infinite mass, and have zero velocity. Static shapes also do
     * not collide with other static or kinematic shapes, but they can be
     * involved in collisions with dynamic shapes.
     * </p><p>
     * In a game, for example, static shapes are best used to represent things
     * like the ground, walls, and other immovable, impenetrable obstacles.
     * </p><p>
     * This is the default motion type for newly created shapes.
     * </p>
     */
    STATIC(BodyType.STATIC),


    // ----------------------------------------------------------
    /**
     * Dynamic shapes are fully simulated in the physics simulation. They have
     * non-zero mass, move according to forces, and can collide with all other
     * shape types (static, dynamic, and kinematic).
     */
    DYNAMIC(BodyType.DYNAMIC),


    // ----------------------------------------------------------
    /**
     * <p>
     * Kinematic shapes move under the physics simulation according to their
     * velocity but do not respond to forces. They behave as if they have
     * infinite mass. Kinematic shapes do not collide with static shapes or
     * other kinematic shapes, but they can be involved in collisions with
     * dynamic shapes.
     * </p><p>
     * An example of a kinematic shape in a game would be a moving platform
     * that slides back and forth in a predetermined way.
     * </p>
     */
    KINEMATIC(BodyType.KINEMATIC);


    //~ Fields ................................................................

    private BodyType b2BodyType;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes the value with the specified JBox2D {@code BodyType}.
     *
     * @param type the JBox2D {@code BodyType} that this value represents
     */
    private ShapeMotion(BodyType type)
    {
        b2BodyType = type;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * <strong>This method is for internal and advanced use only.</strong>
     * Gets the JBox2D {@code BodyType} that this value represents.
     *
     * @return the JBox2D {code BodyType} that this value represents
     */
    public BodyType getB2BodyType()
    {
        return b2BodyType;
    }


    // ----------------------------------------------------------
    /**
     * <strong>This method is for internal and advanced use only.</strong>
     * Gets the value that corresponds to the specified JBox2D
     * {@code BodyType}.
     *
     * @param type the JBox2D {@code BodyType}
     * @return the value corresponding to the specified JBox2D
     *     {@code BodyType}
     */
    public static ShapeMotion fromB2BodyType(BodyType type)
    {
        switch (type)
        {
            case STATIC:    return STATIC;
            case DYNAMIC:   return DYNAMIC;
            case KINEMATIC: return KINEMATIC;
            default:        return null;
        }
    }
}
