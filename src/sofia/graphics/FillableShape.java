package sofia.graphics;

import sofia.graphics.internal.animation.FillColorTransformer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

// -------------------------------------------------------------------------
/**
 * An abstract class that represents shapes that can be filled when they are
 * drawn, and for which the fill color can be set independently of the shape's
 * other color (for the stroke).
 *
 * @author  Tony Allevato
 * @version 2011.11.27
 */
public abstract class FillableShape extends StrokedShape
{
    //~ Fields ................................................................

    private Image image;
    private boolean filled;
    private Color fillColor;
    private boolean fillColorSet;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new {@code FillableShape}.
     */
    public FillableShape()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @SuppressWarnings("rawtypes")
    public Animator<?> animate(long duration)
    {
        return new Animator(duration);
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the shape will be filled when it is
     * drawn.
     *
     * @return true if the shape will be filled when it is drawn, or false if
     *     it will be drawn as an outline
     */
    public boolean isFilled()
    {
        return filled;
    }


    // ----------------------------------------------------------
    /**
     * Sets a value indicating whether the shape will be filled when it is
     * drawn.
     *
     * @param newFilled true if the shape will be filled when it is drawn,
     *     or false to draw it as an outline
     */
    public void setFilled(boolean newFilled)
    {
        this.filled = newFilled;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Gets the color used to fill the shape. This color can be set explicitly
     * by calling {@link #setFillColor(Color)}; if it has not been set, then
     * the shape's color as defined by {@link #getColor()} will be used to fill
     * the shape.
     *
     * @return the {@link Color} used to fill the shape
     */
    public Color getFillColor()
    {
        if (fillColorSet)
        {
            return fillColor;
        }
        else
        {
            return getColor();
        }
    }


    // ----------------------------------------------------------
    /**
     * Sets the color used to fill the shape. If it has not been set, then the
     * shape's color as defined by {@link #getColor()} will be used to fill the
     * shape.
     *
     * @param newFillColor the {@link Color} to use to fill the shape
     */
    public void setFillColor(Color newFillColor)
    {
        if (newFillColor == null)
        {
            throw new IllegalArgumentException("Color cannot be null. To "
                    + "remove the color from a shape, use Color.transparent.");
        }


        this.fillColor = newFillColor;
        this.fillColorSet = true;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Gets the image used to fill this shape.
     *
     * @return the image used to fill this shape
     */
    public Image getImage()
    {
        return image;
    }


    // ----------------------------------------------------------
    /**
     * Sets the image used to fill this shape.
     *
     * @param newImage the image used to fill this shape, or null to remove the
     *     image
     */
    public void setImage(Image newImage)
    {
        image = newImage;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Sets the image used to fill this shape.
     *
     * @param newImage the image used to fill this shape, or null to remove the
     *     image
     */
    public void setImage(String imageName)
    {
        if (imageName == null)
        {
            setImage((Image) null);
        }
        else
        {
            setImage(new Image(imageName));
        }
    }


    // ----------------------------------------------------------
    /**
     * Removes the image used to fill this shape. This method exists for
     * ease of use because trying to call {@code setImage(null)} would be
     * ambiguous without an explicit cast.
     */
    public void removeImage()
    {
        setImage((Image) null);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * A convenience method that gets the alpha (opacity) component of the
     * shape's color.
     * </p><p>
     * Note that calling the {@link #setAlpha(int)} method will update the
     * alpha components of both the color and the fill color of the shape, so
     * this method would return the single alpha value in that case. In the
     * event that the shape's color and fill color have been set explicitly to
     * have different alpha components, this method returns the alpha component
     * of the color returned by {@link #getColor()}.
     * </p>
     *
     * @return The alpha component of the shape's color, where 0 means that
     *         the color is fully transparent and 255 means that it is fully
     *         opaque.
     */
    @Override
    public int getAlpha()
    {
        // No behavioral change; only overridden to provide updated Javadoc.
        return super.getAlpha();
    }


    // ----------------------------------------------------------
    /**
     * A convenience method that sets the alpha (opacity) component of the
     * shape's color and fill color without changing the other color
     * components.
     *
     * @param newAlpha The new alpha component of the shape's color, where 0
     *                 means that the color is fully transparent and 255
     *                 means that it is fully opaque.
     */
    @Override
    public void setAlpha(int newAlpha)
    {
        super.setAlpha(newAlpha);
        setFillColor(getFillColor().withAlpha(newAlpha));
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@link Paint} used to fill the shape. Android's drawing
     * primitives in the {@code Canvas} class do not support having separate
     * stroke and fill colors in a single drawing operation, so subclasses of
     * {@code FillableShape}s typically draw themselves twice -- once for the
     * fill, using the {@code Paint} returned by this method, and then again
     * for the outline, using the {@code Paint} returned by
     * {@link #getPaint()}.
     *
     * @return the {@code Paint} used to fill the shape when it is drawn
     */
    protected Paint getFillPaint()
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(getFillColor().toRawColor());
        return paint;
    }


    // ----------------------------------------------------------
    /**
     * Draws the image for this shape. Called by subclasses inside their
     * {@link #draw(Canvas)} methods.
     *
     * @param canvas the {@code Canvas} to draw on
     */
    protected void drawBitmap(Canvas canvas)
    {
        resolveBitmapIfNecessary();

        // In some cases, the bitmap may be missing...
        Bitmap bm = image.asBitmap();
        if (bm != null)
        {
            RectF sortedBounds = new RectF(getBounds());
            sortedBounds.sort();

            // If the coordinate system is flipped in either direction, we need
            // to do another temporary flip to ensure that the images are drawn
            // in their native orientation.

            CoordinateSystem cs = getParentView().getCoordinateSystem();
            boolean flipX = cs.isFlippedX();
            boolean flipY = cs.isFlippedY();

            if (flipX || flipY)
            {
                canvas.save();

                Matrix matrix = new Matrix();
                matrix.setScale(flipX ? -1 : 1, flipY ? -1 : 1);
                matrix.postTranslate(
                        flipX ? sortedBounds.left + sortedBounds.right : 0,
                        flipY ? sortedBounds.top + sortedBounds.bottom : 0);
                canvas.concat(matrix);
            }

            canvas.drawBitmap(bm, null, sortedBounds, getImagePaint());

            if (flipX || flipY)
            {
                canvas.restore();
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@link Paint} object suitable for drawing the image for this
     * shape.
     *
     * @return a {@code Paint} object
     */
    protected Paint getImagePaint()
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        return paint;
    }


    // ----------------------------------------------------------
    private void resolveBitmapIfNecessary()
    {
        if (image != null && image.asBitmap() == null)
        {
            image.resolveAgainstContext(getParentView().getContext());
        }
    }


    //~ Animation support classes .............................................

    // ----------------------------------------------------------
    /**
     * Provides animation support for shapes. Most uses of this class will not
     * need to reference it directly; for example, an animation can be
     * constructed and played by chaining method calls directly:
     *
     * <pre>
     *     shape.animate(500).color(Color.blue).alpha(128).play();</pre>
     *
     * In situations where the type of the class must be referenced directly
     * (for example, when one is passed to an event handler like
     * {@code onAnimationDone}), referring to the name of that type can be
     * somewhat awkward due to the use of some Java generics tricks to ensure
     * that the methods chain properly. In nearly all cases, it is reasonable
     * to use a "?" wildcard in place of the generic parameter:
     *
     * <pre>
     *     Shape.Animator&lt;?&gt; anim = shape.animate(500).color(Color.blue);
     *     anim.play();</pre>
     *
     * @param <AnimatorType> the concrete type of the animator
     *
     * @author  Tony Allevato
     * @version 2011.12.11
     */
    public class Animator<
        AnimatorType extends FillableShape.Animator<AnimatorType>>
        extends StrokedShape.Animator<AnimatorType>
    {
        //~ Constructors ......................................................

        // ----------------------------------------------------------
        /**
         * Creates a new animator for the specified shape. Users cannot call
         * call this constructor directly; instead, they need to use the
         * {@link StrokedShape#animate(long)} method to get an animator object.
         *
         * @param shape the shape to animate
         * @param duration the length of one pass of the animation, in
         *     milliseconds
         */
        protected Animator(long duration)
        {
            super(duration);
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        /**
         * Gets the shape that the receiver is animating.
         *
         * @return the shape that the receiver is animating
         */
        @Override
        public FillableShape getShape()
        {
            return FillableShape.this;
        }


        // ----------------------------------------------------------
        /**
         * Sets the final fill color of the shape when the animation ends.
         *
         * @param fillColor the final fill color of the shape when the
         *     animation ends
         * @return this animator, for method chaining
         */
        @SuppressWarnings("unchecked")
        public AnimatorType fillColor(Color fillColor)
        {
            addTransformer(new FillColorTransformer(getShape(), fillColor));
            return (AnimatorType) this;
        }
    }
}
