package sofia.graphics;

import java.io.InputStream;
import sofia.internal.JarResources;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//-------------------------------------------------------------------------
/**
 * Represents a single bitmapped image, such as one loaded from a file.
 *
 * <p>This class cannot be a subclass of {@link Bitmap}, since Bitmap is
 * a final class.</p>
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public class Image
{
    //~ Fields ................................................................

    private Bitmap   bitmap;
    private int      bitmapId;
    private Class<?> klass;
    private String   fileName;
    private boolean  useDefault = true;

    private static Bitmap defaultImage;

    private static final String[] EXTENSIONS = {
        ".png", ".PNG", ".gif", ".GIF", ".jpg", ".JPG", ".JPEG", ".JPEG"
    };


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create an image from a bitmap.
     * @param bitmap The bitmap forming this image's contents.
     */
    public Image(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }


    // ----------------------------------------------------------
    /**
     * Create an image from a bitmap by specifying a resource id.
     * @param bitmapId The id of the bitmap resource for this image.
     */
    public Image(int bitmapId)
    {
        this.bitmapId = bitmapId;
    }


    // ----------------------------------------------------------
    /**
     * Create an image from a class.
     * TODO: Lots of fancy searching that still needs documenting.
     *
     * @param klass The Java class after which the file is named.
     */
    public Image(Class<?> klass)
    {
        this.klass = klass;
    }


    // ----------------------------------------------------------
    /**
     * Create an image from a file.
     * TODO: Lots of fancy searching that still needs documenting.
     *
     * @param fileName The name of the image file.
     */
    public Image(String fileName)
    {
        this.fileName = fileName;
    }


    // ----------------------------------------------------------
    /**
     * Get a new Image object that renders as the default Sofia
     * image.
     * @return a new Image object that renders as the default image.
     */
    public static Image getDefault()
    {
        return new Image((String)null);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Access the contents of this image in the form of an Android
     * {@link Bitmap}.
     * @return A Bitmap with this image's contents.
     */
    public Bitmap asBitmap()
    {
        return bitmap;
    }


    // ----------------------------------------------------------
    /**
     * Determine whether this image should use the default Sofia
     * image/icon, if no image corresponding to the search criteria
     * (e.g., a class name, or a file name) is found.  The default
     * is true.
     *
     * @param useDefault True if this image should render as the
     *                   Sofia default image if no corresponding resource
     *                   is found.  If false, and no image is found,
     *                   then asBitmap() will return null.
     */
    public void setUseDefaultIfNotFound(boolean useDefault)
    {
        this.useDefault = useDefault;
    }


    // ----------------------------------------------------------
    /**
     * Says whether this object will use the default Sofia
     * image/icon, if no image corresponding to the search criteria
     * specified in its constructor call (e.g., a class name, or a
     * file name) is found.  If it returns false, and no image file is
     * found, then {@link #asBitmap()} will return null. The default
     * is true.
     *
     * @return True if this image should render as the Sofia default image
     * when no corresponding resource is found.
     */
    public boolean useDefaultIfNotFound()
    {
        return useDefault;
    }


    // ----------------------------------------------------------
    /**
     * Provide an Android resource context to use for loading this
     * image (this must be called before any class/id/file name image
     * will be available).
     * @param context The context to resolve this image against.
     */
    public void resolveAgainstContext(Context context)
    {
        if (bitmap == null)
        {
//            System.out.println("Image.resolveAgainstContext(" + context + ")");
            if (bitmapId != 0)
            {
                bitmap = BitmapFactory.decodeResource(
                    context.getResources(), bitmapId);
//                System.out.println("id " + Integer.toString(bitmapId, 16)
//                    + " = " + bitmap);
            }
            else
            {
                // Find stream based on parameters
                if (fileName != null)
                {
                    bitmap = bitmapForBaseName(fileName, context, null);
//                    System.out.println(
//                        "fileName " + fileName + " = " + stream);
                }
                else if (klass != null)
                {
                    bitmap = bitmapFor(context, klass);
//                    System.out.println(
//                        "class " + klass.getName() + " = " + stream);
                }
            }
        }

        if (bitmap == null && useDefault)
        {
            if (defaultImage == null)
            {
                // Default to generic logo
                defaultImage = BitmapFactory.decodeResource(
                    context.getResources(),
                    R.drawable.sofia_default_image);
                System.out.println("loading default image = " + bitmap);
            }
            bitmap = defaultImage;
            System.out.println("bitmap = default image = " + bitmap);
        }

        if (bitmap != null)
        {
            System.out.println("bitmap " + bitmap + " " + bitmap.getWidth()
                + "x" + bitmap.getHeight() + " density = " +
                bitmap.getDensity());
            System.out.println("target density = " +
                context.getResources().getDisplayMetrics().densityDpi);
        }
    }


    // ----------------------------------------------------------
    /**
     * Return the width of this bitmap.
     *
     * <p>Before calling this method, the bitmap must be resolved (that is,
     * loaded from a resource).</p>
     *
     * @return The width of this bitmap, in pixels.
     * @throws IllegalStateException If the bitmap has not yet been resolved.
     */
    public int getWidth()
    {
        if (bitmap == null)
        {
            throw new IllegalStateException("This method cannot be called "
                + "until the bitmap has been resolved.");
        }
        return bitmap.getWidth();
    }


    // ----------------------------------------------------------
    /**
     * Return the height of this bitmap.
     *
     * <p>Before calling this method, the bitmap must be resolved (that is,
     * loaded from a resource).</p>
     *
     * @return The height of this bitmap, in pixels.
     * @throws IllegalStateException If the bitmap has not yet been resolved.
     */
    public int getHeight()
    {
        if (bitmap == null)
        {
            throw new IllegalStateException("This method cannot be called "
                + "until the bitmap has been resolved.");
        }
        return bitmap.getHeight();
    }


    // ----------------------------------------------------------
    /**
     * Returns the Color at the specified location. Throws an exception if
     * x or y are out of bounds (negative or >= to the width or height
     * respectively).
     *
     * @param x The x coordinate (0...width-1) of the pixel to return.
     * @param y The y coordinate (0...height-1) of the pixel to return.
     * @return The Color at the specified coordinate
     * @throws IllegalArgumentException If x or y exceed the bitmap's bounds.
     * @throws IllegalStateException If the bitmap has not yet been resolved.
     */
    public Color getPixel (int x, int y)
    {
        if (bitmap == null)
        {
            throw new IllegalStateException("This method cannot be called "
                + "until the bitmap has been resolved.");
        }
        return Color.fromRawColor(bitmap.getPixel(x, y));
    }


    // ----------------------------------------------------------
    /**
     * Returns an array containing all of the Color of each pixel in the
     * image, arranged in row-major order.  All of the pixels in row 0
     * appear in the array first, followed by the pixels in row 1, row 2,
     * and so on.  The array is Width x Height in size.
     *
     * @return An array representing all of the pixels in the image.
     * @throws IllegalStateException If the bitmap has not yet been resolved.
     */
    public Color[] getPixels()
    {
        if (bitmap == null)
        {
            throw new IllegalStateException("This method cannot be called "
                + "until the bitmap has been resolved.");
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int len = width * height;
        int[] pixels = new int[len];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        Color[] result = new Color[len];
        for (int i = 0; i < len; i++)
        {
            result[i] = Color.fromRawColor(pixels[i]);
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Write the specified Color into the bitmap at the x,y coordinate.
     *
     * @param x     The x coordinate of the pixel to replace (0...width-1).
     * @param y     The y coordinate of the pixel to replace (0...height-1).
     * @param color The Color to write into the bitmap.
     * @throws IllegalArgumentException If x, y are outside of the bitmap's
     *         bounds.
     * @throws IllegalStateException If the bitmap has not yet been resolved.
     */
    public void setPixel (int x, int y, Color color)
    {
        if (bitmap == null)
        {
            throw new IllegalStateException("This method cannot be called "
                + "until the bitmap has been resolved.");
        }

        // TODO: deal with copy-on-write if the bitmap is not mutable

        bitmap.setPixel(x, y, color.toRawColor());
    }


    // ----------------------------------------------------------
    /**
     * Replace pixels in the bitmap with the colors in the array.
     *
     * @param pixels  The colors to write to the bitmap.
     * @throws IllegalStateException If the bitmap has not yet been resolved.
     * @throws IllegalArgumentException If the pixels array is the wrong size,
     *         or null.
     */
    public void setPixels (Color[] pixels)
    {
        if (bitmap == null)
        {
            throw new IllegalStateException("This method cannot be called "
                + "until the bitmap has been resolved.");
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int len = width * height;

        if (pixels == null)
        {
            throw new IllegalArgumentException(
                "The pixels parameter cannot be null");
        }
        if (pixels.length != len)
        {
            throw new IllegalArgumentException(
                "The pixels parameter contains " + pixels.length
                + " entries, but this " + width + "x" + height
                + " bitmap has " + len + "pixels");
        }

        int[] rawPixels = new int[len];
        for (int i = 0; i < len; i++)
        {
            rawPixels[i] = pixels[i].toRawColor();
        }
        bitmap.setPixels(rawPixels, 0, width, 0, 0, width, height);
    }


    // TODO: add media computation features here


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private static Bitmap bitmapForBaseName(
        String name, Context context, Class<?> klass)
    {
        Bitmap bm = JarResources.getBitmap(context, klass, name);
        if (bm == null)
        {
            for (String extension : EXTENSIONS)
            {
                bm = JarResources.getBitmap(
                    context, klass, name + extension);
                if (bm != null)
                {
                    break;
                }
            }
        }
        return bm;
    }


    // ----------------------------------------------------------
    private static Bitmap bitmapFor(Context context, Class<?> cls)
    {
        Bitmap bm = null;
        while (bm == null && cls != null)
        {
            bm = bitmapForBaseName(cls.getName(), context, cls);
            if (bm == null)
            {
                bm = bitmapForBaseName(
                    cls.getName().toLowerCase(), context, cls);
            }
            if (bm == null)
            {
                bm = bitmapForBaseName(cls.getName().replace('.', '/'),
                    context, cls);
            }
            if (bm == null)
            {
                bm = bitmapForBaseName(cls.getSimpleName(), context, cls);
            }
            if (bm == null)
            {
                bm = bitmapForBaseName(cls.getSimpleName().toLowerCase(),
                    context, cls);
            }
            cls = cls.getSuperclass();
        }
        return bm;
    }
}
