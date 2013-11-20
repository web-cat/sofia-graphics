/**
 * <p>
 * Provides low-level graphical components like colors, shape primitives for
 * creating and adding shapes directly to the screen using a canvas-based
 * model, and animation.
 * </p>
 *
 * <h3>Shapes</h3>
 * <p>
 * This package provides an object-oriented model for drawing shapes on the
 * screen. The {@link sofia.graphics.ShapeView} provides a canvas where objects
 * that extend {@link sofia.graphics.Shape} can be added and removed, and the
 * visuals on the screen are automatically updated whenever shapes are
 * manipulated.
 * </p><p>
 * Sofia provides a number of shape classes that you can begin using right
 * away:
 * </p>
 * <ul>
 * <li>{@link sofia.graphics.LineShape}</li>
 * <li>{@link sofia.graphics.RectangleShape}</li>
 * <li>{@link sofia.graphics.OvalShape}</li>
 * <li>{@link sofia.graphics.TextShape}</li>
 * </ul>
 * <p>
 * You can use these shape classes directly or extend them to add your own
 * capabilities. For example, in a game you might extend
 * {@link sofia.graphics.RectangleShape} to create a few classes that represent
 * different playable and non-playable entities in the game.
 * </p>
 *
 * <h3>Animation</h3>
 * <p>
 * All shapes have an {@link sofia.graphics.Shape#animate(long)} method that
 * allow you to construct animations that will run in the background. This
 * method takes the duration of the animation in milliseconds as an argument
 * (so an animation that runs for 1 second would pass in 1000). It returns an
 * {@link sofia.graphics.Shape.Animator} object (or an {@code Animator}
 * nested inside the {@code Shape} subclass being animated) with several
 * methods that can be chained together to indicate which properties should be
 * animated.
 * </p><p>
 * These animations are <em>interpolating animations</em>, meaning that they
 * operate by considering two values &ndash; the value of a shape property at
 * the beginning of an animation and the desired value of the same property at
 * the end of the animation &ndash; and interpolates the intermediate values of
 * that property to make the shape appear to animate. Consider the following
 * example:
 * </p>
 * <pre>
 *     shape.setColor(Color.black);
 *     shape.animate(1000).color(Color.red).play();</pre>
 * <p>
 * In this case a shape starts out black and animates to red over a duration of
 * 1 second. This would cause the shape to "fade in", starting dark and getting
 * brighter until the duration has passed.
 * </p><p>
 * Motion can also be achieved in these animations:
 * <pre>
 *     shape.setColor(Color.black);
 *     shape.animate(1000).position(10, 40).play();</pre>
 * </p>
 *
 * <h3>Events</h3>
 * <p>
 * <em>To be written.</em>
 * </p>
 *
 * @since API level 1
 */
package sofia.graphics;
