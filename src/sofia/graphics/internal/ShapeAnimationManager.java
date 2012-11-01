package sofia.graphics.internal;

import java.util.HashMap;
import sofia.graphics.Shape;
import sofia.graphics.ShapeView;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

// -------------------------------------------------------------------------
/**
 * Manages animations for all shapes system-wide.
 *
 * @author  Tony Allevato
 * @version 2011.12.04
 */
public class ShapeAnimationManager
{
	public static final String TESTING_MODE_PROPERTY =
			"sofia.graphics.testingMode";

    private ShapeView view;
    private boolean running;
    private Object animatorToken = new Object();

    private ConcurrentLinkedQueue<Shape.Animator<?>> animators =
        new ConcurrentLinkedQueue<Shape.Animator<?>>();
    private HashMap<Shape, Shape.Animator<?>> currentAnimators =
        new HashMap<Shape, Shape.Animator<?>>();


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
    public void enqueue(Shape.Animator<?> animator)
    {
    	if (isTestingMode())
    	{
    		// Just run the animator instantaneously.
    		
    		long endTime = System.currentTimeMillis()
    				+ animator.getDelay() + animator.getDuration();
    		animator.advanceTo(endTime);
    	}
    	else
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
    }


    // ----------------------------------------------------------
    public void stop(Shape shape)
    {
    	Shape.Animator<?> animator = currentAnimators.get(shape);

        if (animator != null)
        {
            animator.stop();
        }
    }


    // ----------------------------------------------------------
    public void start()
    {
    	if (!isTestingMode())
    	{
    		new ProductionThread().start();
    	}
    }


    // ----------------------------------------------------------
    private boolean isTestingMode()
    {
    	String testingProp = System.getProperty(
    			TESTING_MODE_PROPERTY, "false");
    	
    	return Boolean.parseBoolean(testingProp);    	
    }


    // ----------------------------------------------------------
    private class ProductionThread extends Thread
    {
        // ----------------------------------------------------------
	    @Override
	    public void run()
	    {
	        while (running)
	        {
	            waitForSignal();
	            view.internalSetAutoRepaintForThread(false);
	
	            while (!animators.isEmpty())
	            {
	                long start = System.currentTimeMillis();
	
	                Iterator<Shape.Animator<?>> it = animators.iterator();
	
	                while (it.hasNext())
	                {
	                	Shape.Animator<?> animator = it.next();
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
	                    	// Do nothing.
	                    }
	                }
	
	                end = System.currentTimeMillis();
	            }
	
	            view.internalSetAutoRepaintForThread(true);
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