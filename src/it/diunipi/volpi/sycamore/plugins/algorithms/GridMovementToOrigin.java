/**
 * 
 */
package it.diunipi.volpi.sycamore.plugins.algorithms;

import it.diunipi.volpi.sycamore.engine.Observation;
import it.diunipi.volpi.sycamore.engine.Point2D;
import it.diunipi.volpi.sycamore.engine.SycamoreObservedRobot;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.gui.SycamorePanel;

import java.util.Vector;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import com.jme3.math.FastMath;

/**
 * A simple algorithm where the robot reaches the origin and starts rotating on itself.
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
@PluginImplementation
public class GridMovementToOrigin extends AlgorithmImpl<Point2D>
{
	private boolean	beginning	= true;
	private float	angle		= 0;

	/* (non-Javadoc)
	 * @see it.diunipi.volpi.sycamore.plugins.algorithms.Algorithm#init()
	 */
	@Override
	public void init(SycamoreObservedRobot<Point2D> robot)
	{
		// Nothing to do
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.algorithms.Algorithm#compute(java.util.Vector,
	 * it.diunipi.volpi.sycamore.model.SycamoreObservedRobot)
	 */
	@Override
	public Point2D compute(Vector<Observation<Point2D>> observations, SycamoreObservedRobot<Point2D> caller)
	{
		
		Point2D curr=caller.getLocalPosition();
		
		if(curr.x < 0 )
		{
			return new Point2D(curr.x+1, curr.y);
		}
		else if(curr.x > 0)
		{
			return new Point2D(curr.x-1, curr.y);
		}
		else
		{
			if(curr.y>0)
				return new Point2D(curr.x, curr.y - 1);
			else if (curr.y<0)
				return new Point2D(curr.x, curr.y+1);
			else
				return new Point2D(curr.x, curr.y);
		}
		
		/*if (beginning)
		{
			beginning = false;
			return new Point2D(0, 0);
		}
		else
		{
			Point2D p = new Point2D(FastMath.cos(angle), FastMath.sin(angle));
			angle += 0.005f;

			caller.setDirection(p);
			
			return caller.getLocalPosition();
		}*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.algorithms.Algorithm#getReferences()
	 */
	@Override
	public String getReferences()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamoreTypedPlugin#getType()
	 */
	@Override
	public TYPE getType()
	{
		return TYPE.TYPE_2D;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPanel_settings()
	 */
	@Override
	public SycamorePanel getPanel_settings()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginName()
	 */
	@Override
	public String getPluginName()
	{
		return getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getAuthor()
	 */
	@Override
	public String getAuthor()
	{
		return "Milind Shah";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginShortDescription()
	 */
	@Override
	public String getPluginShortDescription()
	{
		return "An orignin seeking robot.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginLongDescription()
	 */
	@Override
	public String getPluginLongDescription()
	{
		return "A simple algorithm where the robot reaches the origin through the grid.";
	}

}
