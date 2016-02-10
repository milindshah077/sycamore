package it.diunipi.volpi.sycamore.engine;

import it.diunipi.volpi.sycamore.animation.SycamoreAnimatedObject;
import it.diunipi.volpi.sycamore.animation.Timeline;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.gui.SycamoreSystem;
import it.diunipi.volpi.sycamore.gui.SycamoreSystem.TIMELINE_MODE;
import it.diunipi.volpi.sycamore.jmescene.SycamoreJMEScene;
import it.diunipi.volpi.sycamore.plugins.SycamorePluginManager;
import it.diunipi.volpi.sycamore.plugins.agreements.Agreement;
import it.diunipi.volpi.sycamore.plugins.agreements.AgreementImpl;
import it.diunipi.volpi.sycamore.plugins.algorithms.Algorithm;
import it.diunipi.volpi.sycamore.plugins.memory.Memory;
import it.diunipi.volpi.sycamore.plugins.visibilities.Visibility;
import it.diunipi.volpi.sycamore.util.ApplicationProperties;
import it.diunipi.volpi.sycamore.util.PropertyManager;
import it.diunipi.volpi.sycamore.util.SubsetFairnessSupporter;
import it.diunipi.volpi.sycamore.util.SycamoreFiredActionEvents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * This class represents a robot in the system. It has a timeline and it cam nove in the enviroment.
 * It is represented in the JME system with a spatial. A robots has an algorithm that it executes.
 * It also have some lights, a visibility, a memory and a speed. The robot can have some states,
 * defined in an enumeration
 * 
 * @author Valerio Volpi - vale.v@me.com
 * 
 * @param <P>
 * @param <O>
 */
public abstract class SycamoreRobot<P extends SycamoreAbstractPoint & ComputablePoint<P>> extends SycamoreAnimatedObject<P> implements SubsetFairnessSupporter, SycamoreObservedRobot<P>
{
	/**
	 * The possible states of the robot
	 * 
	 * @author Valerio Volpi - volpiv@cli.di.unipi.it
	 */
	public static enum ROBOT_STATE
	{
		READY_TO_LOOK, LOOKING, READY_TO_COMPUTE, COMPUTING, READY_TO_MOVE, MOVING, FINISHED;
	}

	// constants
	protected static final float				geometrySize		= 0.25f;
	protected static final float				lightSize			= 0.1f;
	protected static final ColorRGBA			glassColor			= new ColorRGBA(1.0f, 1.0f, 1.0f, 0.75f);

	// utility data
	private final SycamoreSystemMemory<P>		systemMemory;
	private final Vector<SycamoreRobotLight<P>>	lights;
	private long								ID;

	// model data
	protected SycamoreEngine<P>					engine				= null;
	protected Algorithm<P>						algorithm			= null;
	protected Visibility<P>						visibility			= null;
	protected Memory<P>							memory				= null;
	protected Agreement<P>						agreement			= null;

	// JME data
	protected Node								robotNode			= null;
	protected Geometry							sceneGeometry		= null;
	protected Geometry							directionGeometry	= null;
	protected ColorRGBA							color				= null;

	// state informations
	protected int								maxLights			= 0;
	protected float								speed				= 0;
	private ROBOT_STATE							currentState		= ROBOT_STATE.READY_TO_LOOK;
	private int									currentLights		= 0;

	// intermedate operations data
	private Vector<Observation<P>>				snapshot			= null;

	private Vector<ActionListener>				listeners			= null;

	// previous point visited--- added by Milind Shah 
	public Point2D 								previousPoint		= null;
	/**
	 * Default constructor.
	 * 
	 * @param algorithm
	 */
	public SycamoreRobot(SycamoreEngine<P> engine, P startingPosition)
	{
		this(engine, startingPosition, PropertyManager.getSharedInstance().getFloatProperty(ApplicationProperties.DEFAULT_ROBOT_SPEED), ColorRGBA.Red, 0);
		previousPoint = new Point2D();   //default value
	}

	/**
	 * Default constructor.
	 * 
	 * @param algorithm
	 */
	public SycamoreRobot(SycamoreEngine<P> engine, P startingPosition, ColorRGBA color, int maxLights)
	{
		this(engine, startingPosition, PropertyManager.getSharedInstance().getFloatProperty(ApplicationProperties.DEFAULT_ROBOT_SPEED), color, maxLights);
	}

	/**
	 * Default constructor.
	 * 
	 * @param algorithm
	 */
	public SycamoreRobot(final SycamoreEngine<P> engine, P startingPosition, float speed, final ColorRGBA color, final int maxLights)
	{
		this.engine = engine;
		this.maxLights = maxLights;
		this.systemMemory = new SycamoreSystemMemory<P>();
		this.lights = new Vector<SycamoreRobotLight<P>>();

		this.speed = speed;

		// setup timeline
		this.timeline = new Timeline<P>();

		this.startingPosition = startingPosition;
		this.timeline.addKeyframe(startingPosition);
		this.setCurrentRatio(0);

		this.color = color;

		SycamoreSystem.enqueueToJMEandWait(new Callable<Object>()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Object call() throws Exception
			{
				// stup geometries
				setupGeometry();

				// add default black lights
				for (int i = 0; i < maxLights; i++)
				{
					final SycamoreRobotLight light = createNewLightInstance();
					lights.add(light);

					if (SycamoreSystem.isRobotsLightsVisible())
					{
						robotNode.attachChild(light.getLightGeometry());
					}
				}

				return null;
			}
		});

		// assign random ID
		SecureRandom random = new SecureRandom();
		this.ID = random.nextLong();
	}

	protected abstract SycamoreRobotLight<P> createNewLightInstance();

	/**
	 * Adds an <code>ActionListener</code> to the button.
	 * 
	 * @param listener
	 *            the <code>ActionListener</code> to be added
	 */
	public void addActionListener(ActionListener listener)
	{
		if (this.listeners == null)
		{
			this.listeners = new Vector<ActionListener>();
		}
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
		if (this.listeners == null)
		{
			this.listeners = new Vector<ActionListener>();
		}
		this.listeners.remove(listener);
	}

	/**
	 * Fires passed ActionEvent to all registered listeners, by calling <code>ActionPerformed</code>
	 * method on all of them.
	 * 
	 * @param e
	 */
	protected void fireActionEvent(ActionEvent e)
	{
		if (this.listeners != null)
		{
			for (ActionListener listener : this.listeners)
			{
				listener.actionPerformed(e);
			}
		}
	}

	/**
	 * @return
	 */
	public P getGlobalPosition()
	{
		if (this.agreement != null)
		{
			return this.agreement.toGlobalCoordinates(getLocalPosition());
		}
		else
		{
			return this.getLocalPosition();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.animation.SycamoreAnimatedObject#setCurrentRatio(float)
	 */
	@Override
	public synchronized void setCurrentRatio(float currentRatio)
	{
		if (currentRatio != this.getCurrentRatio())
		{
			super.setCurrentRatio(currentRatio);
			fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.ROBOT_RATIO_CHANGED.name()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.animation.SycamoreAnimatedObject#setDirection(it.diunipi.volpi.
	 * sycamore.model.SycamoreAbstractPoint)
	 */
	@Override
	public void setDirection(P direction)
	{
		super.setDirection(direction);
		updateDirectionGeometry();
	}

	/* (non-Javadoc)
	 * @see it.diunipi.volpi.sycamore.engine.SycamoreObservedRobot#setSpeed(float)
	 */
	@Override
	public void setSpeed(float speed)
	{
		this.speed = speed;
	}

	/**
	 * Updates the geometry that represents the direction (the arrow) in order to reflect a change
	 * in the direction vector.
	 */
	private void updateDirectionGeometry()
	{
		if (getDirection() != null && this.sceneGeometry != null)
		{
			P position = this.getLocalPosition();
			P direction = getDirection();

			if (!position.equals(direction))
			{
				float[] angles = this.getLocalPosition().getRotationAngles(getDirection());
				final Quaternion rotation = new Quaternion(angles);

				SycamoreSystem.enqueueToJME(new Callable<Object>()
				{
					@Override
					public Object call() throws Exception
					{
						robotNode.setLocalRotation(rotation);
						return null;
					}
				});

				fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.ROBOT_DIRECTION_CHANGED.name()));
			}
		}
	}

	/**
	 * @return the directionGeometry
	 */
	public Geometry getDirectionGeometry()
	{
		return directionGeometry;
	}

	/**
	 * @param maxLights
	 *            the maxLights to set
	 */
	public synchronized void setMaxLights(final int maxLights)
	{
		final int oldMaxLights = this.maxLights;
		this.maxLights = maxLights;

		if (maxLights > oldMaxLights)
		{
			SycamoreSystem.enqueueToJMEandWait(new Callable<Object>()
			{
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.util.concurrent.Callable#call()
				 */
				@Override
				public Object call() throws Exception
				{
					int lightsNum = maxLights - oldMaxLights;

					// add missing off lights
					for (int i = 0; i < lightsNum; i++)
					{
						final SycamoreRobotLight light = createNewLightInstance();
						lights.add(light);

						if (SycamoreSystem.isRobotsLightsVisible())
						{
							robotNode.attachChild(light.getLightGeometry());
						}
					}

					return null;
				}
			});
		}
		else
		{
			SycamoreSystem.enqueueToJMEandWait(new Callable<Object>()
			{
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.util.concurrent.Callable#call()
				 */
				@Override
				public Object call() throws Exception
				{
					int lightsNum = oldMaxLights - maxLights;

					// add missing off lights
					for (int i = 0; i < lightsNum; i++)
					{
						SycamoreRobotLight light = lights.remove(lights.size() - 1);
						robotNode.detachChild(light.getLightGeometry());
					}

					return null;
				}
			});
		}
	}

	/**
	 * Setup the geometry that represents this robot.
	 */
	protected abstract void setupGeometry();

	/**
	 * Returns a geometry that represents a light of this robot Returns a new instance at each call
	 */
	protected abstract Geometry getNewLightGeometry(ColorRGBA color);

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.util.SubsetFairnessSupporter#getID()
	 */
	@Override
	public long getID()
	{
		return ID;
	}

	/**
	 * @return algorithm
	 */
	public Algorithm<P> getAlgorithm()
	{
		return algorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.model.SycamoreObservedRobot#getVisibility()
	 */
	@Override
	public Visibility<P> getVisibility()
	{
		return visibility;
	}

	/**
	 * @return the memory
	 */
	public Memory<P> getMemory()
	{
		return memory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.model.SycamoreObservedRobot#getLights()
	 */
	@Override
	public Vector<SycamoreObservedLight> getLights()
	{
		Vector<SycamoreObservedLight> obsLights = new Vector<SycamoreObservedLight>();
		for (SycamoreRobotLight<P> light : this.lights)
		{
			obsLights.add(light);
		}

		return obsLights;
	}

	/**
	 * @return the agreement
	 */
	public Agreement<P> getAgreement()
	{
		return agreement;
	}

	/**
	 * @param algorithm
	 *            the algorithm to set
	 */
	public synchronized void setAlgorithm(Algorithm<P> algorithm)
	{
		this.algorithm = algorithm;
	}

	/**
	 * @param visibilitytoSet
	 *            the visibility to set
	 */
	public synchronized void setVisibility(final Visibility<P> visibilitytoSet)
	{
		// detach current geometry
		if (this.visibility != null)
		{
			final Visibility<P> backupVisibility = this.visibility;
			SycamoreSystem.enqueueToJME(new Callable<Object>()
			{
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.util.concurrent.Callable#call()
				 */
				@Override
				public Object call() throws Exception
				{
					robotNode.detachChild(backupVisibility.getVisibilityRangeGeometry());
					return null;
				}
			});

		}

		// set visibility
		this.visibility = visibilitytoSet;

		SycamoreSystem.enqueueToJME(new Callable<Object>()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Object call() throws Exception
			{

				if (SycamoreSystem.isVisibilityRangesVisible() && visibilitytoSet != null && visibilitytoSet.getVisibilityRangeGeometry() != null)
				{
					robotNode.attachChild(visibilitytoSet.getVisibilityRangeGeometry());
				}

				return null;
			}
		});
	}

	/**
	 * @param memory
	 *            the memory to set
	 */
	public synchronized void setMemory(Memory<P> memory)
	{
		memory.setSystemMemory(systemMemory);
		this.memory = memory;
	}

	/**
	 * @param agreement
	 *            the agreement to set
	 */
	public void setAgreement(AgreementImpl<P> agreement)
	{
		this.agreement = agreement;
		if (agreement != null)
		{
			this.agreement.setRobot(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.model.SycamoreObservedRobot#isFinished()
	 */
	@Override
	public boolean isFinished()
	{
		if (algorithm != null)
		{
			return (this.currentState == ROBOT_STATE.FINISHED);
		}
		else
			return true;
	}


	/* (non-Javadoc)
	 * @see it.diunipi.volpi.sycamore.engine.SycamoreObservedRobot#getSpeed()
	 */
	@Override
	public float getSpeed()
	{
		return speed;
	}

	/**
	 * @return the robotNode
	 */
	public Node getRobotNode()
	{
		return robotNode;
	}

	/**
	 * @return the duration of the timeline
	 */
	public float getTimelineDuration()
	{
		return this.timeline.getDuration();
	}

	/**
	 * @return currentState
	 */
	public ROBOT_STATE getCurrentState()
	{
		return currentState;
	}

	/**
	 * @param currentState
	 *            the currentState to set
	 */
	public synchronized void setCurrentState(ROBOT_STATE currentState)
	{
		this.currentState = currentState;
	}

	/**
	 * @return the color
	 */
	public ColorRGBA getColor()
	{
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public abstract void setColor(final ColorRGBA color);

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.model.SycamoreObservedRobot#addLight(com.jme3.math.ColorRGBA)
	 */
	@Override
	public synchronized void turnLightOn(ColorRGBA color) throws TooManyLightsException
	{
		turnLightOn(color, -1.0f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.engine.SycamoreObservedRobot#turnLightOn(com.jme3.math.ColorRGBA,
	 * float)
	 */
	@Override
	public void turnLightOn(ColorRGBA color, float intensity) throws TooManyLightsException
	{
		if (currentLights == maxLights)
		{
			throw new TooManyLightsException("The number of lights added to robots exceed the defined maximum.");
		}
		else
		{
			SycamoreRobotLight light = lights.elementAt(currentLights);
			light.setColor(color);
			light.setIntensity(intensity);
			currentLights++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.model.SycamoreObservedRobot#addLight(com.jme3.math.ColorRGBA)
	 */
	@Override
	public synchronized void turnLightOff()
	{
		if (currentLights > 0)
		{
			currentLights--;
			SycamoreRobotLight light = lights.elementAt(currentLights);
			light.setColor(glassColor);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.engine.SycamoreObservedRobot#setLightIntensity(float)
	 */
	@Override
	public void setLightIntensity(float intensity)
	{
		SycamoreRobotLight light = lights.elementAt(currentLights);
		light.setIntensity(intensity);
	}

	/**
	 * Performs a look. Stores the vector of observations of the other robots in the system inside
	 * the robots temporary storage, and changes the robot state properly.
	 */
	public synchronized void doLook()
	{
		if (getCurrentState() == ROBOT_STATE.READY_TO_LOOK)
		{
			setCurrentState(ROBOT_STATE.LOOKING);

			this.snapshot = engine.getObservations(this);

			// shuffle observations to change their oder
			Collections.shuffle(this.snapshot);

			// if there is a mmory set, save data
			if (memory != null)
			{
				this.systemMemory.addSelfPosition(getLocalPosition());
				this.systemMemory.addSnapshot(snapshot);
			}

			setCurrentState(ROBOT_STATE.READY_TO_COMPUTE);
			fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.ROBOT_DID_LOOK.name()));
		}
	}

	/**
	 * Performs a compute. Uses the observations of the system obtained in last call of the
	 * <code>doLook()</code> method to compute a new destination point for the robot. The
	 * destination point is computed by the Algorithm object stored in this robot. After the
	 * destination has been computed, this method modifies the timeline and the state of the robot
	 * properly.
	 */
	public synchronized void doCompute()
	{
		if (algorithm != null && getCurrentState() == ROBOT_STATE.READY_TO_COMPUTE && !isFinished())
		{
			setCurrentState(ROBOT_STATE.COMPUTING);

			P destination = algorithm.compute(snapshot, this);

			// if destination is not null, add it to the timeline
			if (destination != null)
			{
				P lastPoint = timeline.getLastPoint();
				float distance = lastPoint.distanceTo(destination);

				if (Math.abs(distance) < SycamoreSystem.getEpsilon())
				{
					distance = 0;
				}

				if (distance > 0)
				{
					this.timeline.addKeyframe(destination, (distance / this.getSpeed()));
					if (SycamoreSystem.getTimelineMode() == TIMELINE_MODE.LIVE)
					{
						this.timeline.clearIntermediate();
					}

					int keyframes = this.timeline.getNumKeyframes();
					if (keyframes == 1 || keyframes == 2)
					{
						this.setCurrentRatio(0);
					}
					else
					{
						// for sure there are 3 or more keyframes,
						this.setCurrentRatio(this.timeline.getRatioOfKeyframe(keyframes - 2));
					}
				}
			}

			if (algorithm.isFinished())
			{
				setCurrentState(ROBOT_STATE.FINISHED);
			}
			else
			{
				setCurrentState(ROBOT_STATE.READY_TO_MOVE);
			}
			fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.ROBOT_DID_COMPUTE.name()));
		}
	}

	/**
	 * Performs a move. Uses the destination computed in last call of the <code>doCompute()</code>
	 * method, alteady stored into teh robot's timeline, and starts following the timeline in order
	 * to reach such destination.
	 */
	public synchronized void doMove()
	{
		if (this.currentState == ROBOT_STATE.READY_TO_MOVE)
		{
			if (this.snapshot != null)
			{
				this.snapshot.clear();
				this.snapshot = null;
			}

			setCurrentState(ROBOT_STATE.MOVING);
			fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.ROBOT_DID_MOVE.name()));
		}
	}

	/**
	 * Returns true if the robot is moving, false otherwise.
	 * 
	 * @return
	 */
	public boolean isMoving()
	{
		return this.currentState == ROBOT_STATE.MOVING;
	}

	/**
	 * Performs the next operation, that is look, compute or move depending on current state
	 */
	public synchronized void nextOperation()
	{
		if (currentState == ROBOT_STATE.READY_TO_LOOK)
		{
			doLook();
		}
		else if (currentState == ROBOT_STATE.READY_TO_COMPUTE)
		{
			doCompute();
		}
		else
		{
			doMove();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String description = "SycamoreRobot " + this.ID + "\n";
		String timeline = "Timeline: \n" + this.timeline.toString() + "\n";

		return description + timeline;
	}

	/**
	 * Reset the robot by bringing it to the initial configurations. The timeline is reset also.
	 */
	public synchronized void reset()
	{
		this.setCurrentRatio(0);

		if (algorithm != null)
		{
			algorithm.reset();
		}

		timeline.clear();
		systemMemory.reset();

		timeline = new Timeline<P>();
		timeline.addKeyframe(startingPosition);
		setCurrentRatio(0);

		currentLights = 0;
		for (SycamoreRobotLight light : lights)
		{
			light.setColor(glassColor);
		}

		currentState = ROBOT_STATE.READY_TO_LOOK;
	}

	/**
	 * Updates the lights geometries by attaching or detaching them from JME scene if they are
	 * declared visible or not in the engine.
	 */
	public synchronized void updateLightsVisible()
	{
		SycamoreSystem.enqueueToJME(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				if (!SycamoreSystem.isRobotsLightsVisible())
				{
					for (SycamoreRobotLight light : lights)
					{
						Geometry lightGeom = light.getLightGeometry();
						if (robotNode.hasChild(lightGeom))
						{
							robotNode.detachChild(lightGeom);
						}
					}
				}
				else
				{
					for (SycamoreRobotLight light : lights)
					{
						Geometry lightGeom = light.getLightGeometry();
						if (!robotNode.hasChild(lightGeom))
						{
							robotNode.attachChild(lightGeom);
						}
					}
				}

				return null;
			}
		});
	}

	/**
	 * Updates the visibility rages geometries by attaching or detaching them from JME scene if they
	 * are declared visible or not in the engine.
	 */
	public synchronized void updateVisibilityRangesVisible()
	{
		SycamoreSystem.enqueueToJME(new Callable<Object>()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Object call() throws Exception
			{
				if (engine != null && visibility != null)
				{
					if (visibility.getVisibilityRangeGeometry() != null)
					{
						if (SycamoreSystem.isVisibilityRangesVisible())
						{
							robotNode.attachChild(visibility.getVisibilityRangeGeometry());
						}
						else
						{
							robotNode.detachChild(visibility.getVisibilityRangeGeometry());
						}
					}
				}
				return null;
			}
		});
	}

	/**
	 * Updates the movement directions geometries by attaching or detaching them from JME scene if
	 * they are declared visible or not in the engine.
	 */
	public synchronized void updateMovementDirectionsVisible()
	{
		SycamoreSystem.enqueueToJME(new Callable<Object>()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Object call() throws Exception
			{
				if (engine != null)
				{
					if (SycamoreSystem.isMovementDirectionsVisible())
					{
						robotNode.attachChild(directionGeometry);
					}
					else
					{
						robotNode.detachChild(directionGeometry);
					}
				}
				return null;
			}
		});
	}

	/**
	 * Adds a pause of delta time unit in the timeline of this robot.
	 * 
	 * @param delta
	 */
	public synchronized void addPause(float delta)
	{
		this.timeline.addPause(delta);
	}

	/**
	 * Returns the whole timeline of this robot. Be careful with this method since a wrong
	 * modification of the timeline could lead the system in an unconsisten state.
	 * 
	 * @return
	 */
	public synchronized Timeline<P> getTimeline()
	{
		return timeline;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.model.SycamoreObservedRobot#getN()
	 */
	@Override
	public int getN() throws NNotKnownException
	{
		return SycamoreSystem.getN();
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
		Element element = document.createElement("SycamoreRobot");

		// parent
		Element parentElem = document.createElement("SycamoreRobot_parent");
		parentElem.appendChild(super.encode(factory, builder, document));
		element.appendChild(parentElem);

		// children
		Element systemMemoryElem = document.createElement("systemMemory");
		systemMemoryElem.appendChild(systemMemory.encode(factory, builder, document));

		Element lightsElem = document.createElement("lights");
		for (int i = 0; i < lights.size(); i++)
		{
			Element lightElem = document.createElement("light");

			SycamoreRobotLight<P> light = lights.elementAt(i);
			lightElem.appendChild(light.encode(factory, builder, document));
			lightsElem.appendChild(lightElem);
		}

		Element IDElem = document.createElement("ID");
		IDElem.appendChild(document.createTextNode(ID + ""));

		Element colorElem = document.createElement("color");
		colorElem.appendChild(SycamoreJMEScene.encodeColorRGBA(color, factory, builder, document));

		Element maxLightsElem = document.createElement("maxLights");
		maxLightsElem.appendChild(document.createTextNode(maxLights + ""));

		Element speedElem = document.createElement("speed");
		speedElem.appendChild(document.createTextNode(speed + ""));

		Element currentStateElem = document.createElement("currentState");
		currentStateElem.appendChild(document.createTextNode(currentState + ""));

		Element currentLightsElem = document.createElement("currentLights");
		currentLightsElem.appendChild(document.createTextNode(currentLights + ""));

		// append children
		element.appendChild(systemMemoryElem);
		element.appendChild(lightsElem);
		element.appendChild(IDElem);
		element.appendChild(colorElem);
		element.appendChild(maxLightsElem);
		element.appendChild(speedElem);
		element.appendChild(currentStateElem);
		element.appendChild(currentLightsElem);

		// the following children could be null
		if (algorithm != null)
		{
			Element algorithmElem = document.createElement("algorithm");
			algorithmElem.appendChild(document.createTextNode(algorithm.getPluginName() + ""));
			element.appendChild(algorithmElem);
		}

		if (visibility != null)
		{
			Element visibilityElem = document.createElement("visibility");
			visibilityElem.appendChild(document.createTextNode(visibility.getPluginName() + ""));
			element.appendChild(visibilityElem);
		}

		if (memory != null)
		{
			Element memoryElem = document.createElement("memory");
			memoryElem.appendChild(document.createTextNode(memory.getPluginName() + ""));
			element.appendChild(memoryElem);
		}

		if (agreement != null)
		{
			Element agreementElem = document.createElement("agreement");
			agreementElem.appendChild(document.createTextNode(agreement.getPluginName() + ""));
			element.appendChild(agreementElem);
		}

		return element;
	}

	/**
	 * Decode the fields in this robot by taking them from passed XML element. TYPE parameter is
	 * used to determine the type (2D or 3D) of the decoded object.
	 * 
	 * @param documentElement
	 */
	public synchronized boolean decode(final Element element, final TYPE type)
	{
		boolean success = true;
		NodeList nodes = element.getElementsByTagName("SycamoreRobot");

		// if there is at least a SycamoreRobotMatrix node, decode it
		if (nodes.getLength() > 0)
		{
			// parent
			NodeList parent = element.getElementsByTagName("SycamoreRobot_parent");
			if (parent.getLength() > 0)
			{
				Element parentElem = (Element) parent.item(0);
				success = success && super.decode(parentElem, type);
			}

			// systemMemory
			NodeList systemMemory = element.getElementsByTagName("systemMemory");
			if (systemMemory.getLength() > 0)
			{
				Element systemMemoryElem = (Element) systemMemory.item(0);
				success = success && this.systemMemory.decode(systemMemoryElem, type);
			}

			// ID
			NodeList ID = element.getElementsByTagName("ID");
			if (ID.getLength() > 0)
			{
				Element IDElem = (Element) ID.item(0);
				this.ID = Long.parseLong(IDElem.getTextContent());
			}

			// color
			NodeList color = element.getElementsByTagName("color");
			if (color.getLength() > 0)
			{
				Element colorElem = (Element) color.item(0);
				this.setColor(SycamoreJMEScene.decodeColorRGBA(colorElem));
			}

			// maxLights
			NodeList maxLights = element.getElementsByTagName("maxLights");
			if (maxLights.getLength() > 0)
			{
				Element maxLightsElem = (Element) maxLights.item(0);
				this.setMaxLights(Integer.parseInt(maxLightsElem.getTextContent()));
			}

			// speed
			NodeList speed = element.getElementsByTagName("speed");
			if (speed.getLength() > 0)
			{
				Element speedElem = (Element) speed.item(0);
				this.speed = Float.parseFloat(speedElem.getTextContent());
			}

			// currentState
			NodeList currentState = element.getElementsByTagName("currentState");
			if (currentState.getLength() > 0)
			{
				Element currentStateElem = (Element) currentState.item(0);
				this.currentState = ROBOT_STATE.valueOf(currentStateElem.getTextContent());
			}

			// currentLights
			NodeList currentLights = element.getElementsByTagName("currentLights");
			if (currentLights.getLength() > 0)
			{
				Element currentLightsElem = (Element) currentLights.item(0);
				this.currentLights = Integer.parseInt(currentLightsElem.getTextContent());
			}

			SycamoreSystem.enqueueToJMEandWait(new Callable<Object>()
			{
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.util.concurrent.Callable#call()
				 */
				@Override
				public Object call() throws Exception
				{
					// lights
					NodeList lightsList = element.getElementsByTagName("lights");
					if (lightsList.getLength() > 0)
					{
						Element lightsElem = (Element) lightsList.item(0);

						// single light
						NodeList light = lightsElem.getElementsByTagName("light");
						for (int i = 0; i < light.getLength(); i++)
						{
							Element lightElem = (Element) light.item(i);
							SycamoreRobotLight<P> robotLight = lights.elementAt(i);

							robotLight.decode(lightElem, type);
						}
					}

					return null;
				}
			});

			try
			{
				// algorithm
				NodeList algorithm = element.getElementsByTagName("algorithm");
				if (algorithm.getLength() > 0)
				{
					Element algorithmElem = (Element) algorithm.item(0);
					String algorithmName = algorithmElem.getTextContent();

					// get loaded plugins
					ArrayList<Algorithm> loaded = SycamorePluginManager.getSharedInstance().getLoadedAlgorithms();
					for (Algorithm plugin : loaded)
					{
						if (plugin.getPluginName().equals(algorithmName))
						{
							// create the new
							engine.createAndSetNewAlgorithmInstance(plugin, this);
							break;
						}
					}
				}

				// visibility
				NodeList visibility = element.getElementsByTagName("visibility");
				if (visibility.getLength() > 0)
				{
					Element visibilityElem = (Element) visibility.item(0);
					String visibilityName = visibilityElem.getTextContent();

					// get loaded plugins
					ArrayList<Visibility> loaded = SycamorePluginManager.getSharedInstance().getLoadedVisibilities();
					for (Visibility plugin : loaded)
					{
						if (plugin.getPluginName().equals(visibilityName))
						{
							// create the new
							engine.createAndSetNewVisibilityInstance(plugin, this);
							break;
						}
					}
				}

				// memory
				NodeList memory = element.getElementsByTagName("memory");
				if (memory.getLength() > 0)
				{
					Element memoryElem = (Element) memory.item(0);
					String memoryName = memoryElem.getTextContent();

					// get loaded plugins
					ArrayList<Memory> loaded = SycamorePluginManager.getSharedInstance().getLoadedMemories();
					for (Memory plugin : loaded)
					{
						if (plugin.getPluginName().equals(memoryName))
						{
							// create the new
							engine.createAndSetNewMemoryInstance(plugin, this);
							break;
						}
					}
				}

				// agreement
				NodeList agreement = element.getElementsByTagName("agreement");
				if (agreement.getLength() > 0)
				{
					Element agreementElem = (Element) agreement.item(0);
					String agreementName = agreementElem.getTextContent();

					// get loaded plugins
					ArrayList<Agreement> loaded = SycamorePluginManager.getSharedInstance().getLoadedAgreements();
					for (Agreement plugin : loaded)
					{
						if (plugin.getPluginName().equals(agreementName))
						{
							// create the new
							engine.createAndSetNewAgreementInstance(plugin, this);
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

	/**
	 * @return
	 */
	public boolean isHumanPilot()
	{
		return (algorithm != null && algorithm.isHumanPilot());
	}
}
