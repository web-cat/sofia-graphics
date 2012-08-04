package sofia.graphics;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

//-------------------------------------------------------------------------
/**
 * A shape that displays an image loaded from an SVG {@code R.raw} resource.
 * 
 * @author  Tony Allevato
 * @author  Last changed by $Author$
 * @version $Date$
 */
public class SVGShape extends DrawableShape
{
	//~ Instance/static variables .............................................

	private static SparseArray<Drawable> svgCache =
			new SparseArray<Drawable>();

	private int rawResourceId;


	//~ Constructors ..........................................................

    // ----------------------------------------------------------
	/**
	 * Creates a new {@code SVGShape} with the specified SVG resource and
	 * bounds.
	 * 
	 * @param rawResourceId the {@code R.raw} resource that contains the SVG
	 *     data
	 * @param bounds the bounding rectangle
	 */
	public SVGShape(int rawResourceId, RectF bounds)
	{
		super(null, bounds);
		
		this.rawResourceId = rawResourceId;
	}
	
	
	//~ Methods ...............................................................

    // ----------------------------------------------------------
	@Override
	public void draw(Canvas canvas)
	{
        if (getDrawable() == null && rawResourceId != 0)
        {
            loadDrawableFromResource();
        }

        Drawable drawable = getDrawable();

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();

		float xratio = getWidth() / width;
		float yratio = getHeight() / height;

		canvas.save();
		canvas.translate(getX(), getY());
		canvas.scale(xratio, yratio);

		drawable.draw(canvas);

		canvas.restore();
	}
	
	
    // ----------------------------------------------------------
    private void loadDrawableFromResource()
    {
    	Drawable cached = svgCache.get(rawResourceId);

    	if (cached == null)
    	{
            Resources res = getParentView().getResources();
            SVG svg = SVGParser.getSVGFromResource(res, rawResourceId);
            cached = svg.createPictureDrawable();

            RectF limits = svg.getLimits();
            Rect bounds = new Rect();
            bounds.left = 0;
            bounds.top = 0;
            bounds.right = (int) limits.width();
            bounds.bottom = (int) limits.height();
            cached.setBounds(bounds);
            
            svgCache.put(rawResourceId, cached);
    	}

        setDrawable(cached);
    }
}
