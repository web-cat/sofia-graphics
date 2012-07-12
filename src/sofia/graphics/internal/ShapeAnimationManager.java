package sofia.graphics.internal;

import java.util.HashMap;
import sofia.graphics.Shape;
import java.util.HashSet;
import sofia.graphics.ShapeView;
import sofia.graphics.animation.ShapeAnimator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

// -------------------------------------------------------------------------
/**
 * Manages animations for all shapes system-wide.
 *
 * @author  Tony Allevato
 * @version 2011.12.04
 */
public class ShapeAnimationManager extends Thread
{
    private ShapeView view;
    private boolean running;
    private Object animatorToken = new Object();

    private ConcurrentLinkedQueue<ShapeAnimator> animators =
        new ConcurrentLinkedQueue<ShapeAnimator>();
    private HashMap<Shape, ShapeAnimator> currentAnimators =
        new HashMap<Shape, ShapeAnimator>();


    // ----------------------------------------------------------
    /**
     * Not intended for public use.
     */
    public ShapeAnimationManager(ShapeView view)
    {
        this.view = view;
        running = true;
    }


    // ----------------------------------------------------------
    public synchronized void setRunning(boolean value)
    {
        running = value;
    }


    // ----------------------------------------------------------
    public synchronized boolean isRunning()
    {
        return running;
    }


    // ----------------------------------------------------------
    public void enqueue(ShapeAnimator animator)
    {
        Shape shape = animator.getShape();
        if (currentAnimators.containsKey(shape))
        {
            currentAnimators.get(shape).stop();
        }

        currentAnimators.put(shape, animator);
        animators.offer(animator);

        synchronized (animatorToken)
        {
            animatorToken.notify();
        }
    }


    // ----------------------------------------------------------
    public void stop(Shape shape)
    {
        ShapeAnimator animator = currentAnimators.get(shape);

        if (animator != null)
        {
            animator.stop();
        }
    }


    // ----------------------------------------------------------
    @Override
    public void run()
    {
        view.internalSetAutoRepaintForThread(false);

        while (running)
        {
            waitForSignal();

            while (!animators.isEmpty())
            {
                long start = System.currentTimeMillis();

                Iterator<ShapeAnimator> it = animators.iterator();

                while (it.hasNext())
                {
                    ShapeAnimator animator = it.next();
                    Shape shape = animator.getShape();

                    boolean ended =
                        animator.advanceTo(start);

                    if (ended)
                    {
                        it.remove();

                        if (currentAnimators.get(shape) == animator)
                        {
                            currentAnimators.remove(shape);
                        }
                    }
                }

                view.repaint();

                long end = System.currentTimeMillis();
                long length = end - start;

                long FRAME_TIME = 20;

                if (length < FRAME_TIME)
                {
                    try
                    {
                        Thread.sleep(FRAME_TIME - length);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }

                end = System.currentTimeMillis();
                //System.out.println("Frame time in msec = " + (end - start));
            }
        }
    }


    // ----------------------------------------------------------
    private void waitForSignal()
    {
        synchronized (animatorToken)
        {
            while (animators.isEmpty())
            {
                try
                {
                    animatorToken.wait();
                }
                catch (InterruptedException e)
                {
                    // Do nothing.
                }
            }
        }
    }
}