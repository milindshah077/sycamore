package it.diunipi.volpi.sycamore.engine;

import it.diunipi.volpi.sycamore.plugins.SycamorePluginManager;
import it.diunipi.volpi.sycamore.plugins.agreements.Agreement;
import it.diunipi.volpi.sycamore.plugins.algorithms.Algorithm;
import it.diunipi.volpi.sycamore.plugins.humanpilot.HumanPilotScheduler;
import it.diunipi.volpi.sycamore.plugins.initialconditions.InitialConditions;
import it.diunipi.volpi.sycamore.plugins.measures.Measure;
import it.diunipi.volpi.sycamore.plugins.memory.Memory;
import it.diunipi.volpi.sycamore.plugins.schedulers.Scheduler;
import it.diunipi.volpi.sycamore.plugins.schedulers.SchedulerImpl;
import it.diunipi.volpi.sycamore.plugins.visibilities.Visibility;
import it.diunipi.volpi.sycamore.plugins.visibilities.VisibilityImpl;
import it.diunipi.volpi.sycamore.util.SycamoreFiredActionEvents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jme3.math.ColorRGBA;

/**
 * The Sycamore engine. Contains all the data related to the current simulation: the robots, the
 * schedulers and all the others plugins. Cares about the management of all the objects in the model
 * and about the state of the system, for instance which objects are present and which others are
 * visible. The robots, during the simulation, can query the engine to obtain the snapshot of the
 * system and also the user interface of the application and the OpenGL scene can query the engine
 * to know what data to draw. Every observation is made by the engine, that is the only object in
 * the system that knows the local and global coordinates of any visible object. An engine is valid
 * if there is at least a robot, and if its current scheduler is set (not null).
 * 
 * @author Valerio Volpi - vale.v@me.com
 * 
 * @param <P>
 *            the type of point on which this engine and all the contained objects (robots,
 *            plugins...) are build.
 */
public abstract class SycamoreEngine<P extends SycamoreAbstractPoint & ComputablePoint<P>>
{
	/**
	 * The possible types. These are not only used in engine, but also in robots and typed plugins.
	 * 
	 * @author Valerio Volpi - vale.v@me.com
	 */
	public static enum TYPE
	{
		TYPE_2D("2D"), TYPE_3D("3D");

		private String	shortDescription	= null;

		TYPE(String shortDescription)
		{
			this.shortDescription = shortDescription;
		}

		/**
		 * @return the shortDescription
		 */
		public String getShortDescription()
		{
			return shortDescription;
		}
	}

	// robots
	protected SycamoreRobotMatrix<P>			robots						= null;

	// plugins
	protected InitialConditions<P>				initialConditions			= null;
	protected Vector<Measure>					measures					= null;
	private Scheduler<P>						scheduler					= null;
	private HumanPilotScheduler<P>				humanPilotScheduler			= null;

	// auxiliary data
	private HashMap<SycamoreRobot<P>, Float>	ratioSnapshot				= null;
	private float								animationSpeedMultiplier	= getDefaultAnimationSpeedMultiplier();
	private Vector<ActionListener>				listeners					= null;

	/**
	 * Default constructor.
	 */
	public SycamoreEngine()
	{
		this.robots = new SycamoreRobotMatrix<P>();

		this.humanPilotScheduler = new HumanPilotScheduler<P>();
		this.humanPilotScheduler.setAppEngine(this);

		this.measures = new Vector<Measure>();
		this.listeners = new Vector<ActionListener>();

		this.ratioSnapshot = new HashMap<SycamoreRobot<P>, Float>();
	}

	/**
	 * Adds an <code>ActionListener</code> to the button.
	 * 
	 * @param listener
	 *            the <code>ActionListener</code> to be added
	 */
	public void addActionListener(ActionListener listener)
	{
		this.listeners.add(listener);
	}

	/**
	 * Removes an <code>ActionListener</code> from the button. If the listener is the currently set
	 * <code>Action</code> for the button, then the <code>Action</code> is set to <code>null</code>.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public void removeActionListener(ActionListener listener)
	{
		this.listeners.remove(listener);
	}

	/**
	 * Fires passed ActionEvent to all registered listeners, by calling <code>ActionPerformed</code>
	 * method on all of them.
	 * 
	 * @param e
	 */
	private void fireActionEvent(ActionEvent e)
	{
		for (ActionListener listener : this.listeners)
		{
			listener.actionPerformed(e);
		}
	}

	/**
	 * Returns the default value of the animation speed multiplier
	 * 
	 * @return the default value of the animation speed multiplier
	 */
	public static final int getDefaultAnimationSpeedMultiplier()
	{
		return 50;
	}

	/**
	 * @return the robots vectors
	 */
	public SycamoreRobotMatrix<P> getRobots()
	{
		return this.robots;
	}

	/**
	 * @return the robots with passed index. if the asked element is the "size + 1"-th, creates it
	 */
	public Vector<SycamoreRobot<P>> getRobots(int index)
	{
		return robots.getRobotRow(index);
	}

	/**
	 * @return the animationSpeedMultiplier
	 */
	public float getAnimationSpeedMultiplier()
	{
		return animationSpeedMultiplier;
	}

	/**
	 * @param animationSpeedMultiplier
	 *            the animationSpeedMultiplier to set
	 */
	public void setAnimationSpeedMultiplier(float animationSpeedMultiplier)
	{
		this.animationSpeedMultiplier = animationSpeedMultiplier;
	}

	/**
	 * @param visible
	 */
	public void setRobotLightsVisible(boolean visible)
	{
		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			robot.updateLightsVisible();
		}
	}

	/**
	 * @return the currentScheduler
	 */
	public Scheduler<P> getCurrentScheduler()
	{
		return scheduler;
	}

	/**
	 * @param currentScheduler
	 *            the currentScheduler to set
	 */
	protected void setCurrentScheduler(Scheduler<P> currentScheduler)
	{
		this.scheduler = currentScheduler;
	}

	/**
	 * @return the currentMeasures
	 */
	public Vector<Measure> getCurrentMeasures()
	{
		return measures;
	}

	/**
	 * @param currentMeasures
	 *            the currentMeasures to set
	 */
	protected void setCurrentMeasures(Vector<Measure> currentMeasures)
	{
		this.measures = currentMeasures;
	}

	/**
	 * @return the initialConditions
	 */
	public InitialConditions<P> getCurrentInitialConditions()
	{
		return initialConditions;
	}

	/**
	 * @param initialConditions
	 *            the initialConditions to set
	 */
	public void setCurrentInitialConditions(InitialConditions<P> initialConditions)
	{
		this.initialConditions = initialConditions;
	}

	/**
	 * Returns the current visibility set in the robots of this engine.
	 * 
	 * @return current visibility
	 */
	public Visibility<P> getCurrentVisibility()
	{
		if (this.robots.size() > 0)
		{
			// get robots. Since just one vector between robots and human pilot robots has data
			// inside, I can merge them without worry
			Vector<SycamoreRobot<P>> robotsList = new Vector<SycamoreRobot<P>>();
			robotsList.addAll(robots.getRobotRow(0));
			robotsList.addAll(robots.getHumanPilotRow(0));

			if (!robotsList.isEmpty())
			{
				return robotsList.firstElement().getVisibility();
			}
		}

		return null;
	}

	/**
	 * Creates a new visibility instance using passed interface, and sets such instance as the
	 * visibility in all the robots in the engine. This visibility will be applied also the newly
	 * created robots, since this moment.
	 * 
	 * @param visibility
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void createAndSetNewVisibilityInstance(Visibility<P> visibility) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		// set visibility range in robots
		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			this.createAndSetNewVisibilityInstance(visibility, robot);
		}
	}

	/**
	 * Returns the current memory set in the robots of this engine.
	 * 
	 * @return current memory
	 */
	public Memory<P> getCurrentMemory()
	{
		if (this.robots.size() > 0)
		{
			// get robots. Since just one vector between robots and human pilot robots has data
			// inside, I can merge them without worry
			Vector<SycamoreRobot<P>> robotsList = new Vector<SycamoreRobot<P>>();
			robotsList.addAll(robots.getRobotRow(0));
			robotsList.addAll(robots.getHumanPilotRow(0));

			if (!robotsList.isEmpty())
			{
				return robotsList.firstElement().getMemory();
			}
		}

		return null;
	}

	/**
	 * Creates a new memory instance using passed interface, and sets such instance as the memory in
	 * all the robots in the engine. This memory will be applied also the newly created robots,
	 * since this moment.
	 * 
	 * @param memory
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void createAndSetNewMemoryInstance(Memory<P> memory) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		// set visibility range in robots
		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			this.createAndSetNewMemoryInstance(memory, robot);
		}
	}

	/**
	 * Returns the current agreement set in the robots of this engine.
	 * 
	 * @return current agreement
	 */
	public Agreement<P> getCurrentAgreement()
	{
		if (this.robots.size() > 0)
		{
			// get robots. Since just one vector between robots and human pilot robots has data
			// inside, I can merge them without worry
			Vector<SycamoreRobot<P>> robotsList = new Vector<SycamoreRobot<P>>();
			robotsList.addAll(robots.getRobotRow(0));
			robotsList.addAll(robots.getHumanPilotRow(0));

			if (!robotsList.isEmpty())
			{
				return robotsList.firstElement().getAgreement();
			}
		}

		return null;
	}

	/**
	 * Creates a new agreement instance using passed interface, and sets such instance as the
	 * agreement in all the robots in the engine. This agreement will be applied also the newly
	 * created robots, since this moment.
	 * 
	 * @param agreement
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void createAndSetNewAgreementInstance(Agreement<P> agreement) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		// set visibility range in robots
		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			this.createAndSetNewAgreementInstance(agreement, robot);
		}
	}

	/**
	 * @return the humanPilotRobots
	 */
	public Vector<SycamoreRobot<P>> getHumanPilotRobots(int index)
	{
		return robots.getHumanPilotRow(index);
	}

	/**
	 * @return the humanPilotScheduler
	 */
	public HumanPilotScheduler<P> getHumanPilotScheduler()
	{
		return humanPilotScheduler;
	}

	/**
	 * @return The positions of all the robots in the system
	 * @throws TimelineNotAccessibleException
	 */
	public abstract Observation<P> getObservation(SycamoreRobot<P> robot, SycamoreRobot<P> caller);

	/**
	 * @return true if the simulation is finished, false otherwise. The simulation is finished if
	 *         every robot is finished.
	 */
	public synchronized boolean isSimulationFinished()
	{
		boolean simulationFinished = true;
		// the simulation is finished if every robot is finished.

		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			if (!robot.isFinished())
			{
				simulationFinished = false;
			}
		}

		return simulationFinished;
	}

	/**
	 * Returns a list of {@link Observation} objects, that contains informations about the positions
	 * and the lights of every robot in the system, excluding the caller of the method.
	 * 
	 * @return a list of {@link Observation} objects, that contains informations about the positions
	 *         and the lights of every robot in the system, excluding the caller of the method.
	 * @throws TimelineNotAccessibleException
	 *             if someone tries to access a timeline without permissions.
	 */
	public Vector<Observation<P>> getObservations(SycamoreRobot<P> caller)
	{
		Vector<Observation<P>> observations = new Vector<Observation<P>>();

		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();

			if (robot != caller)
			{
				// ask the robot for the obsevation.
				Observation<P> observation = this.getObservation(robot, caller);

				if (observation != null)
				{
					observations.add(observation);
				}
			}
		}

		// get visibility and filter
		Visibility<P> visibility = caller.getVisibility();

		if (visibility == null)
		{
			return observations;
		}
		else
		{
			return visibility.filter(observations);
		}
	}

	/**
	 * Dispose this engine and all its contents.
	 */
	public void dispose()
	{
		this.robots.clear();

		this.measures.clear();
		this.scheduler = null;
	}

	/**
	 * Add a new element in the lists of robots
	 */
	public void addNewRobotListElement()
	{
		this.getRobots().addRobotRow();
		this.getRobots().addHumanPilotRow();
	}

	/**
	 * Removes a group of robots from the system. The passed index corresponds to the row of both
	 * the robots matrix and the human pilot robots matrix to be deleted. The removed robots are
	 * returned.
	 * 
	 * @param index
	 *            the row of both the robots matrix and the human pilot robots matrix to be deleted.
	 * @return the removed robots
	 */
	public Vector<SycamoreRobot<P>> removeRobotListElement(int index)
	{
		// save robots before removal
		Vector<SycamoreRobot<P>> robotsToRemove = robots.getRobotRow(index);
		robotsToRemove.addAll(robots.getHumanPilotRow(index));

		// remove them
		robots.removeRobotRow(index);
		robots.removeHumanPilotRow(index);

		return robotsToRemove;
	}

	/**
	 * Computes a starting point for a new robot
	 * 
	 * @return a new starting point for a robot.
	 */
	protected abstract P computeStartingPoint();

	/**
	 * Creates a new robot and inserts it inside the appropriate robots matrix. The robot is
	 * inserted into the robots matrix or the human pilot robots matrix depending on the value if
	 * the isHumanPilot field. The index parameter identifies the row of the matrix where to store
	 * the robot, and the other three parameters are used to create it.
	 * 
	 * @param isHumanPilot
	 * @param index
	 * @param color
	 * @param numLights
	 * @param speed
	 * @return
	 */
	public abstract SycamoreRobot<P> createAndAddNewRobotInstance(boolean isHumanPilot, int index, ColorRGBA color, int numLights, float speed);

	/**
	 * Removes a robot (any) from the index-th list. The removed robot is returned and the list is
	 * never deleted, even if it becomes empty.
	 * 
	 * @param index
	 *            the index of the row of of both the robots matrix and the human pilot robots
	 *            matrix where to remove a robot. The list is never deleted, even if it becomes
	 *            empty.
	 * @return
	 */
	public SycamoreRobot<P> removeRobot(boolean isHumanPilot, int index)
	{
		if (isHumanPilot)
		{
			return robots.removeHumanPilot(index, 0);
		}
		else
		{
			return robots.removeRobot(index, 0);
		}
	}

	/**
	 * Creates a new scheduler instance using passed interface, and sets such instance as the
	 * current scheduler in this engine.
	 * 
	 * @param scheduler
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void createAndSetNewSchedulerInstance(Scheduler scheduler) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		if (scheduler != null)
		{
			// create a new instance of the scheduler
			Class<? extends Scheduler> schedulerClass = scheduler.getClass();
			Constructor<?> constructor = schedulerClass.getConstructors()[0];
			SchedulerImpl<P> newInstance = (SchedulerImpl<P>) constructor.newInstance();

			newInstance.setAppEngine(this);
			this.setCurrentScheduler(newInstance);
		}
		else
		{
			this.setCurrentScheduler(null);
		}
	}

	/**
	 * Creates a new Measure instance using passed interface, and adds such instance in the vector
	 * of the current measures in this engine.
	 * 
	 * @param measure
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public abstract void createAndAddNewMeasureInstance(Measure measure) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException;

	/**
	 * Creates a new initial conditions instance using passed interface, and sets such instance as
	 * the current initial conditions in this engine.
	 * 
	 * @param initialConditions
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public abstract void createAndSetNewInitialConditionsInstance(InitialConditions<P> initialConditions) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException;

	/**
	 * Creates a new visibility instance using passed interface, and sets such instance as the
	 * visibility in passed robot. No other robot will be touched.
	 * 
	 * @param visibility
	 * @param robot
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public abstract void createAndSetNewVisibilityInstance(Visibility<P> visibility, SycamoreRobot<P> robot) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException;

	/**
	 * Creates a new memory instance using passed interface, and sets such instance as the memory in
	 * passed robot. No other robot will be touched.
	 * 
	 * @param algorithm
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public abstract void createAndSetNewMemoryInstance(Memory<P> memory, SycamoreRobot<P> robot) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException;

	/**
	 * Creates a new algorithm instance using passed interface, and sets such instance as the
	 * algorithm in the robots of the list identified with passed index. This algorithm will be
	 * applied also the newly created robots of such list, since this moment.
	 * 
	 * @param algorithm
	 * @param index
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public abstract void createAndSetNewAlgorithmInstance(Algorithm<P> algorithm, int index) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException;

	/**
	 * Creates a new algorithm instance using passed interface, and sets such instance as the
	 * algorithm in passed robot. No other robot will be touched.
	 * 
	 * @param algorithm
	 * @param index
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public abstract void createAndSetNewAlgorithmInstance(Algorithm<P> algorithm, SycamoreRobot<P> robot) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException;

	/**
	 * Creates a new agreement instance using passed interface, and sets such instance as the
	 * agreement in passed robot. No other robot will be touched.
	 * 
	 * @param agreement
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public abstract void createAndSetNewAgreementInstance(Agreement<P> agreement, SycamoreRobot<P> robot) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException;

	/**
	 * Returns true if this engine is valid. An engine is valid if there is at least a robot, and if
	 * its current scheduler is set (not null).
	 * 
	 * @return true if this engine is valid, false otherwise.
	 */
	public boolean isValid()
	{
		if (scheduler != null)
		{
			return true;
		}
		return false;
	}

	/**
	 * Reset the simulation data to bring it to the initial configuration
	 */
	public void reset()
	{
		// reset robots
		Iterator<SycamoreRobot<P>> iterator = robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			robot.reset();
		}
	}

	/**
	 * Performs all the operations necessary to manage the ending of a simulation. Concretely it
	 * fires an event to the GUI and eventually resets the system, depending on the value of passed
	 * flag.
	 */
	public void manageSimulationFinished(boolean doReset)
	{
		fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.SIMULATION_FINISHED.name()));
		if (doReset)
		{
			this.reset();
		}
	}

	/**
	 * Call the <code>onSimulationStart()</code> method on all the measures registered in this
	 * engine.
	 */
	public void performMeasuresSimulationStart()
	{
		for (Measure measure : this.measures)
		{
			measure.onSimulationStart();
		}
	}

	/**
	 * Call the <code>onSimulationStep()</code> method on all the measures registered in this
	 * engine.
	 */
	public void performMeasuresSimulationStep()
	{
		for (Measure measure : this.measures)
		{
			measure.onSimulationStep();
		}
	}

	/**
	 * Call the <code>onSimulationEnd()</code> method on all the measures registered in this engine.
	 */
	public void performMeasuresSimulationEnd()
	{
		for (Measure measure : this.measures)
		{
			measure.onSimulationEnd();
		}
	}

	/**
	 * @param visibilityRangesVisible
	 *            the visibilityRangesVisible to set
	 */
	public void setVisibilityRangesVisible(boolean visibilityRangesVisible)
	{
		// update visibility range in robots
		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			robot.updateVisibilityRangesVisible();
		}
	}

	/**
	 * @param movementDirectionsVisible
	 *            the movementDirectionsVisible to set
	 */
	public void setMovementDirectionsVisible(boolean movementDirectionsVisible)
	{
		// update movement directions in robots
		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			robot.updateMovementDirectionsVisible();
		}
	}

	/**
	 * Updates the visibility ranges in all the robots in this engine
	 */
	public void updateVisibilityRange()
	{
		// update visibilities in all robots
		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			VisibilityImpl<P> visibilityImpl = (VisibilityImpl<P>) robot.getVisibility();
			if (visibilityImpl != null)
			{
				visibilityImpl.updateVisibilityGeometry();
			}
		}
	}

	/**
	 * Returns the TYPE enum element corresponding to the type of this engine.
	 * 
	 * @return the TYPE enum element corresponding to the type of this engine.
	 */
	public abstract TYPE getType();

	/**
	 * Returns the number of robots that are in this engine
	 * 
	 * @return
	 */
	public int getRobotsCount()
	{
		return this.robots.size();
	}

	/**
	 * Sets passed color as the color of all the robots in the i-th list. The isHumanPilot flag is
	 * used to determine which robots set is to be used.
	 * 
	 * @param i
	 * @param color
	 * @param isHumanPilot
	 */
	public void setRobotsColor(int i, ColorRGBA color, boolean isHumanPilot)
	{
		Vector<SycamoreRobot<P>> robotsList = isHumanPilot ? robots.getHumanPilotRow(i) : this.robots.getRobotRow(i);
		for (SycamoreRobot<P> robot : robotsList)
		{
			robot.setColor(color);
		}
	}

	/**
	 * Sets passed value as the max number of supported lights in all the robots in the i-th list.
	 * The isHumanPilot flag is used to determine which robots set is to be used.
	 * 
	 * @param i
	 * @param value
	 */
	public void setRobotsMaxLights(int i, Integer value, boolean isHumanPilot)
	{
		Vector<SycamoreRobot<P>> robotsList = isHumanPilot ? robots.getHumanPilotRow(i) : this.robots.getRobotRow(i);
		for (SycamoreRobot<P> robot : robotsList)
		{
			robot.setMaxLights(value);
		}
	}

	/**
	 * Sets passed value as the max number of supported lights in all the robots in the i-th list.
	 * The isHumanPilot flag is used to determine which robots set is to be used.
	 * 
	 * @param i
	 * @param value
	 */
	public void setRobotsSpeed(int i, Float value, boolean isHumanPilot)
	{
		Vector<SycamoreRobot<P>> robotsList = isHumanPilot ? robots.getHumanPilotRow(i) : this.robots.getRobotRow(i);
		for (SycamoreRobot<P> robot : robotsList)
		{
			robot.setSpeed(value);
		}
	}

	/**
	 * Performs a snapshot of all the ratios of all the timelines in the system. This snapshot is
	 * used by the manual-control of the system to correctly compute the percentages of the ratios.
	 */
	public synchronized void makeRatioSnapshot()
	{
		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			this.ratioSnapshot.put(robot, robot.getCurrentRatio());
		}
	}

	/**
	 * Clears the data structure that contains the snapshots of the ratios.
	 */
	public synchronized void clearRatioSnapshot()
	{
		this.ratioSnapshot.clear();
	}

	/**
	 * Modifies all the ratios of all the animated objects in the system, by putting as their
	 * current ratio a percentage of the ratio that they have at the moment of the call of this
	 * method. In order to have this method behave correctly, it is important that a snapshot of the
	 * original ratios is made before a sequence of calls to the<code>setRobotRatioPercentage</code>
	 * method. The <code>makeRatioSnapshot()</code> method takes care of this.
	 * 
	 * @param percentage
	 */
	public synchronized void setRobotRatioPercentage(float percentage)
	{
		Iterator<SycamoreRobot<P>> iterator = this.robots.iterator();
		while (iterator.hasNext())
		{
			SycamoreRobot<P> robot = iterator.next();
			Float originalRatio = this.ratioSnapshot.get(robot);
			if (originalRatio != null)
			{
				float newRatio = (originalRatio / 100.0f) * percentage;
				robot.setCurrentRatio(newRatio);
			}
		}
	}

	/**
	 * Encode this object to XML format. The encoded Element will contain all data necessary to
	 * re-create and object that is equal to this one.
	 * 
	 * @return an XML Element containing the XML description of this object.
	 */
	public synchronized Element encode(DocumentBuilderFactory factory, DocumentBuilder builder, Document document)
	{
		// create element
		Element element = document.createElement("SycamoreEngine");

		// children
		Element robotsElem = document.createElement("robots");
		robotsElem.appendChild(robots.encode(factory, builder, document));

		Element animationSpeedMultiplierElem = document.createElement("animationSpeedMultiplier");
		animationSpeedMultiplierElem.appendChild(document.createTextNode(animationSpeedMultiplier + ""));

		// append children
		element.appendChild(robotsElem);
		element.appendChild(animationSpeedMultiplierElem);

		// the following children could be null
		if (initialConditions != null)
		{
			Element initialConditionsElem = document.createElement("initialConditions");
			initialConditionsElem.appendChild(document.createTextNode(initialConditions.getPluginName() + ""));
			element.appendChild(initialConditionsElem);
		}

		if (scheduler != null)
		{
			Element schedulerElem = document.createElement("scheduler");
			schedulerElem.appendChild(document.createTextNode(scheduler.getPluginName() + ""));
			element.appendChild(schedulerElem);
		}

		Element measuresElem = document.createElement("measures");
		for (int i = 0; i < measures.size(); i++)
		{
			Measure measure = measures.elementAt(i);
			Element measureElem = document.createElement("measure");
			measureElem.appendChild(document.createTextNode(measure.getPluginName() + ""));
			element.appendChild(measureElem);
		}
		element.appendChild(measuresElem);

		return element;
	}

	/**
	 * Decode the fields in this Engine by taking them from passed XML element. TYPE parameter is
	 * used to determine the type (2D or 3D) of the decoded object.
	 * 
	 * @param documentElement
	 */
	public synchronized boolean decode(Element element, TYPE type)
	{
		boolean success = true;
		NodeList nodes = element.getElementsByTagName("SycamoreEngine");

		// if there is at least a SycamoreEngine node, decode it
		if (nodes.getLength() > 0)
		{
			// decode fields
			success = success && this.robots.decode(element, type, this);

			// get values
			NodeList animationSpeedMultiplier = element.getElementsByTagName("animationSpeedMultiplier");
			if (animationSpeedMultiplier.getLength() > 0)
			{
				Element animationSpeedMultiplierElem = (Element) animationSpeedMultiplier.item(0);
				this.animationSpeedMultiplier = Float.parseFloat(animationSpeedMultiplierElem.getTextContent());
			}

			try
			{
				// initial conditions
				NodeList initialConditions = element.getElementsByTagName("initialConditions");
				if (initialConditions.getLength() > 0)
				{
					Element initialConditionsElem = (Element) initialConditions.item(0);
					String initialConditionsName = initialConditionsElem.getTextContent();

					// get loaded plugins
					ArrayList<InitialConditions> loaded = SycamorePluginManager.getSharedInstance().getLoadedInitialConditions();
					for (InitialConditions plugin : loaded)
					{
						if (plugin.getPluginName().equals(initialConditionsName))
						{
							// create the new
							this.createAndSetNewInitialConditionsInstance(plugin);
							break;
						}
					}
				}

				// scheduler
				NodeList scheduler = element.getElementsByTagName("scheduler");
				if (scheduler.getLength() > 0)
				{
					Element schedulerElem = (Element) scheduler.item(0);
					String schedulerName = schedulerElem.getTextContent();

					// get loaded plugins
					ArrayList<Scheduler> loaded = SycamorePluginManager.getSharedInstance().getLoadedSchedulers();
					for (Scheduler plugin : loaded)
					{
						if (plugin.getPluginName().equals(schedulerName))
						{
							// create the new
							this.createAndSetNewSchedulerInstance(plugin);
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}

		return success;
	}
}
