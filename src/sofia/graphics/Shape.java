package sofia.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import sofia.graphics.animation.ShapeAnimator;
import sofia.graphics.internal.GeometryUtils;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here.
 * Follow it with additional details about its purpose, what abstraction
 * it represents, and how to use it.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:32 $
 */
public abstract class Shape
{
    //~ Instance/static variables .............................................

    private RectF bounds;
    private int zIndex;
    private ShapeParent parent;
    private boolean visible;
    private Color color;
    private float rotation;
    private PointF rotationPivot;
    private PointF positionAnchor;
    private Matrix transform;
    private Matrix inverseTransform;
    private float[][] rotatedCorners;

    // Collision checker hook, for future location-based query extensions
    @SuppressWarnings("unused")
    private sofia.graphics.collision.CollisionChecker collisionChecker;
    private sofia.graphics.collision.ShapeNode node;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new shape with default bounds (located at the center of the
     * screen and 100 pixels wide by 100 pixels tall).
     */
    public Shape()
    {
        this(Anchor.CENTER.anchoredAt(Anchor.CENTER.ofView()).sized(100, 100));
    }


    // ----------------------------------------------------------
    /**
     * Creates a new shape with the specified bounds.
     *
     * @param bounds The bounds of the shape.
     */
    public Shape(RectF bounds)
    {
        this.zIndex = 0;
        this.bounds = GeometryUtils.copy(bounds);
        this.visible = true;
        this.color = Color.white;
        this.positionAnchor = new PointF(0, 0);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Add another shape to the same view (or parent) containing this shape.
     * @param newShape The other shape to add.
     */
    public void addOther(Shape newShape)
    {
    	ShapeParent parent = getShapeParent();

    	if (parent != null)
    	{
    		parent.add(newShape);
    	}
    }


    // ----------------------------------------------------------
    /**
     * Remove this shape from its view (or parent).
     */
    public void remove()
    {
    	ShapeParent parent = getShapeParent();

    	if (parent != null)
    	{
    		parent.remove(this);
    	}
    }


    // ----------------------------------------------------------
    /**
     * Gets the bounding rectangle of the shape. The top-left corner of the
     * bounding rectangle is the shape's origin, and the bottom-right corner
     * is the shape's extent.
     *
     * @return The bounding rectangle of the shape.
     */
    public RectF getBounds()
    {
        return bounds;
    }


    // ----------------------------------------------------------
    /**
     * Sets the bounding rectangle of the shape.
     *
     * @param newBounds The new bounding rectangle of the shape.
     */
    public void setBounds(RectF newBounds)
    {
        this.bounds = GeometryUtils.copy(newBounds);

        if (getShapeParent() != null)
        {
            GeometryUtils.resolveGeometry(this.bounds, this);
        }

        notifyParentOfPositionChange();
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Get the current position anchor, which is an offset relative to the
     * upper left corner of the shape that is used as the shape's "origin"
     * for the purposes of getting/setting x-y positions.  The default
     * anchor is (0, 0) unless it has been explicitly set.
     * @return The current position anchor.
     */
    public PointF getPositionAnchor()
    {
        return positionAnchor;
    }


    // ----------------------------------------------------------
    /**
     * Set the position anchor, which is an offset relative to the
     * upper left corner of the shape that is used as the shape's "origin"
     * for the purposes of getting/setting x-y positions.  This does not
     * change the shape's current position, but will change the behavior
     * of future calls to setX()/setY()/setPosition() and
     * getX()/getY()/getPosition().
     * @param anchor The new position anchor.
     */
    public void setPositionAnchor(PointF anchor)
    {
        positionAnchor = new PointF(anchor.x, anchor.y);
    }


    // ----------------------------------------------------------
    /**
     * Set the position anchor, which is an offset relative to the
     * upper left corner of the shape that is used as the shape's "origin"
     * for the purposes of getting/setting x-y positions.  This does not
     * change the shape's current position, but will change the behavior
     * of future calls to setX()/setY()/setPosition() and
     * getX()/getY()/getPosition().
     * @param anchor The new position anchor.
     */
    public void setPositionAnchor(Anchor anchor)
    {
        positionAnchor = anchor.getPoint(getBounds());
        positionAnchor.offset(-getBounds().left, -getBounds().top);
    }


    // ----------------------------------------------------------
    /**
     * Gets the current angle of rotation of the shape, in degrees clockwise.
     *
     * @return The current angle of rotation of the shape.
     */
    public float getRotation()
    {
        return rotation;
    }


    // ----------------------------------------------------------
    /**
     * Gets the point around which the shape's rotation will pivot. By
     * default, the center of the shape's bounding box is used.
     *
     * @return The point around which the shape's rotation will pivot.
     */
    public PointF getRotationPivot()
    {
        return rotationPivot;
    }


    // ----------------------------------------------------------
    /**
     * Sets the angle of rotation of the shape in degrees clockwise, using
     * the center of the shape's bounding box as the pivot point.
     *
     * @param newRotation The new angle of rotation of the shape.
     */
    public void setRotation(float newRotation)
    {
        setRotation(newRotation, Anchor.CENTER.of(this));
    }


    // ----------------------------------------------------------
    /**
     * Sets the angle of rotation of the shape in degrees clockwise, using
     * the specified point as the pivot point.
     *
     * @param newRotation The new angle of rotation of the shape.
     * @param newPivot    The point around which the rotation will pivot.
     */
    public void setRotation(float newRotation, PointF newPivot)
    {
        this.rotation = newRotation;
        this.rotationPivot = newPivot;

        updateTransform();
        notifyParentOfPositionChange();
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Increments the shape's rotation by the specified number of degrees,
     * around the same pivot point that was used previously (or the center
     * of the shape if no other pivot has been previously used).
     *
     * @param angleDelta The number of degrees to add to the shape's rotation.
     */
    public void rotateBy(float angleDelta)
    {
        this.rotation += angleDelta;

        updateTransform();
        notifyParentOfPositionChange();
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    private void updateTransform()
    {
        if (rotation == 0)
        {
            transform = null;
        }
        else
        {
            transform = new Matrix();

            PointF pivot;

            if (rotationPivot == null)
            {
                pivot = Anchor.CENTER.of(this);
            }
            else
            {
                pivot = rotationPivot;
            }

            GeometryUtils.resolveGeometry(pivot, this);
            transform.postRotate(rotation, pivot.x, pivot.y);
        }
        inverseTransform = null;
        rotatedCorners = null;
    }


    // ----------------------------------------------------------
    /**
     * Gets the current linear transformation that will be applied to the
     * shape when it is drawn. Currently, the matrix only contains rotation
     * information.
     *
     * @return The current linear transformation that is applied to the shape.
     */
    public Matrix getTransform()
    {
        return transform;
    }


    // ----------------------------------------------------------
    /**
     * Gets the x-coordinate of the anchor point of the shape's bounding
     * box (the top-left corner, by default).
     *
     * @return The x-coordinate of the anchor point of the shape's
     *         bounding box.
     * @see #setPositionAnchor(Anchor)
     */
    public float getX()
    {
        return getBounds().left + positionAnchor.x;
    }


    // ----------------------------------------------------------
    /**
     * Sets the x-coordinate of the anchor point of the shape's bounding
     * box (the top-left corner, by default). This moves the shape, so
     * calling this method also causes the extent of the shape to change,
     * keeping with width the same.
     *
     * @param x The x-coordinate of the anchor point of the shape's
     *          bounding box.
     * @see #setPositionAnchor(Anchor)
     */
    public void setX(float x)
    {
        getBounds().offsetTo(x - positionAnchor.x, getBounds().top);
        notifyParentOfPositionChange();
        conditionallyRelayout();
    }


    // ----------------------------------------------------------
    /**
     * Gets the y-coordinate of the anchor point of the shape's bounding
     * box (the top-left corner, by default).
     *
     * @return The y-coordinate of the anchor point of the shape's
     *         bounding box.
     * @see #setPositionAnchor(Anchor)
     */
    public float getY()
    {
        return getBounds().top + positionAnchor.y;
    }


    // ----------------------------------------------------------
    /**
     * Sets the y-coordinate of the anchor point of the shape's bounding
     * box (the top-left corner, by default). This moves the shape, so
     * calling this method also causes the extent of the shape to change,
     * keeping with height the same.
     *
     * @param y The y-coordinate of the anchor point of the shape's
     *          bounding box.
     * @see #setPositionAnchor(Anchor)
     */
    public void setY(float y)
    {
        getBounds().offsetTo(getBounds().left, y - positionAnchor.y);
        notifyParentOfPositionChange();
        conditionallyRelayout();
    }


    // ----------------------------------------------------------
    /**
     * Gets the width of the shape, in pixels.
     *
     * @return The width of the shape.
     */
    public float getWidth()
    {
        return getBounds().width();
    }


    // ----------------------------------------------------------
    /**
     * Gets the height of the shape, in pixels.
     *
     * @return The height of the shape.
     */
    public float getHeight()
    {
        return getBounds().height();
    }


    // ----------------------------------------------------------
    /**
     * Gets the origin (top-left corner) of the receiver. Be aware that the
     * {@link PointF#x} and {@link PointF#y} fields of the returned point may
     * not be valid if layout of the shapes has not yet occurred.
     *
     * @return A {@link PointF} object describing the origin of the shape.
     */
    public PointF getPosition()
    {
        return new PointF(
            bounds.left + positionAnchor.x,
            bounds.top + positionAnchor.y);
    }


    // ----------------------------------------------------------
    /**
     * Sets the origin (top-left corner) of the receiver.
     *
     * @param position A {@link PointF} object describing the origin of the
     *                 shape.
     */
    public void setPosition(PointF position)
    {
        bounds.offsetTo(
            position.x - positionAnchor.x,
            position.y - positionAnchor.y);
        notifyParentOfPositionChange();
        conditionallyRelayout();
    }


    // ----------------------------------------------------------
    /**
     * Sets the position of the receiver based on the specified point and
     * anchor, leaving its size unchanged.
     *
     * @param pointAndAnchor A {@link PointAndAnchor} object describing the
     *                       position of the shape.
     */
    public void setPosition(PointAndAnchor pointAndAnchor)
    {
        bounds = pointAndAnchor.sized(bounds.width(), bounds.height());
        bounds.offset(-positionAnchor.x, -positionAnchor.y);
        notifyParentOfPositionChange();
        conditionallyRelayout();
    }


    // ----------------------------------------------------------
    /**
     * Moves the receiver by the specified horizontal and vertical distance.
     * Positive values move the shape to the right or down, and negative
     * values move it to the left or up.
     *
     * @param dx The number of pixels to move the shape horizontally.
     * @param dy The number of pixels to move the shape vertically.
     */
    public void move(float dx, float dy)
    {
        bounds.offset(dx, dy);
        notifyParentOfPositionChange();
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * Transforms a point on the screen into the original bounds of a shape,
     * pre-rotation. This method is mainly meant to be used by subclasses
     * that need to provide their own {@link #contains(float, float)}
     * implementation, so that those methods return the correct values when
     * a rotation is applied to the shape.
     *
     * @param x The x-coordinate in the view.
     * @param y The y-coordinate in the view.
     * @return A two-element float array that contains the x- and
     *     y-coordinates that the inputs would map to before the rotation
     *     was applied.
     */
    protected float[] inverseTransformPoint(float x, float y)
    {
        float[] point = { x, y };
        Matrix xform = getTransform();

        if (xform != null)
        {
            if (inverseTransform == null)
            {
                inverseTransform = new Matrix();
                xform.invert(inverseTransform);
            }
            inverseTransform.mapPoints(point);
        }

        return point;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Gets a value indicating whether the specified pixel location is
     * contained in the receiver.
     * </p><p>
     * By default, this method checks to see whether the point is located
     * within the bounding rectangle of the shape. Shapes where this would
     * produce incorrect results, such as ovals or lines, override this method
     * accordingly.
     * </p><p>
     * This method <strong>does</strong> take the shape's rotation into
     * account. This means that if you subclass {@code Shape} and need to
     * provide logic that is different from the default bounding box behavior,
     * then you may need to undo the rotation of the incoming point before
     * testing it against your shape's bounds. The
     * {@link #inverseTransformPoint(float, float)} method has been provided
     * to simplify this.
     * </p>
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if the shape contains the point, otherwise false.
     */
    public boolean contains(float x, float y)
    {
        float[] point = inverseTransformPoint(x, y);
        return getBounds().contains(point[0], point[1]);
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the specified pixel location is
     * contained in the receiver.
     *
     * @param point The point.
     * @return True if the shape contains the point, otherwise false.
     */
    public boolean contains(PointF point)
    {
        return contains(point.x, point.y);
    }


    // ----------------------------------------------------------
    /**
     * Called when the bounds of the shape have been resolved. Subclasses may
     * want to override this if they need to update their state when this
     * occurs.
     */
    public void onBoundsResolved()
    {
        updateTransform();
    }


    // ----------------------------------------------------------
    /**
     * Gets the z-index of the receiver. A shape with a higher z-index will be
     * drawn on top of a shape with a lower z-index. By default, shapes are
     * created with a z-index of 0.
     *
     * @return The z-index of the shape.
     */
    public int getZIndex()
    {
        return zIndex;
    }


    // ----------------------------------------------------------
    /**
     * Sets the z-index of the receiver.
     *
     * @param newZIndex The new z-index of the shape.
     */
    public void setZIndex(int newZIndex)
    {
        zIndex = newZIndex;

        if (parent != null)
        {
            parent.onZIndexChanged(this);
        }
    }


    // ----------------------------------------------------------
    /**
     * Returns true if this shape is drawn in front of (later than) the
     * other shape.
     * @param other The shape to check against.
     * @return True if this shape is drawn in front of (later than) the other.
     */
    public boolean isInFrontOf(Shape other)
    {
        return parent != null && parent.isInFrontOf(this, other);
    }


    // ----------------------------------------------------------
    /**
     * Gets the parent of the receiver.
     *
     * @return The parent of the receiver.
     */
    public final ShapeParent getShapeParent()
    {
        return parent;
    }


    // ----------------------------------------------------------
    /**
     * Gets the parent of the receiver.
     *
     * @return The parent of the receiver.
     */
    public final ShapeView getParentView()
    {
        ShapeParent ancestor = getShapeParent();

        while (ancestor != null)
        {
            if (ancestor instanceof ShapeView)
            {
                return (ShapeView) ancestor;
            }

            ancestor = ancestor.getShapeParent();
        }

        return null;
    }


    // ----------------------------------------------------------
    /**
     * Sets the parent of the receiver.
     *
     * @param newParent The new parent.
     */
    final void setParent(ShapeParent newParent)
    {
        this.parent = newParent;
    }


    // ----------------------------------------------------------
    /**
     * Gets the color of the receiver.
     *
     * @return The color of the receiver.
     */
    public Color getColor()
    {
        return color;
    }


    // ----------------------------------------------------------
    /**
     * Sets the color of the receiver.
     *
     * @param newColor The new color of the receiver.
     */
    public void setColor(Color newColor)
    {
        this.color = newColor;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * A convenience method that gets the alpha (opacity) component of the
     * shape's color.
     *
     * @return The alpha component of the shape's color, where 0 means that
     *         the color is fully transparent and 255 means that it is fully
     *         opaque.
     */
    public int getAlpha()
    {
        return getColor().alpha();
    }


    // ----------------------------------------------------------
    /**
     * A convenience method that sets the alpha (opacity) component of the
     * shape's color without changing the other color components.
     *
     * @param newAlpha The new alpha component of the shape's color, where 0
     *                 means that the color is fully transparent and 255
     *                 means that it is fully opaque.
     */
    public void setAlpha(int newAlpha)
    {
    	setColor(getColor().withAlpha(newAlpha));
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiver is visible (drawn on the
     * screen). Invisible shapes also do not receive touch events.
     *
     * @return True if the shape is visible, otherwise false.
     */
    public boolean isVisible()
    {
        return visible;
    }


    // ----------------------------------------------------------
    /**
     * Sets a value indicating whether the receiver is visible (drawn on the
     * screen).
     *
     * @param newVisible True if the shape should be visible, otherwise false.
     */
    public void setVisible(boolean newVisible)
    {
        this.visible = newVisible;
        conditionallyRepaint();
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Gets an <em>animator</em> object that lets the user animate properties
     * of the receiving shape.
     * </p><p>
     * For ease of use, the description of the animation desired can be
     * chained directly to the result of this method. For example, the
     * following code fragment would create an animation that runs for 2
     * seconds, gradually changing the shape's color to red, its position
     * to the top-right corner of the view, and then starts the animation
     * after a delay of 1 second after the method is called:
     * <pre>
     *     shape.animate(2000)
     *          .delay(1000)
     *          .color(Color.RED)
     *          .position(Anchor.TOP_RIGHT.ofView())
     *          .play();
     * </pre>
     * </p>
     *
     * @param duration The length of the animation in milliseconds.
     * @return A {@link ShapeAnimator} that lets the user animate properties
     *         of the receiving shape.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ShapeAnimator animate(long duration)
    {
        return new ShapeAnimator(this, duration);
    }


    // ----------------------------------------------------------
    /**
     * Stops the current animation for this shape, if there is one.
     */
    public void stopAnimation()
    {
        ShapeView view = getParentView();

        if (view != null)
        {
            view.getAnimationManager().stop(this);
        }
    }


    // ----------------------------------------------------------
    /**
     * Draws the receiver on the canvas.
     *
     * @param canvas The Canvas on which to draw the shape.
     */
    public abstract void draw(Canvas canvas);


    // ----------------------------------------------------------
    /**
     * Gets the {@code Paint} object that describes how this shape should be
     * drawn. By default, the {@code Paint}'s style is set to {@code STROKE}
     * and the color to the value returned by {@link #getColor()}. Subclasses
     * can override this method to add their own attributes; they should call
     * the superclass implementation and then add their own styles to the
     * returned object.
     *
     * @return A Paint object describing how the shape should be drawn.
     */
    protected Paint getPaint()
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getColor().toRawColor());
        return paint;
    }


    // ----------------------------------------------------------
    /**
     * Called to indicate that the shape needs to be repainted on the screen.
     * Most users will not need to call this method, because modifying a
     * property of the shape such as its color will repaint it automatically.
     */
    protected void conditionallyRepaint()
    {
        ShapeView view = getParentView();

        if (view != null)
        {
            view.conditionallyRepaint();
        }
    }


    // ----------------------------------------------------------
    /**
     * Called to recalculate the layout of the shape on the screen. Most users
     * will not need to call this method, because modifying a property of the
     * shape such as its position and size will cause the layout to be
     * recalculated automatically.
     */
    protected void conditionallyRelayout()
    {
        ShapeView view = getParentView();

        if (view != null)
        {
            view.conditionallyRelayout();
        }
    }


    // ----------------------------------------------------------
    /**
     * Called to recalculate the layout of the shape on the screen. Most users
     * will not need to call this method, because modifying a property of the
     * shape such as its position and size will cause the layout to be
     * recalculated automatically.
     */
    protected void notifyParentOfPositionChange()
    {
        rotatedCorners = null;
        ShapeView view = getParentView();

        if (view != null)
        {
            view.onPositionChanged(this);
        }
    }


    // ----------------------------------------------------------
    /**
     * Returns a human-readable string representation of the shape.
     *
     * @return A human-readable string representation of the shape.
     */
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + ": "
            + "bounds=" + bounds + ", isVisible=" + visible
            + ", color=" + color;
    }


    // ----------------------------------------------------------
    private void updateRotatedCorners()
    {
        if (rotatedCorners == null)
        {
            RectF bounds = getBounds();
            rotatedCorners = new float[][] {
                { bounds.left,  bounds.top    },
                { bounds.right, bounds.top    },
                { bounds.left,  bounds.bottom },
                { bounds.right, bounds.bottom }
            };

            Matrix xform = getTransform();
            if (xform != null)
            {
                for (float[] pt : rotatedCorners)
                {
                    xform.mapPoints(pt);
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Check whether all of the vertexes in the "other" rotated rectangle
     * are on the outside of any one of the edges in "my" rotated rectangle.
     *
     * @param myBox    The rotated corners of "my" bounding box.
     * @param otherBox The rotated corners of the "other" bounding box.
     *
     * @return True if all corners of the "other" rectangle are on the
     *         outside of any of  the edges of "my" rectangle.
     */
    private static boolean checkOutside(float [][] myBox, float[][] otherBox)
    {
        vloop:
        for (int v = 0; v < 4; v++)
        {
            int v1 = (v + 1) & 3; // wrap at 4 back to 0
            float edgeX = myBox[v][0] - myBox[v1][0];
            float edgeY = myBox[v][1] - myBox[v1][1];
            float reX = -edgeY;
            float reY = edgeX;

            if (reX == 0.0 && reY == 0.0)
            {
                continue vloop;
            }

            for (int e = 0; e < 4; e++)
            {
                float scalar = reX * (otherBox[e][0] - myBox[v1][0])
                    + reY * (otherBox[e][1] - myBox[v1][1]);
                if (scalar < 0)
                {
                    continue vloop;
                }
            }

            // If we got here, we have an edge with all vertexes from the
            // other rect on the outside:
            return true;
        }

        return false;
    }


    // ----------------------------------------------------------
    /**
     * Determine whether this shape intersects another, based on their
     * bounding boxes.
     * @param otherShape The other shape to check against.
     * @return True if this shape and the other shape intersect.
     */
    public boolean intersects(Shape otherShape)
    {
        if (rotation == 0.0 && otherShape.rotation == 0.0)
        {
            return RectF.intersects(getBounds(), otherShape.getBounds());
        }
        else
        {
            updateRotatedCorners();
            otherShape.updateRotatedCorners();
            if (checkOutside(rotatedCorners, otherShape.rotatedCorners))
            {
                return false;
            }
            if (checkOutside(otherShape.rotatedCorners, rotatedCorners))
            {
                return false;
            }
            return true;
        }
    }


    // ----------------------------------------------------------
    /**
     * Determine whether any part of this shape extends outside the given
     * rectangle.
     * @param bounds The rectangle to check against.
     * @return A ViewEdges object indicating whether any part of this shape
     * extends outside the top, bottom, left, or right side of the bounds.
     */
    public ViewEdges extendsOutside(RectF bounds)
    {
        boolean left   = false;
        boolean top    = false;
        boolean right  = false;
        boolean bottom = false;

        updateRotatedCorners();
        for (int i = 0; i < 4; i++)
        {
            left   = left   || (rotatedCorners[i][0] <  bounds.left  );
            top    = top    || (rotatedCorners[i][1] <  bounds.top   );
            right  = right  || (rotatedCorners[i][0] >= bounds.right );
            bottom = bottom || (rotatedCorners[i][1] >= bounds.bottom);
        }

        return new ViewEdges(left, top, right, bottom);
    }


    // ----------------------------------------------------------
    /* package */ sofia.graphics.collision.ShapeNode getShapeNode()
    {
        return node;
    }


    // ----------------------------------------------------------
    /* package */ void setShapeNode(sofia.graphics.collision.ShapeNode node)
    {
        this.node = node;
    }


    // ----------------------------------------------------------
    /* package */ void setCollisionChecker(
        sofia.graphics.collision.CollisionChecker collisionChecker)
    {
        this.collisionChecker = collisionChecker;
    }
}
