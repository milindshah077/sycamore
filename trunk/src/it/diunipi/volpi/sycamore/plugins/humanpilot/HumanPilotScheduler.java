package it.diunipi.volpi.sycamore.plugins.humanpilot;

import it.diunipi.volpi.sycamore.gui.SycamorePanel;
import it.diunipi.volpi.sycamore.gui.SycamoreSystem;
import it.diunipi.volpi.sycamore.model.ComputablePoint;
import it.diunipi.volpi.sycamore.model.SycamoreAbstractPoint;
import it.diunipi.volpi.sycamore.model.SycamoreRobot;
import it.diunipi.volpi.sycamore.model.SycamoreRobot.ROBOT_STATE;
import it.diunipi.volpi.sycamore.plugins.schedulers.SchedulerImpl;

import java.util.Iterator;
import java.util.Vector;

import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * This scheduler is specific for human pilots. 
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
@PluginImplementation
public class HumanPilotScheduler<P extends SycamoreAbstractPoint & ComputablePoint<P>> extends SchedulerImpl<P>
{
	/* (non-Javadoc)
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getAuthor()
	 */
	@Override
	public String getAuthor()
	{
		return "Valerio Volpi - vale.v@me.com";
	}

	/* (non-Javadoc)
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginShortDescription()
	 */
	@Override
	public String getPluginShortDescription()
	{
		return "A scheduler for human pilot robots";
	}

	/* (non-Javadoc)
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginLongDescription()
	 */
	@Override
	public String getPluginLongDescription()
	{
		return "A scheduler for human pilot robots. They never stay without moving.";
	}

	/* (non-Javadoc)
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
	 * @see it.diunipi.volpi.sycamore.plugins.SchedulerImpl#runLoop_pre()
	 */
	@Override
	public void runLoop_pre()
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SchedulerImpl#runLoopIteration()
	 */
	@Override
	public void runLoopIteration()
	{
		// get not moving robots
		Vector<SycamoreRobot<P>> notMovingRobots = getNotMovingRobots();

		// then calls next operation
		for (SycamoreRobot<P> robot : notMovingRobots)
		{
			robot.doLook();
			robot.doCompute();
			robot.doMove();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.Scheduler#runLoop_post()
	 */
	@Override
	public void runLoop_post()
	{

	}
	
	
	/* (non-Javadoc)
	 * @see it.diunipi.volpi.sycamore.plugins.schedulers.SchedulerImpl#getNotMovingRobots()
	 */
	@Override
	protected Vector<SycamoreRobot<P>> getNotMovingRobots()
	{
		Vector<SycamoreRobot<P>> ret = new Vector<SycamoreRobot<P>>();
		Iterator<SycamoreRobot<P>> iterator = appEngine.getRobots().humanPilotsIterator();

		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			if (!robot.isMoving())
			{
				ret.add(robot);
			}
		}

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.schedulers.Scheduler#updateTimelines()
	 */
	@Override
	public void updateTimelines()
	{
		Iterator<SycamoreRobot<P>> iterator = appEngine.getRobots().humanPilotsIterator();

		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();

			float delta = SycamoreSystem.getSchedulerFrequency() * appEngine.getAnimationSpeedMultiplier() * (1.0f / robot.getTimelineDuration());

			if (robot.isMoving())
			{
				float ratio = robot.getCurrentRatio();
				ratio = ratio + delta;

				if (ratio > 1.0f)
				{
					robot.setCurrentState(ROBOT_STATE.READY_TO_LOOK);
					ratio = 1.0f;
				}
				robot.setCurrentRatio(ratio);
			}
			else
			{
				robot.addPause(SycamoreSystem.getSchedulerFrequency() * appEngine.getAnimationSpeedMultiplier());
			}
		}
	}
}
