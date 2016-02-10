/**
 * 
 */
package it.diunipi.volpi.sycamore.plugins.initialconditions;

import it.diunipi.volpi.sycamore.engine.Point2D;
import it.diunipi.volpi.sycamore.engine.SycamoreRobotMatrix;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.gui.SycamorePanel;
import it.diunipi.volpi.sycamore.jmescene.SycamoreJMEScene;
import it.diunipi.volpi.sycamore.util.SycamoreUtil;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * An initial condition where the robots are randomly disposed only on the grid points in a square  
 * from (-10, -10) to (10, 10)
 * 
 * @author Milind Shah
 */
@PluginImplementation
public class Random extends InitialConditionsImpl<Point2D>
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.InitialConditions#nextStartingPoint(java.util.Vector)
	 */
	@Override
	public Point2D nextStartingPoint(SycamoreRobotMatrix<Point2D> robots)
	{
		float y = SycamoreUtil.getRandomInt(-10, 10);
		float x = SycamoreUtil.getRandomInt(-10, 10);
		
		switch(SycamoreJMEScene.gridDimension)
		{
			case EvenEven: 
				if(y>=0)
					y-=0.5;
				else
					y+=0.5;
				if(x>=0)
					x-=0.5;
				else
					x+=0.5;	
				break;
				
			case EvenOdd:
				if(x>=0)
					x-=0.5;
				else
					x+=0.5;	
				break;
			case OddEven:
				if(y>=0)
					y-=0.5;
				else
					y+=0.5;
				break;
		}

		return new Point2D(x, y);
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
		return "Makes the robots randomly disposed on a 2D grid.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.plugins.SycamorePlugin#getPluginLongDescription()
	 */
	@Override
	public String getPluginLongDescription()
	{
		return "An initial condition where the robots are randomly disposed on a grid in a square from (-10, -10) to (10, 10).";
	}
}
