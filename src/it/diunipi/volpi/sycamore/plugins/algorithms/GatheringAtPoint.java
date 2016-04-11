/**
 * 
 */
package it.diunipi.volpi.sycamore.plugins.algorithms;

import it.diunipi.volpi.sycamore.animation.SycamoreAnimatedObject;
import it.diunipi.volpi.sycamore.engine.Observation;

import it.diunipi.volpi.sycamore.engine.Point2D;
import it.diunipi.volpi.sycamore.engine.SycamoreObservedRobot;
import it.diunipi.volpi.sycamore.engine.SycamoreRobot;
import it.diunipi.volpi.sycamore.engine.SycamoreRobot2D;
import it.diunipi.volpi.sycamore.engine.SycamoreRobotMatrix;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.gui.SycamorePanel;
import it.diunipi.volpi.sycamore.jmescene.SycamoreJMEScene;
import it.diunipi.volpi.sycamore.plugins.memory.Memory;
import it.diunipi.volpi.sycamore.plugins.memory.RequestedDataNotInMemoryException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import it.diunipi.volpi.sycamore.gui.SycamoreRobotsConfigurationPanel;
/**
 * A simple algorithm where the robots reach the center or the points surrounding the center avoiding 
 * collision and only through the grid points.
 * @author Milind Shah
 */
@PluginImplementation
public class GatheringAtPoint extends AlgorithmImpl<Point2D>
{
	
	private static Queue<Point2D> recentPoints=new LinkedList<Point2D>();
	
	static{
		recentPoints.clear();
	}
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
		/*SycamoreRobot2D robot=(SycamoreRobot2D)caller;  // caller robot
		
		Point2D pos=new Point2D(0,0);                   // destination position
		Point2D curr=caller.getLocalPosition();         // current position
		Iterator<SycamoreRobot> iterator = SycamoreRobotsConfigurationPanel.appEngineCopy.getRobots().iterator();
		
		float lowerX=0, upperX=0, lowerY=0, upperY=0 ;
		pos.x=curr.x;
		pos.y=curr.y;
			
		synchronized(recentPoints)
		{
			if(recentPoints.contains(robot.previousPoint))
				recentPoints.remove(robot.previousPoint);
		}
			
		switch(SycamoreJMEScene.gridDimension)
		{
			case EvenEven :
				lowerX=-0.5f; upperX=0.5f; lowerY=-0.5f; upperY=0.5f;
				break;
			case OddOdd :
				lowerX=0; upperX=0; lowerY=0; upperY=0;
				break;
			case EvenOdd :
				lowerX=-0.5f; upperX=0.5f; lowerY=0; upperY=0;
				break;
			case OddEven:
				lowerX=0; upperX=0; lowerY=-0.5f; upperY=0.5f;
				break;
		}
		
		switch((int)Math.floor(Math.random()*2))
		{
			case 0:
			{
				if(curr.x < lowerX )
				{
					pos.x=curr.x+1;
					pos.y=curr.y;
				}
				else if(curr.x > upperX)
				{
					pos.x=curr.x-1;
					pos.y=curr.y;
				}
				else
				{
					if(curr.y > upperY)
					{
						pos.x=curr.x;
						pos.y=curr.y-1;
					}
					else if (curr.y < lowerY)
					{
						pos.x=curr.x;
						pos.y=curr.y+1;
					}
				}
				break;
			}
			case 1:
			{
				if(curr.y > upperY)
				{
					pos.x=curr.x;
					pos.y=curr.y-1;
				}
				else if (curr.y < lowerY)
				{
					pos.x=curr.x;
					pos.y=curr.y+1;
				}
				else
				{
					if(curr.x < lowerX )
					{
						pos.x=curr.x+1;
						pos.y=curr.y;
					}
					else if(curr.x > upperX)
					{
						pos.x=curr.x-1;
						pos.y=curr.y;
					}
					
				}
				break;
			}
		}
		
		
		if(pos.x==curr.x && pos.y==curr.y)
		{
			robot.previousPoint=new Point2D();
			return new Point2D(curr.x, curr.y);
	
		}
		
		if((pos.x==lowerX && pos.y==lowerY)||(pos.x==lowerX && pos.y==upperY)||(pos.x==upperX && pos.y==lowerY)||(pos.x==upperX && pos.y==upperY))
		{
			robot.previousPoint=curr;
			return new Point2D(pos.x,pos.y);
		}
		//synchronized(recentPoints)
		//{
			while(iterator.hasNext())
		
			{
				SycamoreRobot surrRobot =iterator.next();
				if(surrRobot.getLocalPosition().equals(pos))
				{
					robot.previousPoint=new Point2D();
					return new Point2D(curr.x, curr.y); 
				}
				
			}
			
		
			synchronized(recentPoints)
			{
				if(recentPoints.contains(pos))
				{
					robot.previousPoint=new Point2D();
					return new Point2D(curr.x, curr.y); 
				}
				recentPoints.add(pos);
			}
			
		//}
		robot.previousPoint=curr;
		*/return new Point2D();
		
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
		return "A center seeking robot.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginLongDescription()
	 */
	@Override
	public String getPluginLongDescription()
	{
		return "A simple algorithm where a robot reaches the center through the grid points avoiding collision. In case the center is not at a grid point, it reaches the closest point surrounding the center.";
	}

}
