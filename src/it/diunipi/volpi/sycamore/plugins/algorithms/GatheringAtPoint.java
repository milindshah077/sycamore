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
 * A simple algorithm where the robots gather at the center or the points surrounding the center avoiding 
 * collision and only through the grid points.
 * @author Milind Shah, Sunil Singh
 */
@PluginImplementation
public class GatheringAtPoint extends AlgorithmImpl<Point2D>
{
	/* (non-Javadoc)
	 * @see it.diunipi.volpi.sycamore.plugins.algorithms.Algorithm#init()
	 */
	@Override
	public void init(SycamoreObservedRobot<Point2D> robot)
	{
		// Nothing to do
	}
	
	
	public static double getDistance(Point2D a, Point2D b)
	{
		double dist;
		dist = Math.pow((a.x-b.x), 2);
		dist += Math.pow((a.y-b.y), 2);
		return Math.sqrt(dist);
	}
	
	public static boolean hasReached(Point2D pos, float lowerX, float upperX, float lowerY, float upperY)
	{
		if((pos.x==lowerX && pos.y==lowerY)||(pos.x==lowerX && pos.y==upperY)||(pos.x==upperX && pos.y==lowerY)||(pos.x==upperX && pos.y==upperY))
		{
			return true;
		}
		return false;
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
		SycamoreRobot2D robot=(SycamoreRobot2D)caller;    // caller robot
		
		Point2D targetPos=new Point2D(0,0);              // target position
		Point2D currentPos=robot.getGlobalPosition();    // current position
		
		float lowerX=0, upperX=0, lowerY=0, upperY=0 ;
		
		targetPos.x=currentPos.x; targetPos.y=currentPos.y;
		
		// decide the destination i.e. the center point	
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
		
		// decide the next target point
		switch((int)Math.floor(Math.random()*2))                   
		{
			case 0:
			{
				if(currentPos.x < lowerX )
				{
					targetPos.x=currentPos.x+1;
				}
				else if(currentPos.x > upperX)
				{
					targetPos.x=currentPos.x-1;
				}
				else
				{
					if(currentPos.y > upperY)
					{
						targetPos.y=currentPos.y-1;
					}
					else if (currentPos.y < lowerY)
					{
						targetPos.y=currentPos.y+1;
					}
				}
				break;
			}
			case 1:
			{
				if(currentPos.y > upperY)
				{
					targetPos.y=currentPos.y-1;
				}
				else if (currentPos.y < lowerY)
				{
					targetPos.y=currentPos.y+1;
				}
				else
				{
					if(currentPos.x < lowerX )
					{
						targetPos.x=currentPos.x+1;
					}
					else if(currentPos.x > upperX)
					{
						targetPos.x=currentPos.x-1;
					}
					
				}
				break;
			}
		}
		
		// check if the robot is already at destination
		if(targetPos.x==currentPos.x && targetPos.y==currentPos.y)
		{
			//robot.setAlgorithm(null);
			return currentPos;
		}
		
		// check if the target point is the destination 
		if((targetPos.x==lowerX && targetPos.y==lowerY)||(targetPos.x==lowerX && targetPos.y==upperY)||(targetPos.x==upperX && targetPos.y==lowerY)||(targetPos.x==upperX && targetPos.y==upperY))
		{
			return targetPos;                          
		}

		double myDist = getDistance(currentPos, targetPos);
		Point2D robotPosition;
		
		// check for collision 
		for(Observation<Point2D> obsv : observations)
		{
			robotPosition=obsv.getRobotPosition();
			if(getDistance(robotPosition, targetPos) <= myDist && !hasReached(robotPosition, lowerX, upperX, lowerY, upperY) &&  !robotPosition.equals(currentPos) )
			{
				return currentPos; 
			}
		}
				
		return targetPos;
		
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
		return "Milind Shah, Sunil Singh";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginShortDescription()
	 */
	@Override
	public String getPluginShortDescription()
	{
		return "An algorithm for gathering all the robots at a fixed point.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginLongDescription()
	 */
	@Override
	public String getPluginLongDescription()
	{
		return "A simple algorithm where a robot gathers at the center through the grid points avoiding collision. In case the center is not at a grid point, it reaches the closest point surrounding the center.";
	}

}
