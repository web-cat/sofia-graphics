package sofia.graphics;

import java.util.Collections;
import java.util.Set;

import sofia.graphics.internal.animation.TypeSizeTransformer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;

public class TextShape extends Shape
{
    private String text;
    private Typeface typeface;
    private float typeSize;


    // ----------------------------------------------------------
    public TextShape(String text)
    {
        this(text, Anchor.CENTER.anchoredAt(Anchor.CENTER.ofView()));
    }


    // ----------------------------------------------------------
    public TextShape(String text, PointF origin)
    {
        this(text, Anchor.TOP_LEFT.anchoredAt(origin));
    }


    // ----------------------------------------------------------
    public TextShape(String text, PointAndAnchor pointAndAnchor)
    {
        setBounds(pointAndAnchor.sized(new TextShapeSize()));

        this.text = text;
        this.typeface = Typeface.DEFAULT;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override @SuppressWarnings("rawtypes")
    public Animator<?> animate(long duration)
    {
        return new Animator(duration);
    }


    // ----------------------------------------------------------
    public String getText()
    {
        return text;
    }


    // ----------------------------------------------------------
    public void setText(String text)
    {
        this.text = text;
        conditionallyRelayout();
    }


    // ----------------------------------------------------------
    public Typeface getTypeface()
    {
        return typeface;
    }


    // ----------------------------------------------------------
    public void setTypeface(Typeface typeface)
    {
        this.typeface = typeface;
        conditionallyRelayout();
    }


    // ----------------------------------------------------------
    public void setTypeface(String typeface)
    {
        String family = null;
        float textSize = 0;
        int style = Typeface.NORMAL;

        String[] parts = typeface.split("-");

        if (parts.length > 0)
        {
            if ("*".equals(parts[0]))
            {
                family = null;
            }
            else
            {
                family = parts[0];
            }
        }

        if (parts.length > 1)
        {
            if ("*".equals(parts[1]))
            {
                style = getTypeface().getStyle();
            }
            else if ("plain".equalsIgnoreCase(parts[1]) ||
                "normal".equalsIgnoreCase(parts[1]))
            {
                style = Typeface.NORMAL;
            }
            else if ("bold".equalsIgnoreCase(parts[1]))
            {
                style = Typeface.BOLD;
            }
            else if ("italic".equalsIgnoreCase(parts[1]))
            {
                style = Typeface.ITALIC;
            }
            else if ("bolditalic".equalsIgnoreCase(parts[1]))
            {
                style = Typeface.BOLD_ITALIC;
            }
            else
            {
                throw new IllegalArgumentException(
                    "'" + parts[1] + "' is not a valid typeface style.");
            }
        }
        else
        {
            style = getTypeface().getStyle();
        }

        if (parts.length > 2)
        {
            if ("*".equals(parts[2]))
            {
                textSize = getTypeSize();
            }
            else
            {
                try
                {
                    textSize = Float.parseFloat(parts[2]);
                }
                catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException(
                        "'" + parts[2] + "' is not a valid typeface size.");
                }
            }
        }
        else
        {
            textSize = getTypeSize();
        }

        Typeface newTypeface;

        if (family == null)
        {
            newTypeface = Typeface.create(getTypeface(), style);
        }
        else
        {
            newTypeface = Typeface.create(family, style);
        }

        this.typeface = newTypeface;
        this.typeSize = textSize;
        conditionallyRelayout();
    }


    // ----------------------------------------------------------
    public float getTypeSize()
    {
        if (typeSize == 0)
        {
            typeSize = getPaint().getTextSize();
        }

        return typeSize;
    }


    // ----------------------------------------------------------
    public void setTypeSize(float typeSize)
    {
        this.typeSize = typeSize;
        conditionallyRelayout();
    }


    // ----------------------------------------------------------
    public float getAscent()
    {
        return getPaint().ascent();
    }


    // ----------------------------------------------------------
    public float getDescent()
    {
        return getPaint().descent();
    }


    // ----------------------------------------------------------
    @Override
    protected Paint getPaint()
    {
        Paint paint = super.getPaint();
        paint.setTypeface(getTypeface());

        if (typeSize != 0)
        {
            float pointSize = getTypeSize();

            Context c = (getParentView() == null) ?
                null : getParentView().getContext();
            Resources r;

            if (c == null)
            {
                r = Resources.getSystem();
            }
            else
            {
                r = c.getResources();
            }

            paint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PT, pointSize, r.getDisplayMetrics()));
        }
        paint.setAntiAlias(true);
        return paint;
    }


    // ----------------------------------------------------------
    @Override
    public void draw(Canvas canvas)
    {
        if (text != null)
        {
            Paint paint = getPaint();
            canvas.drawText(text,
                getBounds().left, getBounds().top - getAscent(), paint);
        }
    }


    // ----------------------------------------------------------
    private class TextShapeSize
        extends SizeF
        implements ResolvableGeometry<TextShapeSize>
    {
        // ----------------------------------------------------------
        public TextShapeSize()
        {
            super(Float.NaN, Float.NaN);
        }


        // ----------------------------------------------------------
        public TextShapeSize copy()
        {
            return this;
        }


        // ----------------------------------------------------------
        public void resolveGeometry(Shape shape)
        {
            Rect bounds = new Rect();

            if (text != null)
            {
                getPaint().getTextBounds(text, 0, text.length(), bounds);
            }

            this.width = bounds.width();
            this.height = bounds.height();
        }


        // ----------------------------------------------------------
        public boolean isGeometryResolved()
        {
            return false;
        }


        // ----------------------------------------------------------
        public Set<Shape> getShapeDependencies()
        {
            return Collections.<Shape>emptySet();
        }
    }


    //~ Animation support classes .............................................

    // ----------------------------------------------------------
    public class Animator<
	    ConcreteType extends TextShape.Animator<ConcreteType>>
	    extends Shape.Animator<ConcreteType>
	{
	    //~ Constructors ......................................................
	
	    // ----------------------------------------------------------
	    public Animator(long duration)
	    {
	        super(duration);
	    }
	
	
	    //~ Methods ...........................................................

	    // ----------------------------------------------------------
	    @Override
	    public TextShape getShape()
	    {
	    	return TextShape.this;
	    }


	    // ----------------------------------------------------------
	    @SuppressWarnings("unchecked")
	    public ConcreteType typeSize(double typeSize)
	    {
	        addTransformer(new TypeSizeTransformer(getShape(), typeSize));
	        return (ConcreteType)this;
	    }
	}
}
