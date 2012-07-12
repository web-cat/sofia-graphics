/*
 This file is part of the Greenfoot program.
 Copyright (C) 2005-2009  Poul Henriksen and Michael Kolling

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 This file is subject to the Classpath exception as provided in the
 LICENSE.txt file that accompanied this code.
 */
package sofia.graphics.collision;

import sofia.graphics.Shape;

import java.util.*;

/**
 * A node in a BSP tree. Each node covers a rectangular area, and is potentially split
 * down either axis to allow two child nodes. A BSP node area contains one or more
 * Actors (or parts of Actors); in implementation, this is represented as a map of
 * Actor to ActorNode.
 *
 * @author Davin McCall
 */
public final class BSPNode
    implements Iterable<Shape>
{
    private Map<Shape, ShapeNode> shapes;

    private BSPNode parent;
    private Rect area;
    private int splitAxis;  // which axis is split
    private float splitPos;   // where it is split (absolute)
    private BSPNode left;
    private BSPNode right;

    private boolean areaRipple; // area has been set, need to ripple
        // down to children at some stage

    public BSPNode(Rect area, int splitAxis, float splitPos)
    {
        this.area = area;
        this.splitAxis = splitAxis;
        this.splitPos = splitPos;

        // actorNodes = new LinkedList<ActorNode>();
        shapes = new HashMap<Shape, ShapeNode>();
    }

    /**
     * Set the child on either side. The child's area must be set
     * appropriately before calling this.
     */
    public void setChild(int side, BSPNode child)
    {
        if (side == IBSPColChecker.PARENT_LEFT) {
            left = child;
            if (child != null) {
                child.parent = this;
            }
        }
        else {
            right = child;
            if (child != null) {
                child.parent = this;
            }
        }
    }

    public void setArea(Rect area)
    {
        this.area = area;
        areaRipple = true;
    }

    public void setSplitAxis(int axis)
    {
        if (axis != splitAxis) {
            splitAxis = axis;
            areaRipple = true;
        }
    }

    public void setSplitPos(float pos)
    {
        if (pos != splitPos) {
            splitPos = pos;
            areaRipple = true;
        }
    }

    public int getSplitAxis()
    {
        return splitAxis;
    }

    public float getSplitPos()
    {
        return splitPos;
    }

    public Rect getLeftArea()
    {
        if (splitAxis == IBSPColChecker.X_AXIS) {
            return new Rect(area.getX(), area.getY(), splitPos - area.getX(), area.getHeight());
        }
        else {
            return new Rect(area.getX(), area.getY(), area.getWidth(), splitPos - area.getY());
        }
    }

    public Rect getRightArea()
    {
        if (splitAxis == IBSPColChecker.X_AXIS) {
            return new Rect(splitPos, area.getY(), area.getRight() - splitPos, area.getHeight());
        }
        else {
            return new Rect(area.getX(), splitPos, area.getWidth(), area.getTop() - splitPos);
        }
    }

    public Rect getArea()
    {
        return area;
    }

    private void resizeChildren()
    {
        if (left != null) {
            left.setArea(getLeftArea());
        }
        if (right != null) {
            right.setArea(getRightArea());
        }
    }

    public BSPNode getLeft()
    {
        if (areaRipple) {
            resizeChildren();
            areaRipple = false;
        }
        return left;
    }

    public BSPNode getRight()
    {
        if (areaRipple) {
            resizeChildren();
            areaRipple = false;
        }
        return right;
    }

    public BSPNode getParent()
    {
        return parent;
    }

    public void setParent(BSPNode parent)
    {
        this.parent = parent;
    }

    public int getChildSide(BSPNode child)
    {
        if (left == child) {
            return IBSPColChecker.PARENT_LEFT;
        }
        else {
            return IBSPColChecker.PARENT_RIGHT;
        }
    }

    public String toString()
    {
        return "bsp" + hashCode();
    }

    public void addShape(Shape shape)
    {
        shapes.put(shape, new ShapeNode(shape, this));
    }

    /**
     * Check whether the actor is already listed in this node, and
     * mark the ActorNode if this is the case.
     */
    public boolean containsShape(Shape shape)
    {
        ShapeNode anode = shapes.get(shape);
        if (anode != null) {
            anode.mark();
            return true;
        }
        return false;
    }

    public void shapeRemoved(Shape shape)
    {
        shapes.remove(shape);
    }

    public int numberShapes()
    {
        return shapes.size();
    }

    /**
     * Check whether any actors are registered in this node.
     */
    public boolean isEmpty()
    {
        return shapes.isEmpty();
    }

    public Iterator<Shape> iterator()
    {
        return shapes.keySet().iterator();
    }

    public Set<Shape> getShapes()
    {
        return shapes.keySet();
    }
}
