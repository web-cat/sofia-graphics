package sofia.graphics;

import sofia.internal.MRUMap;
import sofia.internal.JarResources;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//-------------------------------------------------------------------------
/**
 * Represents a single bitmapped image, such as one loaded from a file.
 *
 * <p>Images retrieved by name or class are searched for using a robust
 * and flexible search scheme.</p>
 *
 * <p>This class cannot be a subclass of {@link Bitmap}, since Bitmap is
 * a final class.</p>
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:16 $
 */
public class Image
{
    //~ Fields ................................................................

    private Bitmap   bitmap;
    private int      bitmapId;
    private Class<?> klass;
    private String   fileName;
    private boolean  useDefault  = true;
    private boolean  scaleForDpi = true;

    private static Bitmap defaultImage;
    private static MRUMap<String, Bitmap> bitmapCache =
            new MRUMap<String, Bitmap>(256, 60 * 60 * 4,
                    new MRUMap.Recycler<Bitmap>() {
                        @Override
                        public void recycle(Bitmap value)
                        {
                            value.recycle();
                        }
            });


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
     * Create an image from a class.  The image used will be found
     * based on the name of the class.
     *
     * @param klass The Java class after which the file is named.
     */
    public Image(Class<?> klass)
    {
        this.klass = klass;
    }


    // ----------------------------------------------------------
    /**
     * Create an image from a file.  The image will be found by
     * searching for an appropriate match.
     *
     * @param fileName The name of the image file, optionally including
     *                 an extension.
     */
    public Image(String fileName)
    {
        this.fileName = fileName;
    }


    // ----------------------------------------------------------
    /**
     * Create an image that is a duplicate of another image
     * (a copy constructor).
     * @param other The image to copy.
     */
    public Image(Image other)
    {
        bitmap = other.bitmap;
        bitmapId = other.bitmapId;
        klass = other.klass;
        fileName = other.fileName;
        useDefault = other.useDefault;
        scaleForDpi = other.scaleForDpi;
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
     * Determine whether this image will be automatically scaled up or down
     * based on the current device's pixel density when it is loaded.  The
     * default is true.  This setting is only useful before the image is
     * resolved (loaded), since scaling happens at that time.
     *
     * @param willScaleForDpi True if this image should be scaled for
     *                        the current device's pixel density.
     * @see #resolveAgainstContext(Context)
     */
    public void setScaleForDpi(boolean willScaleForDpi)
    {
        this.scaleForDpi = willScaleForDpi;
    }


    // ----------------------------------------------------------
    /**
     * Get whether this image will be automatically scaled up or down
     * based on the current device's pixel density when it is loaded.  The
     * default is true.  This setting is only useful before the image is
     * resolved (loaded), since scaling happens at that time.
     *
     * @return True if this image will be scaled for the current device's
     *         pixel density when it is loaded.
     * @see #resolveAgainstContext(Context)
     */
    public boolean getScaleForDpi()
    {
        return scaleForDpi;
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
        boolean alreadyCached = false;

        if (bitmap != null && bitmap.isRecycled())
        {
            bitmap = null;
        }

        if (bitmap == null)
        {
            bitmap = bitmapCache.get(fileName /*FIXME*/);

            if (bitmap != null)
            {
                alreadyCached = true;
            }
        }

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
                    bitmap = JarResources.getBitmap(
                        context, fileName, true, scaleForDpi, "");
//                    System.out.println(
//                        "fileName " + fileName + " = " + stream);
                }
                else if (klass != null)
                {
                    bitmap = bitmapFor(context, klass, scaleForDpi);
//                    System.out.println(
//                        "class " + klass.getName() + " = " + stream);
                }
            }
        }

        if (bitmap == null && useDefault)
        {
            if (defaultImage == null)
            {
                BitmapFactory.Options bfo = null;
                if (!scaleForDpi)
                {
                    bfo = new BitmapFactory.Options();
                    bfo.inScaled = false;
                }
                // Default to generic logo
                defaultImage = Bitmap.createBitmap(
                        16, 16, Bitmap.Config.ARGB_8888);
                /*defaultImage = BitmapFactory.decodeResource(
                    context.getResources(),
                    R.drawable.sofia_default_image, bfo);*/
//                System.out.println("loading default image = " + bitmap);
            }
            bitmap = defaultImage;
//            System.out.println("bitmap = default image = " + bitmap);
        }

//        if (bitmap != null)
//        {
//            System.out.println("bitmap " + bitmap + " " + bitmap.getWidth()
//                + "x" + bitmap.getHeight() + " density = " +
//                bitmap.getDensity());
//            System.out.println("target density = " +
//                context.getResources().getDisplayMetrics().densityDpi);
//        }

        if (!alreadyCached && bitmap != null)
        {
            System.out.println("Putting " + fileName + " in cache");
            bitmapCache.put(fileName /*FIXME*/, bitmap);
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
    public Color getPixel(int x, int y)
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
    public void setPixel(int x, int y, Color color)
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
    public void setPixels(Color[] pixels)
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
    private static Bitmap bitmapFor(
        Context context, Class<?> cls, boolean scaleForDpi)
    {
        Bitmap bm = null;
        while (bm == null && cls != null)
        {
            if (bm == null)
            {
                bm = JarResources.getBitmap(context,
                    cls.getName().replace('.', '/'), true, scaleForDpi, "");
            }
            if (bm == null)
            {
                bm = JarResources.getBitmap(context,
                    cls.getName().toLowerCase().replace('.', '/'),
                    true, scaleForDpi, "");
            }
            if (bm == null)
            {
                bm = JarResources.getBitmap(context,
                    cls.getSimpleName(), true, scaleForDpi,
                    cls.getPackage().getName(), "");
            }
            if (bm == null)
            {
                bm = JarResources.getBitmap(context,
                    cls.getSimpleName().toLowerCase(), true, scaleForDpi,
                    cls.getPackage().getName(), "");
            }
            cls = cls.getSuperclass();
        }
        return bm;
    }
}
