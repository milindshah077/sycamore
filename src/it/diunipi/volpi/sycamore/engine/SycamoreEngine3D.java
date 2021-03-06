/**
 * 
 */
package it.diunipi.volpi.sycamore.engine;

import it.diunipi.volpi.sycamore.plugins.agreements.Agreement;
import it.diunipi.volpi.sycamore.plugins.agreements.AgreementImpl;
import it.diunipi.volpi.sycamore.plugins.algorithms.Algorithm;
import it.diunipi.volpi.sycamore.plugins.algorithms.AlgorithmImpl;
import it.diunipi.volpi.sycamore.plugins.initialconditions.InitialConditions;
import it.diunipi.volpi.sycamore.plugins.measures.Measure;
import it.diunipi.volpi.sycamore.plugins.memory.Memory;
import it.diunipi.volpi.sycamore.plugins.memory.MemoryImpl;
import it.diunipi.volpi.sycamore.plugins.visibilities.Visibility;
import it.diunipi.volpi.sycamore.plugins.visibilities.VisibilityImpl;
import it.diunipi.volpi.sycamore.util.ApplicationProperties;
import it.diunipi.volpi.sycamore.util.PropertyManager;
import it.diunipi.volpi.sycamore.util.SycamoreUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import com.jme3.math.ColorRGBA;

/**
 * @see SycamoreEngine
 * 
 *      The 3D version of the Sycamore engine. It is a concrete implementation of the engine that
 *      fixes its type to be 3 dimensional. It uses a TYPE_3D enum element to identify the type, and
 *      a Point3D object to satisfy the generic.
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
public class SycamoreEngine3D extends SycamoreEngine<Point3D>
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.engine.SycamoreEngine#getObservation(it.diunipi.volpi.sycamore.
	 * engine.SycamoreRobot, it.diunipi.volpi.sycamore.engine.SycamoreRobot)
	 */
	@Override
	public Observation<Point3D> getObservation(SycamoreRobot<Point3D> robot, SycamoreRobot<Point3D> caller)
	{
		Point3D position = robot.getGlobalPosition();

		// translate position to caller's local coords
		if (caller.getAgreement() != null)
		{
			position = caller.getAgreement().toLocalCoordinates(position);
		}

		// update intensity of lights
		Vector<SycamoreObservedLight> lights = robot.getLights();
		for (int i = 0; i < lights.size(); i++)
		{
			SycamoreObservedLight light = lights.elementAt(i);
			if (light.getIntensity() != -1.0f)
			{
				ColorRGBA color = light.getColor();

				// compute the distance between the robots
				float distance = caller.getGlobalPosition().distanceTo(robot.getGlobalPosition());
				float intensity = light.getIntensity();

				if (distance >= 1)
				{
					// intensity degrades with the squared of the distance
					intensity = (float) (light.getIntensity() / (Math.pow(distance, 2)));
				}
				else
				{
					intensity = light.getIntensity();
				}

				SycamoreRobotLight3D newLight = new SycamoreRobotLight3D(color, null, intensity);
				lights.set(i, newLight);
			}
		}

		return new ObservationExt<Point3D>(position, lights, robot.getAlgorithm());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.engine.SycamoreEngine#createAndAddNewRobotInstance(int,
	 * java.lang.Class)
	 */
	@Override
	public SycamoreRobot<Point3D> createAndAddNewRobotInstance(boolean isHumanPilot, int index, ColorRGBA color, int maxLights, float speed)
	{
		{
			// create a new oblivious robot
			SycamoreRobot3D robot = new SycamoreRobot3D(this, computeStartingPoint(), speed, color, maxLights);

			if (isHumanPilot)
			{
				this.robots.addHumanPilot(index, robot);
			}
			else
			{
				// add in vector
				this.robots.addRobot(index, robot);
			}

			// check if some visibility plugins have to be added
			Visibility visibility = this.getCurrentVisibility();
			if (visibility != null && visibility.getType() == TYPE.TYPE_3D)
			{
				try
				{
					createAndSetNewVisibilityInstance(visibility, robot);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			// check if some memory plugins have to be added
			Memory memory = this.getCurrentMemory();
			if (memory != null && memory.getType() == TYPE.TYPE_3D)
			{
				try
				{
					createAndSetNewMemoryInstance(memory, robot);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			// check if some agreement plugins have to be added
			Agreement agreement = this.getCurrentAgreement();
			if (agreement != null && agreement.getType() == TYPE.TYPE_3D)
			{
				try
				{
					createAndSetNewAgreementInstance(agreement, robot);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			return robot;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.engine.SycamoreEngine#createAndSetNewAlgorithmInstance(it.diunipi
	 * .volpi.sycamore.plugins.algorithms.Algorithm, int)
	 */
	@Override
	public void createAndSetNewAlgorithmInstance(Algorithm<Point3D> algorithm, int index) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{		
		boolean isHumanPilot = algorithm.isHumanPilot();
		Vector<SycamoreRobot<Point3D>> robotList = isHumanPilot ? robots.getHumanPilotRow(index) : this.robots.getRobotRow(index);

		for (SycamoreRobot<Point3D> robot : robotList)
		{
			this.createAndSetNewAlgorithmInstance(algorithm, robot);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.engine.SycamoreEngine#createAndSetNewAlgorithmInstance(it.diunipi
	 * .volpi.sycamore.plugins.algorithms.Algorithm, it.diunipi.volpi.sycamore.engine.SycamoreRobot)
	 */
	@Override
	public void createAndSetNewAlgorithmInstance(Algorithm<Point3D> algorithm, SycamoreRobot<Point3D> robot) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException
	{
		if (robot.getAlgorithm() != null)
		{
			robot.getAlgorithm().reset();
		}
		
		// create a new instance of the algorithm
		Class<? extends Algorithm> algorithmClass = algorithm.getClass();
		Constructor<?> constructor = algorithmClass.getConstructors()[0];

		AlgorithmImpl<Point3D> newInstance = (AlgorithmImpl<Point3D>) constructor.newInstance();
		newInstance.init(robot);
		robot.setAlgorithm(newInstance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.engine.SycamoreEngine#createAndSetNewVisibilityInstance(it.diunipi
	 * .volpi.sycamore.plugins.visibilities.Visibility,
	 * it.diunipi.volpi.sycamore.engine.SycamoreRobot)
	 */
	@Override
	public void createAndSetNewVisibilityInstance(Visibility<Point3D> visibility, SycamoreRobot<Point3D> robot) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException
	{
		Constructor<?> constructor = null;
		if (visibility != null)
		{
			// create a new instance of the visibility
			Class<? extends Visibility> visibilityClass = visibility.getClass();
			constructor = visibilityClass.getConstructors()[0];

			if (constructor != null)
			{
				// assign visibilty to each robot
				VisibilityImpl<Point3D> newInstance = (VisibilityImpl<Point3D>) constructor.newInstance();

				newInstance.setRobot(robot);
				robot.setVisibility(newInstance);
				return;
			}
		}

		// If no Visibility is set, set null
		robot.setVisibility(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.engine.SycamoreEngine#createAndSetNewMemoryInstance(it.diunipi.
	 * volpi.sycamore.plugins.memory.Memory, it.diunipi.volpi.sycamore.engine.SycamoreRobot)
	 */
	@Override
	public void createAndSetNewMemoryInstance(Memory<Point3D> memory, SycamoreRobot<Point3D> robot) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException
	{
		Constructor<?> constructor = null;
		if (memory != null)
		{
			// create a new instance of the memory
			Class<? extends Memory> memoryClass = memory.getClass();
			constructor = memoryClass.getConstructors()[0];

			if (constructor != null)
			{
				// assign memory to each robot
				MemoryImpl<Point3D> newInstance = (MemoryImpl<Point3D>) constructor.newInstance();
				robot.setMemory(newInstance);
				return;
			}
		}

		// If no Memory is set, set null
		robot.setMemory(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.engine.SycamoreEngine#createAndAddNewMeasureInstance(it.diunipi
	 * .volpi.sycamore.plugins.measures.Measure)
	 */
	@Override
	public void createAndAddNewMeasureInstance(Measure measure) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Constructor<?> constructor = null;

		// create a new instance of the Measure
		Class<? extends Measure> measureClass = measure.getClass();
		constructor = measureClass.getConstructors()[0];

		if (constructor != null)
		{
			Measure newInstance = (Measure) constructor.newInstance();

			newInstance.setEngine(this);
			this.measures.add(newInstance);
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.engine.SycamoreEngine#creatAndSetNewInitialConditionsInstance(it
	 * .diunipi.volpi.sycamore.plugins.Visibility)
	 */
	@Override
	public void createAndSetNewInitialConditionsInstance(InitialConditions<Point3D> initialConditions) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException
	{
		Constructor<?> constructor = null;
		if (initialConditions != null)
		{
			// create a new instance of the scheduler
			Class<? extends InitialConditions> initialConditionsClass = initialConditions.getClass();
			constructor = initialConditionsClass.getConstructors()[0];

			InitialConditions<Point3D> newInstance = (InitialConditions<Point3D>) constructor.newInstance();
			this.initialConditions = newInstance;
			return;
		}

		// if no InitialConditions has been set, set null
		this.initialConditions = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.engine.SycamoreEngine#createAndSetNewAgreementInstance(it.diunipi
	 * .volpi.sycamore.plugins.agreements.Agreement, it.diunipi.volpi.sycamore.engine.SycamoreRobot)
	 */
	@Override
	public void createAndSetNewAgreementInstance(Agreement<Point3D> agreement, SycamoreRobot<Point3D> robot) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException
	{
		Constructor<?> constructor = null;
		if (agreement != null)
		{
			// create a new instance of the agreement
			Class<? extends Agreement> agreementClass = agreement.getClass();
			constructor = agreementClass.getConstructors()[0];

			if (constructor != null)
			{
				// assign memory to each robot
				AgreementImpl<Point3D> newInstance = (AgreementImpl<Point3D>) constructor.newInstance();
				robot.setAgreement(newInstance);
				return;
			}
		}

		// if no Agreement has been set, set null
		robot.setAgreement(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.engine.SycamoreEngine#getTypeClass()
	 */
	@Override
	public TYPE getType()
	{
		return TYPE.TYPE_3D;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.engine.SycamoreEngine#computeStartingPoint()
	 */
	@Override
	protected Point3D computeStartingPoint()
	{
		if (initialConditions == null)
		{
			int minX = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MIN_X);
			int maxX = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MAX_X);
			int minY = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MIN_Y);
			int maxY = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MAX_Y);
			int minZ = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MIN_Z);
			int maxZ = PropertyManager.getSharedInstance().getIntegerProperty(ApplicationProperties.INITIAL_POSITION_MAX_Z);

			return SycamoreUtil.getRandomPoint3D(minX, maxX, minY, maxY, minZ, maxZ);
		}
		else
		{
			return initialConditions.nextStartingPoint(this.robots);
		}
	}
}
