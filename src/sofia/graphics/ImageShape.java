package sofia.graphics;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * A shape that renders itself using a bitmap image.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public class ImageShape
    extends Shape
{
    //~ Fields ................................................................

    private RectF bounds;

    private Image image;
    private Rect sourceBounds;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates an {@code ImageShape} with default position and size, and no
     * associated image. The shape will appear blank when it is drawn unless an
     * image is set by calling {@link #setImage(String)} or a similar method.
     */
    public ImageShape()
    {
        this(new RectF(0, 0, 0, 0));
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code ImageShape} with the specified bounds and no
     * associated image. The shape will appear blank when it is drawn unless an
     * image is set by calling {@link #setImage(String)} or a similar method.
     *
     * @param bounds the bounds of the shape, which will cause the image to be
     *     stretched or shrunk if necessary to fit
     */
    public ImageShape(RectF bounds)
    {
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code ImageShape} with the specified bounds and no
     * associated image. The shape will appear blank when it is drawn unless an
     * image is set by calling {@link #setImage(String)} or a similar method.
     *
     * @param left the x-coordinate of the left side of the area where the
     *     image will be drawn
     * @param top the y-coordinate of the top of the area where the image will
     *     be drawn
     * @param right the x-coordinate of the right side of the area where the
     *     image will be drawn
     * @param bottom the y-coordinate of the bottom of the area where the image
     *     will be drawn
     */
    public ImageShape(float left, float top, float right, float bottom)
    {
        this(new RectF(left, top, right, bottom));
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code ImageShape} stretched to fit the specified bounds.
     *
     * @param image a Sofia {@link Image} object that will be drawn by the
     *     shape
     * @param bounds the bounds of the shape, which will cause the image to be
     *     stretched or shrunk if necessary to fit
     */
    public ImageShape(Image image, RectF bounds)
    {
        this.image = image;
        setBounds(bounds);
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code ImageShape} stretched to fit the specified bounds.
     *
     * @param bitmap an Android {@link Bitmap} object that will be drawn by the
     *     shape
     * @param bounds the bounds of the shape, which will cause the image to be
     *     stretched or shrunk if necessary to fit
     */
    public ImageShape(Bitmap bitmap, RectF bounds)
    {
        this(new Image(bitmap), bounds);
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code ImageShape} stretched to fit the specified bounds.
     *
     * @param bitmapId the numeric ID ({@code R.drawable.*}) of an image
     *     resource in your application package that represents the image that
     *     should be drawn
     * @param bounds the bounds of the shape, which will cause the image to be
     *     stretched or shrunk if necessary to fit
     */
    public ImageShape(int bitmapId, RectF bounds)
    {
        this(new Image(bitmapId), bounds);
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code ImageShape} stretched to fit the specified bounds.
     *
     * @param imageName the name of the image, either from a resource (e.g.,
     *     "res/drawable-hdpi/foo.png" would be "foo") or from an image in the
     *     source tree
     * @param bounds the bounds of the shape, which will cause the image to be
     *     stretched or shrunk if necessary to fit
     */
    public ImageShape(String imageName, RectF bounds)
    {
        this(new Image(imageName), bounds);
    }


    // ----------------------------------------------------------
    /**
     * Creates an {@code ImageShape} stretched to fit the specified bounds.
     *
     * @param imageName the name of the image, either from a resource (e.g.,
     *     res/drawable-hdpi/foo.png would be "foo") or from an image in the
     *     source tree
     * @param left the x-coordinate of the left side of the area where the
     *     image will be drawn
     * @param top the y-coordinate of the top of the area where the image will
     *     be drawn
     * @param right the x-coordinate of the right side of the area where the
     *     image will be drawn
     * @param bottom the y-coordinate of the bottom of the area where the image
     *     will be drawn
     */
    public ImageShape(String imageName, float left, float top, float right,
            float bottom)
    {
        this(imageName, new RectF(left, top, right, bottom));
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the image drawn by this shape.
     *
     * @return the {@link Image} drawn by this shape
     */
    public Image getImage()
    {
        return image;
    }


    // ----------------------------------------------------------
    /**
     * Sets the image drawn by this shape.
     *
     * @param newImage the {@link Image} to be drawn by this shape
     */
    public void setImage(String newImage)
    {
        setImage(new Image(newImage));
    }


    // ----------------------------------------------------------
    /**
     * Sets the image drawn by this shape.
     *
     * @param newImage the {@link Image} to be drawn by this shape
     */
    public void setImage(Image newImage)
    {
        this.image = newImage;
        sourceBounds = null;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * A convenience method that sets the image drawn by this shape from an
     * Android {@link Bitmap} object instead of a Sofia {@code Image}.
     *
     * @param newBitmap the {@link Bitmap} to be drawn by this shape
     */
    public void setBitmap(Bitmap newBitmap)
    {
        setImage(new Image(newBitmap));
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Gets a {@code Rect} indicating the subset of the image that should be
     * drawn. Notice that these are integer coordinates since they represent
     * pixels in the image.
     * </p><p>
     * This method returns null if the entire image is being drawn.
     * </p>
     *
     * @return a {@code Rect} indicating the subset of the image that should be
     *     drawn, or null if the entire image is being drawn
     */
    public Rect getSourceBounds()
    {
        return sourceBounds;
    }


    // ----------------------------------------------------------
    /**
     * Sets the subset of the image that should be drawn. Notice that these are
     * integer coordinates since they represent pixels in the image.
     *
     * @param newSourceBounds the bounds of the source rectangle indicating the
     *     portion of the image to be drawn, or null to draw the entire image
     */
    public void setSourceBounds(Rect newSourceBounds)
    {
        this.sourceBounds = newSourceBounds;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Sets the subset of the image that should be drawn. Notice that these are
     * integer coordinates since they represent pixels in the image.
     * </p><p>
     * In order to revert this and have the entire image drawn again, call
     * {@link #setSourceBounds(Rect)} and pass it {@code null}.
     * </p>
     *
     * @param left the x-coordinate of the left side of the source rectangle
     * @param top the y-coordinate of the top of the source rectangle
     * @param right the x-coordinate of the right side of the source rectangle
     *     (which is <em>not</em> included in the pixels that are drawn)
     * @param bottom the y-coordinate of the bottom of the source rectangle
     *     (which is <em>not</em> included in the pixels that are drawn)
     */
    public void setSourceBounds(int left, int top, int right, int bottom)
    {
        setSourceBounds(new Rect(left, top, right, bottom));
    }


    // ----------------------------------------------------------
    @Override
    public RectF getBounds()
    {
        // If the body has been created, update the bounding box using the
        // body's current position.

        Body b2Body = getB2Body();
        if (b2Body != null)
        {
            float hw = bounds.width() / 2;
            float hh = bounds.height() / 2;
            Vec2 center = b2Body.getPosition();
            bounds.offsetTo(center.x - hw, center.y - hh);
        }

        return new RectF(bounds);
    }


    // ----------------------------------------------------------
    @Override
    public void setBounds(RectF newBounds)
    {
        bounds = new RectF(newBounds);

        updateTransform(bounds.centerX(), bounds.centerY());

        recreateFixtures();
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Canvas canvas)
    {
        loadBitmapIfNecessary();

        // In some cases, the bitmap may be missing ...
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

            canvas.drawBitmap(bm, sourceBounds, sortedBounds, getPaint());

            if (flipX || flipY)
            {
                canvas.restore();
            }
        }
    }


    // ----------------------------------------------------------
    @Override
    protected Paint getPaint()
    {
        Paint paint = super.getPaint();

        Color color = getColor();

        if (color != null)
        {
            Color fullColor = color.withAlpha(255);

            paint.setAntiAlias(true);
            paint.setColorFilter(
                    new PorterDuffColorFilter(fullColor.toRawColor(),
                            PorterDuff.Mode.MULTIPLY));
            paint.setAlpha(color.alpha());
        }

        return paint;
    }


    // ----------------------------------------------------------
    private void loadBitmapIfNecessary()
    {
        if (image.asBitmap() == null)
        {
            image.resolveAgainstContext(getParentView().getContext());
        }
    }


    // ----------------------------------------------------------
    @Override
    protected void createFixtures()
    {
        PolygonShape box = new PolygonShape();
        box.setAsBox(
                Math.abs(bounds.width() / 2), Math.abs(bounds.height() / 2));

        addFixtureForShape(box);
    }
}
