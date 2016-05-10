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
 * A simple algorithm where the robots gather at an undecided point avoiding 
 * collision and only through the grid points.
 * @author Milind Shah, Sunil Singh
 */
@PluginImplementation
public class Gathering extends AlgorithmImpl<Point2D>
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
		SycamoreRobot2D robot=(SycamoreRobot2D)caller;     // caller robot
		
		Point2D targetPos=new Point2D(0,0);              // target position
		Point2D currentPos=robot.getLocalPosition();    // current position
		Point2D destPos=new Point2D(0,0);   			// destination position
	
		targetPos.x=currentPos.x; targetPos.y=currentPos.y;
		destPos.x=currentPos.x; destPos.y=currentPos.y;
			
		Point2D robotPos;
		
		// decide the destination point
		for(Observation<Point2D> obsv : observations)
		{
			robotPos=obsv.getRobotPosition();
			if(Math.round(robotPos.x) > Math.round(destPos.x) )
			{
				destPos.x = robotPos.x; destPos.y = robotPos.y; 
			}
			else if(Math.round(robotPos.x) == Math.round(destPos.x) && Math.round(robotPos.y) < Math.round(destPos.y))
			{
				destPos.x = robotPos.x; destPos.y = robotPos.y;
			}
				
		}
		
		// check if the robot is already at destination 
		if(destPos.equals(currentPos))
		{
			//robot.setAlgorithm(null);
			return currentPos;
		}
		
		// decide the next target point
		switch((int)Math.floor(Math.random()*2))
		{
			case 0:
			{
				if(currentPos.x < destPos.x  && (currentPos.x + 1 < destPos.x || currentPos.y >= destPos.y))
				{
					targetPos.x=currentPos.x+1;
				}
				else if(currentPos.x > destPos.x)
				{
					targetPos.x=currentPos.x-1;
				}
				else
				{
					if(currentPos.y > destPos.y)
					{
						targetPos.y=currentPos.y-1;
					}
					else if (currentPos.y < destPos.y)
					{
						targetPos.y=currentPos.y+1;
					}
				}
				break;
			}
			case 1:
			{
				if(currentPos.y > destPos.y)
				{
					targetPos.y=currentPos.y-1;
				}
				else if (currentPos.y < destPos.y)
				{
					targetPos.y=currentPos.y+1;
				}
				else
				{
					if(currentPos.x < destPos.x  && (currentPos.x + 1 < destPos.x || currentPos.y >= destPos.y))
					{
						targetPos.x=currentPos.x+1;
					}
					else if(currentPos.x > destPos.x)
					{
						targetPos.x=currentPos.x-1;
					}
					
				}
				break;
			}
		}
		
		// check if the target point is the destination
		if((targetPos.x==destPos.x && targetPos.y==destPos.y))
		{
			return targetPos;
		}

		double myDist = getDistance(currentPos, targetPos);
		
		// check for collision
		for(Observation<Point2D> obsv : observations)
		{
			robotPos=obsv.getRobotPosition();
			if(getDistance(robotPos, targetPos) <= myDist &&  !robotPos.equals(destPos) &&  !robotPos.equals(currentPos) )
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
		return "An algorithm for gathering all the robots at an undecided point.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginLongDescription()
	 */
	@Override
	public String getPluginLongDescription()
	{
		return "A simple algorithm where a robot gathers at an undecided point avoiding collision. The robots gather at the position of the robot to the extreme right bottom.";
	}

}
