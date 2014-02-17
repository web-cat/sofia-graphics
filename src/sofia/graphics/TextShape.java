/*
 * Copyright (C) 2011 Virginia Tech Department of Computer Science
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sofia.graphics;

import sofia.graphics.internal.animation.TypeSizeTransformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.TypedValue;

import org.jbox2d.collision.shapes.PolygonShape;

//-------------------------------------------------------------------------
/**
 * A shape that renders a text string on the canvas.
 *
 * @author Tony Allevato
 */
public class TextShape extends Shape
{
    //~ Fields ................................................................

    private PointAndAnchor pointAndAnchor;
    private String text;
    private Typeface typeface;
    private float typeSize;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a {@code TextShape} with the specified text that is positioned
     * so that the top-left corner of the area containing the text is at the
     * specified point.
     *
     * @param text the text to draw in the shape
     * @param origin the point on the view to draw the text
     */
    public TextShape(String text, PointF origin)
    {
        this(text, Anchor.TOP_LEFT.anchoredAt(origin));
    }


    // ----------------------------------------------------------
    /**
     * Creates a {@code TextShape} with the specified text that is positioned
     * so that the top-left corner of the area containing the text is at the
     * specified point.
     *
     * @param text the text to draw in the shape
     * @param origin the point on the view to draw the text
     */
    public TextShape(String text, float x, float y)
    {
        this(text, Anchor.TOP_LEFT.anchoredAt(x, y));
    }


    // ----------------------------------------------------------
    /**
     * Creates a {@code TextShape} with the specified text that is positioned
     * with respect to the specified point and anchor. For example,
     * {@code new TextShape("foo", Anchor.BOTTOM.anchoredAt(point))} would
     * anchor the text so that the bottom-center of the text is at the
     * specified point.
     *
     * @param text the text to draw in the shape
     * @param pointAndAnchor a {@code PointAndAnchor} that determines where the
     *     text should be positioned
     */
    public TextShape(String text, PointAndAnchor pointAndAnchor)
    {
        this.pointAndAnchor = pointAndAnchor;
        this.text = text;
        this.typeface = Typeface.DEFAULT;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public RectF getBounds()
    {
        Rect textBounds = new Rect();
        Paint paint = getPaint();
        paint.getTextBounds(text, 0, text.length(), textBounds);

        PointF pt = pointAndAnchor.getPoint();
        RectF bounds = new RectF(
                pt.x + textBounds.left, pt.y + textBounds.top,
                pt.x + textBounds.right, pt.y + textBounds.bottom);
        pointAndAnchor.getAnchor().getPoint(bounds);

        return bounds;
    }


    // ----------------------------------------------------------
    /**
     * Updates the position of the text shape so that the text would be drawn
     * in the center of the bounding rectangle.
     *
     * @param newBounds the new bounds
     */
    public void setBounds(RectF newBounds)
    {
        pointAndAnchor = Anchor.CENTER.anchoredAt(
                newBounds.centerX(), newBounds.centerY());
    }


    // ----------------------------------------------------------
    @Override @SuppressWarnings("rawtypes")
    public Animator<?> animate(long duration)
    {
        return new Animator(duration);
    }


    // ----------------------------------------------------------
    /**
     * Gets the text drawn by this shape.
     *
     * @return the text drawn by this shape
     */
    public String getText()
    {
        return text;
    }


    // ----------------------------------------------------------
    /**
     * Sets the text drawn by this shape.
     *
     * @param text the text to be drawn by this shape
     */
    public void setText(String text)
    {
        this.text = text;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Gets the typeface used to render the text in this shape. The typeface
     * indicates the font family (such as "Droid Sans") and whether or not the
     * type is bold and/or italicized.
     *
     * @return the {@link Typeface} used to render the text in this shape
     */
    public Typeface getTypeface()
    {
        return typeface;
    }


    // ----------------------------------------------------------
    /**
     * Sets the typeface used to render the text in this shape. The typeface
     * indicates the font family (such as "Droid Sans") and whether or not the
     * type is bold and/or italicized.
     *
     * @param typeface the {@link Typeface} used to render the text in this
     *     shape
     */
    public void setTypeface(Typeface typeface)
    {
        this.typeface = typeface;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Sets the typeface and size of the text in this shape using a shorthand
     * string to describe these properties.
     * </p><p>
     * The string passed to this method should be in the format
     * {@code "typeface-style-size"}, where:
     * </p>
     * <ul>
     * <li>{@code typeface} is the name of the typeface (such as
     * {@code "DroidSans"})</li>
     * <li>{@code style} is one of the following: {@code plain} or
     * {@code normal} for normal text, {@code bold} for bold text,
     * {@code italic} for italicized text, or {@code bolditalic} for both
     * bold and italicized text</li>
     * <li>{@code size} is the size of the text, in points</li>
     * </ul>
     * <p>
     * Any of the three parts of this string can be replaced with a {@code "*"}
     * wildcard, which means retain its original value while changing the
     * others.
     * </p>
     *
     * @param typefaceAndSize a string describing the typeface and text size
     */
    public void setTypefaceAndSize(String typefaceAndSize)
    {
        String family = null;
        float textSize = 0;
        int style = Typeface.NORMAL;

        String[] parts = typefaceAndSize.split("-");

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
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Gets the point size of the text in the shape.
     *
     * @return the point size of the text in the shape
     */
    public float getTypeSize()
    {
        if (typeSize == 0)
        {
            typeSize = getPaint().getTextSize();
        }

        return typeSize;
    }


    // ----------------------------------------------------------
    /**
     * Sets the point size of the text in the shape.
     *
     * @param typeSize the new point size of the text in the shape
     */
    public void setTypeSize(float typeSize)
    {
        this.typeSize = typeSize;
        conditionallyRepaint();
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
    public void draw(Drawing drawing)
    {
        if (text != null)
        {
            Canvas canvas = drawing.getCanvas();

            Paint paint = getPaint();
            canvas.drawText(text,
                getBounds().left, getBounds().top - getAscent(), paint);
        }
    }


    // ----------------------------------------------------------
    @Override
    protected void createFixtures()
    {
        PolygonShape box = new PolygonShape();
        RectF bounds = getBounds();
        box.setAsBox(
                Math.abs(bounds.width() / 2), Math.abs(bounds.height() / 2));

        addFixtureForShape(box);
    }


    //~ Animation support classes .............................................

    // ----------------------------------------------------------
    /**
     * Provides animation support for shapes. Most uses of this class will not
     * need to reference it directly; for example, an animation can be
     * constructed and played by chaining method calls directly:
     *
     * <pre>
     *     shape.animate(500).color(Color.BLUE).alpha(128).play();</pre>
     *
     * In situations where the type of the class must be referenced directly
     * (for example, when one is passed to an event handler like
     * {@code onAnimationDone}), referring to the name of that type can be
     * somewhat awkward due to the use of some Java generics tricks to ensure
     * that the methods chain properly. In nearly all cases, it is reasonable
     * to use a "?" wildcard in place of the generic parameter:
     *
     * <pre>
     *     Shape.Animator&lt;?&gt; anim = shape.animate(500).color(Color.BLUE);
     *     anim.play();</pre>
     *
     * @param <AnimatorType> the concrete type of the animator
     *
     * @author  Tony Allevato
     * @version 2011.12.11
     */
    public class Animator<
        AnimatorType extends TextShape.Animator<AnimatorType>>
        extends Shape.Animator<AnimatorType>
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
        public TextShape getShape()
        {
            return TextShape.this;
        }


        // ----------------------------------------------------------
        /**
         * Sets the final type size of the shape when the animation ends.
         *
         * @param typeSize the final type size of the shape when the animation
         *     ends
         * @return this animator, for method chaining
         */
        @SuppressWarnings("unchecked")
        public AnimatorType typeSize(double typeSize)
        {
            addTransformer(new TypeSizeTransformer(getShape(), typeSize));
            return (AnimatorType) this;
        }
    }
}
