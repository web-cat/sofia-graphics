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

/**
 * An ActorNode represents a piece (or whole) of an Actor within the IBSP collision
 * checking tree. Because an actor can be split over several tree nodes, it may be
 * represented by several ActorNodes, which are linked together in a linked list.
 *
 * @author Davin McCall
 */
public final class ShapeNode
{
    private Shape shape;
    private BSPNode node;
    private ShapeNode next;
    private ShapeNode prev;
    private boolean mark;

    public ShapeNode(Shape shape, BSPNode node)
    {
        this.shape = shape;
        this.node = node;

        // insert into linked list
        ShapeNode first = IBSPColChecker.getNodeForShape(shape);
        this.next = first;
        IBSPColChecker.setNodeForShape(shape, this);
        if (next != null) {
            next.prev = this;
        }

        mark = true;
    }

    /**
     * Clar the mark on this ActorNode. This is used by the collision
     * checker when actors reposition or resize.
     */
    public void clearMark()
    {
        mark = false;
    }

    /**
     * Mark this ActorNode. This is used by the collision checker when
     * actors reposition or resize.
     */
    public void mark()
    {
        mark = true;
    }

    /**
     * Check, and clear, the mark on this ActorNode. This is used by the
     * collision checker when actors reposition or resize. Returns the
     * mark value before it was cleared.
     */
    public boolean checkMark()
    {
        boolean markVal = mark;
        mark = false;
        return markVal;
    }

    public Shape getShape()
    {
        return shape;
    }

    public BSPNode getBSPNode()
    {
        return node;
    }

    /**
     * Get the next ActorNode for the same actor. Returns null if this
     * is the last ActorNode for the actor.
     */
    public ShapeNode getNext()
    {
        return next;
    }

    /**
     * Remove this actor node. The node is removed from both the BSPNode
     * which contains it, and the linked list of actor nodes for the actor.
     * The next() call will still be valid, unless the next actor is also
     * removed.
     */
    public void remove()
    {
        removed();
        node.shapeRemoved(shape);
    }

    /**
     * Notify this actor node that it has been removed from the BSPNode.
     * It must remove itself from the linked list of actor nodes for the
     * actor.
     */
    public void removed()
    {
        if (prev == null) {
            IBSPColChecker.setNodeForShape(shape, next);
        }
        else {
            prev.next = next;
        }

        if (next != null) {
            next.prev = prev;
        }
    }
}
