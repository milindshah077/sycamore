/**
 * 
 */
package it.diunipi.volpi.sycamore.plugins.initialconditions;

import it.diunipi.volpi.sycamore.engine.Point2D;
import it.diunipi.volpi.sycamore.engine.SycamoreRobot;
import it.diunipi.volpi.sycamore.engine.SycamoreRobotMatrix;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.gui.SycamorePanel;
import it.diunipi.volpi.sycamore.plugins.agreements.Agreement;
import it.diunipi.volpi.sycamore.plugins.visibilities.Visibility;
import it.diunipi.volpi.sycamore.util.ApplicationProperties;
import it.diunipi.volpi.sycamore.util.PropertyManager;
import it.diunipi.volpi.sycamore.util.SycamoreUtil;

import java.util.Iterator;

import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * An initial condition that makes the robots start disposed in a way that their visibility graph is
 * connected, so that all of them can see at least one of its neighbors.
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
@PluginImplementation
public class VisibilityGraphConnected2D extends InitialConditionsImpl<Point2D>
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.InitialConditions#nextStartingPoint(java.util.Vector)
	 */
	@Override
	public Point2D nextStartingPoint(SycamoreRobotMatrix<Point2D> robots)
	{
		if (robots.robotsCount() > 0)
		{
			int num = 1;
			// choose one random robot between passed ones
			if (robots.robotsCount() > 1)
			{
				num = SycamoreUtil.getRandomInt(1, robots.robotsCount());
			}
			Iterator<SycamoreRobot<Point2D>> iterator = robots.robotsIterator();

			SycamoreRobot<Point2D> chosen = null;
			for (int i = 0; i < num; i++)
			{
				chosen = iterator.next();
			}

			if (chosen != null)
			{
				// take a point inside robot's visible area
				Visibility<Point2D> visibilty = chosen.getVisibility();
				Agreement<Point2D> agreement = chosen.getAgreement();
				if (visibilty != null)
				{
					// point is in local coords
					Point2D point = visibilty.getPointInside();
					
					if (agreement != null)
					{
						point = agreement.toGlobalCoordinates(point);
					}
					
					return point;
				}
			}
		}

		int minX = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MIN_X);
		int maxX = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MAX_X);
		int minY = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MIN_Y);
		int maxY = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MAX_Y);

		return SycamoreUtil.getRandomPoint2D(minX, maxX, minY, maxY);
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
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getSettings()
	 */
	@Override
	public SycamorePanel getPanel_settings()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getAuthor()
	 */
	@Override
	public String getAuthor()
	{
		return "Valerio Volpi - vale.v@me.com";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginShortDescription()
	 */
	@Override
	public String getPluginShortDescription()
	{
		return "Makes the visibility graph initially connected";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginLongDescription()
	 */
	@Override
	public String getPluginLongDescription()
	{
		return "An initial condition that makes the robots start disposed in a way that their visibility graph is connected, so that all of them can see at least one of its neighbors.";
	}
}
