package it.diunipi.volpi.sycamore.jmescene;

import it.diunipi.volpi.app.sycamore.SycamoreApp.OsUtils;
import it.diunipi.volpi.sycamore.engine.Point2D;
import it.diunipi.volpi.sycamore.engine.Point3D;
import it.diunipi.volpi.sycamore.engine.SycamoreAbstractPoint;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.engine.SycamoreRobot;
import it.diunipi.volpi.sycamore.gui.SycamoreSystem;
import it.diunipi.volpi.sycamore.plugins.agreements.Agreement;
import it.diunipi.volpi.sycamore.util.SycamoreFiredActionEvents;
import it.diunipi.volpi.sycamore.util.SycamoreUtil;
import it.diunipi.volpi.sycamore.virca.vircaDeviceImpl;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;

/**
 * This is the JME application that represents the Sycamore scene. It is an OpenGL application that
 * draws the robots inside a canvas. The animation is managed by a {@link SycamoreTimer} object.
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
public class SycamoreJMEScene extends SimpleApplication implements ActionListener
{
	/**
	 * This class checks if contenxt is renderable. Sleeps while it is not, and fires a
	 * SCENE_3D_READY event while the context become renderable.
	 * 
	 * @author Valerio Volpi - vale.v@me.com
	 */
	private class ContextChecker extends Thread
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			super.run();

			while (!context.isRenderable())
			{
				try
				{
					// sleep a bit
					Thread.sleep(3000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			// context is now renderable. fire the event.
			fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.JME_SCENE_READY.name()));
		}
	}

	private SycamoreEngine							appEngine			= null;
	private TYPE									currentType			= TYPE.TYPE_3D;

	// spatials
	private Spatial									skyBox				= null;
	private Geometry								xAxis				= null;
	private Geometry								yAxis				= null;
	private Geometry								zAxis				= null;
	private Node									origin				= null;
	private Geometry								baricentrumCross	= null;
	private Node									baricentrum			= null;
	private Node									robotsNode			= null;
	private Node									localAxesNode		= null;
	private ChaseCamera								chaseCam			= null;
	private Node									mainNode			= null;
	private Quaternion								billBoardRotation	= null;
	private Geometry								grid				= null;

	private HashMap<String, String>					caps				= null;
	private Vector<java.awt.event.ActionListener>	listeners			= null;
	private vircaDeviceImpl							vircaDevice			= null;
	public static boolean 							gridCentrePoint     =false;
	
	/**
	 * Default constructor.
	 */
	public SycamoreJMEScene()
	{
		this.listeners = new Vector<java.awt.event.ActionListener>();
		SycamoreSystem.setJmeSceneManager(this);
		if (OsUtils.isWindows()) vircaDevice = new vircaDeviceImpl();
		setupSystemCaps();
		
		
	}

	/**
	 * Setup system capabilities
	 */
	private void setupSystemCaps()
	{
		this.caps = new HashMap<String, String>();
		this.enqueue(new Callable<Object>()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Object call() throws Exception
			{
				caps.put("txt_opengl_version", GL11.glGetString(GL11.GL_VERSION));
				caps.put("txt_gfx_vendor", GL11.glGetString(GL11.GL_VENDOR));
				caps.put("txt_renderer", GL11.glGetString(GL11.GL_RENDERER));
				caps.put("txt_lwjgl_version", Sys.getVersion());

				return null;
			}
		});
	}

	/**
	 * @return the capabilities of the system
	 */
	public HashMap<String, String> getCaps()
	{
		return caps;
	}

	/**
	 * Init the settings of the app
	 */
	private void initSettings()
	{
		// setup settings
		settings.setVSync(true);
		settings.setFrameRate(30);
		settings.setAudioRenderer(null);
		settings.setRenderer(AppSettings.LWJGL_OPENGL1);

		if (SycamoreSystem.getLoggerLevel().intValue() > Level.WARNING.intValue())
		{
			setDisplayFps(false); // to hide the FPS
			setDisplayStatView(false); // to hide the statistics
		}
	}

	/**
	 * Attach the 3 axes on the scene.
	 * 
	 * @param pos
	 */
	private void initCoordinateAxes(Vector3f pos)
	{
		Arrow arrow = new Arrow(new Vector3f(3, 0, 0));
		arrow.setLineWidth(4); // make arrow thicker
		this.xAxis = getGeometryForShape(arrow, ColorRGBA.Red);
		this.xAxis.setLocalTranslation(pos);
		mainNode.attachChild(this.xAxis);

		arrow = new Arrow(new Vector3f(0, 3, 0));
		arrow.setLineWidth(4); // make arrow thicker
		this.yAxis = getGeometryForShape(arrow, ColorRGBA.Green);
		this.yAxis.setLocalTranslation(pos);
		mainNode.attachChild(this.yAxis);

		arrow = new Arrow(new Vector3f(0, 0, 3));
		arrow.setLineWidth(4); // make arrow thicker
		this.zAxis = getGeometryForShape(arrow, ColorRGBA.Blue);
		this.zAxis.setLocalTranslation(pos);
		mainNode.attachChild(this.zAxis);
	}

	/**
	 * Init the baricentrum geometry
	 */
	private void initBaricentrum()
	{
		Material mat = new Material(SycamoreSystem.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");

		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		mat.getAdditionalRenderState().setAlphaTest(true);
		mat.getAdditionalRenderState().setAlphaFallOff(0);

		Texture skyTexture = SycamoreSystem.getAssetManager().loadTexture("it/diunipi/volpi/sycamore/resources/textures/baricentrum.png");
		mat.setTexture("ColorMap", skyTexture);

		baricentrumCross = new Geometry("Cross", new Quad(1f, 1f));
		baricentrumCross.center();

		TangentBinormalGenerator.generate(baricentrumCross.getMesh(), true);
		baricentrumCross.setMaterial(mat);
		baricentrumCross.setQueueBucket(Bucket.Transparent);

		this.baricentrum.addControl(new BillboardControl());
	}

	/**
	 * Given a {@link Shape} object and a color implemented as {@link ColorRGBA} object, returns a
	 * {@link Geometry} object representing passed shape and colored with passed color. The returned
	 * {@link Geometry} is ready to be attached on the Sycamore Scene.
	 * 
	 * @param shape
	 * @param color
	 * @return
	 */
	private Geometry getGeometryForShape(Mesh shape, ColorRGBA color)
	{
		Geometry g = new Geometry("coordinate axis", shape);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(true);
		mat.setColor("Color", color);
		g.setMaterial(mat);
		return g;
	}

	/**
	 * Attaches the grid that represents the floor
	 * 
	 * @param pos
	 * @param size
	 * @param color
	 */
	private void initGrid(int size, ColorRGBA color)
	{
		gridCentrePoint = true;
		
		if(gridCentrePoint)
			grid = new Geometry("wireframe grid", new Grid(size+1, size-1, 1f));
		else
			grid = new Geometry("wireframe grid", new Grid(size, size, 1f));
	
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(true);
		mat.setColor("Color", color);
		grid.setMaterial(mat);
		grid.center();
		mainNode.attachChild(grid);
	}

	/**
	 * Rotates the grid along the x axis of an angle equal to passed one.
	 */
	private void rotateGrid(float angle)
	{
		/* This quaternion stores a 180 degree rolling rotation */
		Quaternion roll180 = new Quaternion();
		roll180.fromAngleAxis(angle, new Vector3f(1, 0, 0));
		grid.setLocalRotation(roll180);
		grid.center();

		rootNode.updateGeometricState();
	}

	/**
	 * Shows/ hides the grid in the scene
	 * 
	 * @param visible
	 */
	public void setGridVisible(final boolean visible)
	{
		this.enqueue(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				if (visible && !mainNode.hasChild(grid))
				{
					mainNode.attachChild(grid);
					mainNode.updateGeometricState();
				}
				else if (!visible && mainNode.hasChild(grid))
				{
					mainNode.detachChild(grid);
					mainNode.updateGeometricState();
				}
				return null;
			}
		});
	}

	/**
	 * Shows/ hides the axes in the scene
	 * 
	 * @param visible
	 */
	public void setAxesVisible(final boolean visible)
	{
		this.enqueue(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				if (visible)
				{
					if (!mainNode.hasChild(xAxis))
					{
						mainNode.attachChild(xAxis);
					}
					if (!mainNode.hasChild(yAxis))
					{
						mainNode.attachChild(yAxis);
					}
					if (!mainNode.hasChild(zAxis) && currentType == TYPE.TYPE_3D)
					{
						mainNode.attachChild(zAxis);
					}
					mainNode.updateGeometricState();
				}
				else
				{
					if (mainNode.hasChild(xAxis))
					{
						mainNode.detachChild(xAxis);
					}
					if (mainNode.hasChild(yAxis))
					{
						mainNode.detachChild(yAxis);
					}
					if (mainNode.hasChild(zAxis))
					{
						mainNode.detachChild(zAxis);
					}
					mainNode.updateGeometricState();
				}
				return null;
			}
		});
	}

	/**
	 * Shows/ hides the baricentrum in the scene
	 * 
	 * @param visible
	 */
	public void setBaricentrumVisible(final boolean visible)
	{
		this.enqueue(new Callable<Object>()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Object call() throws Exception
			{
				if (visible && !baricentrum.hasChild(baricentrumCross))
				{
					baricentrum.attachChild(baricentrumCross);
				}
				else if (!visible && baricentrum.hasChild(baricentrumCross))
				{
					baricentrum.detachChild(baricentrumCross);
				}
				return null;
			}
		});
	}

	/**
	 * Shows/ hides the local coordinates axes in the JME scene
	 * 
	 * @param visible
	 */
	public void setLocalCoordinatesVisible(final boolean visible)
	{
		this.enqueue(new Callable<Object>()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Object call() throws Exception
			{
				if (visible && appEngine != null)
				{
					mainNode.attachChild(localAxesNode);

					Iterator<SycamoreRobot> iterator = appEngine.getRobots().iterator();
					while (iterator.hasNext())
					{
						SycamoreRobot robot = iterator.next();
						Agreement agreement = robot.getAgreement();

						if (agreement != null)
						{
							// attach local coordinates node
							Node axesNode = agreement.getAxesNode();

							axesNode.setLocalTranslation(agreement.getLocalTranslation());
							axesNode.setLocalRotation(agreement.getLocalRotation());
							axesNode.setLocalScale(agreement.getLocalScale());

							localAxesNode.attachChild(axesNode);
						}
					}

					mainNode.updateGeometricState();
				}
				else
				{
					mainNode.detachChild(localAxesNode);
					mainNode.updateGeometricState();
				}

				return null;
			}
		});
	}

	/**
	 * Handle a change in the selection of the current agreement
	 */
	public void manageAgreementChange()
	{
		localAxesNode.detachAllChildren();
		if (SycamoreSystem.isLocalCoordinatesVisible())
		{
			this.setLocalCoordinatesVisible(true);
		}

		updateAgreementsGraphics();
	}

	/**
	 * Update the graphics for Agreements axes and positions
	 */
	public void updateAgreementsGraphics()
	{
		this.enqueue(new Callable<Object>()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.concurrent.Callable#call()
			 */
			@Override
			public Object call() throws Exception
			{
				if (appEngine != null)
				{
					Iterator<SycamoreRobot> iterator = appEngine.getRobots().iterator();
					while (iterator.hasNext())
					{
						SycamoreRobot robot = iterator.next();
						Agreement agreement = robot.getAgreement();

						if (agreement != null)
						{
							// attach local coordinates node
							Node axesNode = agreement.getAxesNode();

							axesNode.setLocalTranslation(agreement.getLocalTranslation());
							axesNode.setLocalRotation(agreement.getLocalRotation());
							axesNode.setLocalScale(agreement.getLocalScale());
						}
						else if (robot.getRobotNode() != null)
						{
							robot.getRobotNode().setLocalRotation(Quaternion.IDENTITY);
							robot.getRobotNode().setLocalScale(1, 1, 1);
							robot.getRobotNode().setLocalTranslation(0, 0, 0);
						}
					}
				}

				return null;
			}
		});
	}

	/**
	 * Init the spatials of the scene
	 */
	private void initSpatials()
	{
		this.mainNode = new Node();
		rootNode.attachChild(mainNode);

		this.billBoardRotation = mainNode.getLocalRotation().clone();

		// Create and load the skybox
		Texture skyTexture = assetManager.loadTexture("it/diunipi/volpi/sycamore/resources/textures/light_gray.png");
		Vector3f normal = new Vector3f(-1, -1, -1);
		this.skyBox = SkyFactory.createSky(assetManager, skyTexture, normal, true);
		mainNode.attachChild(this.skyBox);

		// create the origin
		this.origin = new Node("Origin node");
		mainNode.attachChild(origin);

		// create the baricentrum
		this.baricentrum = new Node("Baricentrum node");
		mainNode.attachChild(baricentrum);

		// create the robots node
		this.robotsNode = new Node("Robots node");
		mainNode.attachChild(robotsNode);

		// create the local axes node
		this.localAxesNode = new Node("Local axes node");
		mainNode.attachChild(localAxesNode);

		// Create and load the 3 axis lines
		initCoordinateAxes(Vector3f.ZERO);
		initGrid(200, ColorRGBA.White);
		initBaricentrum();

		setGridVisible(SycamoreSystem.isGridVisible());
		setAxesVisible(SycamoreSystem.isAxesVisible());
		setBaricentrumVisible(SycamoreSystem.isBaricentrumVisible());
	}

	/**
	 * Init the lights in the scene
	 */
	private void initLights()
	{
		PointLight lamp_light1 = new PointLight();
		lamp_light1.setColor(ColorRGBA.White);
		lamp_light1.setPosition(new Vector3f(200, 200, 200));
		rootNode.addLight(lamp_light1);

		PointLight lamp_light2 = new PointLight();
		lamp_light2.setColor(ColorRGBA.White);
		lamp_light2.setPosition(new Vector3f(0, -200, 0));
		rootNode.addLight(lamp_light2);

		PointLight lamp_light3 = new PointLight();
		lamp_light3.setColor(ColorRGBA.White);
		lamp_light3.setPosition(new Vector3f(-200, 200, -200));
		rootNode.addLight(lamp_light3);
	}

	/**
	 * Init the camera of the 3D version of the scene
	 */
	private void initCamera(Spatial target)
	{
		// Disable the default first-person cam!
		flyCam.setEnabled(false);

		// Enable a chase cam
		chaseCam = new ChaseCamera(cam, target, inputManager);
		// Comment this to disable smooth camera motion
		chaseCam.setSmoothMotion(true);
		chaseCam.setMinDistance(2);
		chaseCam.setDefaultDistance(30);
		chaseCam.setMaxDistance(400);
	}

	/**
	 * Put the camera on the origin of the system
	 */
	public void cameraOnOrigin()
	{
		this.setCameraTarget(origin);
	}

	/**
	 * Put the camera on the baricentrum of the system
	 */
	public void cameraOnBaricentrum()
	{
		this.setCameraTarget(baricentrum);
	}

	/**
	 * Set passed node as the target of the camera
	 * 
	 * @param node
	 */
	private void setCameraTarget(final Node node)
	{
		this.enqueue(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				if (node == origin)
				{
					origin.addControl(chaseCam);
				}
				else
				{
					baricentrum.addControl(chaseCam);
				}

				return null;
			}
		});
	}

	/**
	 * Update the geometries of the robots
	 */
	private void updateRobotsGeometries()
	{
		robotsNode.detachAllChildren();

		if (getAppEngine() != null)
		{
			// update geometries in robots
			Iterator<SycamoreRobot> iterator = getAppEngine().getRobots().iterator();
			while (iterator.hasNext())
			{
				SycamoreRobot robot = iterator.next();
				if (robot.getRobotNode() != null)
				{
					robotsNode.attachChild(robot.getRobotNode());
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp()
	{
		// init settings of the app
		initSettings();

		// init lights
		initLights();

		// inits the spatial elements
		initSpatials();

		// init orbit camera
		initCamera(this.origin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme3.app.Application#createCanvas()
	 */
	@Override
	public void createCanvas()
	{
		super.createCanvas();
		new ContextChecker().start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme3.app.Application#handleError(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void handleError(String errMsg, Throwable t)
	{
		super.handleError(errMsg, t);

		String pt1 = "<html><body><p>Error. Unable to create 3D scene.<br>";
		String pt2 = "Please, check that your machine meets the minimum system requirements<br>";
		String pt3 = "and that OpenGL acceleration is enabled.<br>";
		String pt4 = "If you need some support you can check <a href='http://code.google.com/p/sycamore'>http://code.google.com/p/sycamore</a></p></body></html>";

		String s = pt1 + pt2 + pt3 + pt4;

		JOptionPane.showMessageDialog(null, s, "Unable to create 3D scene", JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme3.app.SimpleApplication#simpleUpdate(float)
	 */
	@Override
	public void simpleUpdate(float tpf)
	{
		if (getAppEngine() != null)
		{
			Vector<SycamoreAbstractPoint> allRobots = new Vector<SycamoreAbstractPoint>();

			// update geometries in robots
			Iterator<SycamoreRobot<Point2D>> iterator = getAppEngine().getRobots().iterator();
			while (iterator.hasNext())
			{
				SycamoreRobot<Point2D> robot = iterator.next();
				simpleUpdate_impl(tpf, robot, allRobots);
			}

			// update baricentrum
			SycamoreAbstractPoint baricentrumPoint = SycamoreUtil.computeBaricentrum(allRobots);
			if (baricentrumPoint != null)
			{
				this.baricentrum.setLocalTranslation(baricentrumPoint.toVector3f());
			}
			rootNode.updateGeometricState();
			robotsNode.updateLogicalState(tpf);
		}
	}

	/**
	 * Perform the update operations
	 * 
	 * @param tpf
	 * @param robot
	 */
	private void simpleUpdate_impl(float tpf, SycamoreRobot robot, Vector<SycamoreAbstractPoint> allRobots)
	{
		Vector3f position = robot.getLocalPosition().toVector3f();

		Node robotNode = robot.getRobotNode();

		Agreement agreement = robot.getAgreement();
		if (agreement != null)
		{
			// default translation, rotation, scale
			Vector3f translation = Vector3f.ZERO;
			Vector3f scale = new Vector3f(1, 1, 1);
			Quaternion rotation = Quaternion.ZERO;

			// update with robot's ones
			translation = agreement.getLocalTranslation();
			scale = agreement.getLocalScale();
			rotation = agreement.getLocalRotation();

			// set them
			robotNode.setLocalRotation(rotation);
			robotNode.setLocalScale(scale);
			robotNode.setLocalTranslation(translation);

			// get the transform for the translation determined by the robot position instead of
			// robot's agreement
			Transform transform = robotNode.getLocalTransform();
			Transform positionTransform = new Transform(position);

			// combine
			positionTransform.combineWithParent(transform);
			robotNode.setLocalTransform(positionTransform);
		}
		else
		{
			// translate robot
			robotNode.setLocalTranslation(position);
		}

		robotNode.updateLogicalState(tpf);
		robotNode.updateGeometricState();

		allRobots.add(robot.getGlobalPosition());
		
		
		if(OsUtils.isWindows()) sendPositionToVirca(robot);
		
	}

	/**
	 * Adds an <code>ActionListener</code> to the button.
	 * 
	 * @param listener
	 *            the <code>ActionListener</code> to be added
	 */
	public void addActionListener(java.awt.event.ActionListener listener)
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
	public void removeActionListener(java.awt.event.ActionListener listener)
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
		for (java.awt.event.ActionListener listener : this.listeners)
		{
			listener.actionPerformed(e);
		}
	}

	/**
	 * @return the appEngine
	 */
	public synchronized SycamoreEngine getAppEngine()
	{
		return appEngine;
	}

	/**
	 * @param appEngine
	 *            the appEngine to set
	 */
	public synchronized void setAppEngine(SycamoreEngine<?> appEngine)
	{
		this.appEngine = appEngine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme3.input.controls.ActionListener#onAction(java.lang.String, boolean, float)
	 */
	@Override
	public void onAction(String name, boolean isPressed, float tpf)
	{
		// Nothing to do
	}

	/**
	 * Adds passed robot in the scene
	 */
	public void addRobotInScene(final SycamoreRobot<?> robot)
	{
		this.enqueue(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				robotsNode.attachChild(robot.getRobotNode());

				Agreement agreement = robot.getAgreement();
				if (SycamoreSystem.isLocalCoordinatesVisible() && agreement != null)
				{
					// attach local coordinates node
					Node axesNode = agreement.getAxesNode();

					axesNode.setLocalTranslation(agreement.getLocalTranslation());
					axesNode.setLocalRotation(agreement.getLocalRotation());
					axesNode.setLocalScale(agreement.getLocalScale());

					localAxesNode.attachChild(axesNode);
					localAxesNode.updateGeometricState();
				}
				return null;
			}
		});
	}

	/**
	 * Removes passed robot from the scene
	 */
	public void removeRobotFromScene(final SycamoreRobot<?> robot)
	{
		this.enqueue(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				robotsNode.detachChild(robot.getRobotNode());

				Agreement agreement = robot.getAgreement();
				if (agreement != null)
				{
					localAxesNode.detachChild(agreement.getAxesNode());
				}
				return null;
			}
		});
	}

	/**
	 * Setup the scene to adapt to passed type
	 * 
	 * @param type
	 */
	public void setupScene(final TYPE type)
	{
		if (type != null)
		{
			this.enqueue(new Callable<Object>()
			{
				@Override
				public Object call() throws Exception
				{
					// store current type
					currentType = type;

					if (type == TYPE.TYPE_2D)
					{
						mainNode.addControl(new BillboardControl());
						rotateGrid(FastMath.PI / 2);
						mainNode.detachChild(zAxis);
					}
					else if (type == TYPE.TYPE_3D)
					{
						mainNode.removeControl(BillboardControl.class);
						mainNode.setLocalRotation(billBoardRotation.clone());
						rotateGrid(0);
						if (SycamoreSystem.isAxesVisible())
						{
							mainNode.attachChild(zAxis);
						}
					}

					updateRobotsGeometries();

					return null;
				}
			});
		}
		else
		{
			System.out.println("Inconsistent types");
		}
	}

	/**
	 * Reset the scene to initial configuration
	 */
	public synchronized void reset()
	{
		// not enqueued because it is always executed in the JME thread
		mainNode.removeControl(BillboardControl.class);

		setupScene(TYPE.TYPE_3D);
		robotsNode.detachAllChildren();
		rootNode.updateGeometricState();

	}

	/**
	 * Encodes passed color inside an XML Element object, to be used by other objects that need to
	 * encode a color anywhere.
	 * 
	 * @param color
	 * @param factory
	 * @param builder
	 * @param document
	 * @return
	 */
	public static Element encodeColorRGBA(ColorRGBA color, DocumentBuilderFactory factory, DocumentBuilder builder, Document document)
	{
		// create element
		Element element = document.createElement("colorRGBA");

		// children
		Element rElem = document.createElement("r");
		rElem.appendChild(document.createTextNode(color.r + ""));

		Element gElem = document.createElement("g");
		gElem.appendChild(document.createTextNode(color.g + ""));

		Element bElem = document.createElement("b");
		bElem.appendChild(document.createTextNode(color.b + ""));

		Element aElem = document.createElement("a");
		aElem.appendChild(document.createTextNode(color.a + ""));

		// append children
		element.appendChild(rElem);
		element.appendChild(gElem);
		element.appendChild(bElem);
		element.appendChild(aElem);

		return element;
	}

	/**
	 * Decodes passed XML element and returns a ColorRGBA object that matches with the color
	 * informations encoded in XML
	 * 
	 * @param color
	 * @param factory
	 * @param builder
	 * @param document
	 * @return
	 */
	public static ColorRGBA decodeColorRGBA(Element element)
	{
		float red = 0;
		float green = 0;
		float blue = 0;
		float alpha = 0;

		NodeList nodes = element.getElementsByTagName("colorRGBA");

		// if there is at least a Timeline node, decode it
		if (nodes.getLength() > 0)
		{
			// r
			NodeList r = element.getElementsByTagName("r");
			if (r.getLength() > 0)
			{
				Element rElem = (Element) r.item(0);
				red = Float.parseFloat(rElem.getTextContent());
			}

			// g
			NodeList g = element.getElementsByTagName("g");
			if (g.getLength() > 0)
			{
				Element gElem = (Element) g.item(0);
				green = Float.parseFloat(gElem.getTextContent());
			}

			// b
			NodeList b = element.getElementsByTagName("b");
			if (b.getLength() > 0)
			{
				Element bElem = (Element) b.item(0);
				blue = Float.parseFloat(bElem.getTextContent());
			}

			// a
			NodeList a = element.getElementsByTagName("a");
			if (a.getLength() > 0)
			{
				Element aElem = (Element) a.item(0);
				alpha = Float.parseFloat(aElem.getTextContent());
			}
		}

		return new ColorRGBA(red, green, blue, alpha);
	}
	
	protected void sendPositionToVirca(SycamoreRobot robot){
		if(robot.getAlgorithm()!=null)
			if(!robot.getAlgorithm().isFinished()){
				//System.out.println(robot.getGlobalPosition() + " -- ID: " + robot.getID());
				if (robot.getGlobalPosition().isPoint2D()){
					Point3D p = new Point3D(((Point2D) robot.getGlobalPosition()).x, ((Point2D) robot.getGlobalPosition()).y, 10);
					if (robot.isHumanPilot())
						vircaDevice.setRobotPosition("H"+String.valueOf(robot.getID()),p);
					else
						vircaDevice.setRobotPosition(String.valueOf(robot.getID()),p);
				}
				else
					if (robot.isHumanPilot())
						vircaDevice.setRobotPosition("H"+String.valueOf(robot.getID()),(Point3D) robot.getGlobalPosition());
					else
						vircaDevice.setRobotPosition(String.valueOf(robot.getID()),(Point3D) robot.getGlobalPosition());

			}
	}
}