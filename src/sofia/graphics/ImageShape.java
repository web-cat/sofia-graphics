package sofia.graphics;

import android.graphics.Bitmap;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * A shape that represents images, currently just a shortcut for using images
 * with the {@link RectangleShape}.
 *
 * @author  Brian Bowden
 * @author  Last changed by $Author$
 * @version $Date: 2013/10/29 16:32 $
 */
public class ImageShape
    extends RectangleShape
{
    //~ Fields ................................................................
    private Image image;

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
     * Creates an {@code ImageShape} with the specified bounds and no
     * associated image. The shape will appear blank when it is drawn unless an
     * image is set by calling {@link #setImage(String)} or a similar method.
     *
     * @param bounds the bounds of the shape, which will cause the image to be
     *     stretched or shrunk if necessary to fit
     */
    public ImageShape(RectF bounds)
    {
        super(bounds);
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
        super(bounds);
        setImage(image);
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
        super(bounds);
        image = new Image(bitmap);
        setImage(image);
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
        super(bounds);
        image = new Image(bitmapId);
        setImage(image);
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
        super(bounds);
        image = new Image(imageName);
        setImage(image);
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
     * A convenience method that sets the image drawn by this shape from an
     * Android {@link Bitmap} object instead of a Sofia {@code Image}.
     *
     * @param newBitmap the {@link Bitmap} to be drawn by this shape
     */
    public void setBitmap(Bitmap newBitmap)
    {
        setImage(new Image(newBitmap));
    }
}
