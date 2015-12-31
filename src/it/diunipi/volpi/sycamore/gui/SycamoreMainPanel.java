package it.diunipi.volpi.sycamore.gui;

import it.diunipi.volpi.app.sycamore.SycamoreApp.APP_MODE;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine2D;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine3D;
import it.diunipi.volpi.sycamore.engine.SycamoreRobot;
import it.diunipi.volpi.sycamore.util.SycamoreFiredActionEvents;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

/**
 * The Sycamore main GUI panel
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
public class SycamoreMainPanel extends SycamorePanel implements ActionListener
{
	private static final long				serialVersionUID				= -3399357140028668175L;
	private JXMultiSplitPane				multiSplitPane					= null;
	private SycamorePluginsListPanel		pluginsPanel					= null;
	private SycamoreSimulationSettingsPanel	simulationSettingsPanel			= null;
	private SycamoreSimulationViewPanel		simulationViewPanel				= null;
	private SycamoreAnimationControlPanel	sycamoreAnimationControlPanel	= null;
	private SycamoreEngine					appEngine						= null;

	// the app mode
	private final APP_MODE					appMode;

	/**
	 * Default constructor
	 */
	public SycamoreMainPanel(APP_MODE appMode)
	{
		this.appMode = appMode;
		initialize();
	}

	/**
	 * Init the GUI
	 */
	private void initialize()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{ 0 };
		gridBagLayout.rowHeights = new int[]
		{ 0, 0 };
		gridBagLayout.columnWeights = new double[]
		{ 1.0 };
		gridBagLayout.rowWeights = new double[]
		{ 0.0, 1.0 };
		setLayout(gridBagLayout);

		GridBagConstraints gbc_multiSplitPane = new GridBagConstraints();
		gbc_multiSplitPane.insets = new Insets(5, 5, 5, 5);
		gbc_multiSplitPane.gridx = 0;
		gbc_multiSplitPane.gridy = 1;
		gbc_multiSplitPane.fill = GridBagConstraints.BOTH;

		if (this.appMode == APP_MODE.SIMULATOR)
		{
			add(getMultiSplitPane(), gbc_multiSplitPane);
		}
		else
		{
			add(getSimulationViewPanel(), gbc_multiSplitPane);
		}

		GridBagConstraints gbc_sycamoreAnimationControlPanel = new GridBagConstraints();
		gbc_sycamoreAnimationControlPanel.fill = GridBagConstraints.BOTH;
		gbc_sycamoreAnimationControlPanel.gridx = 0;
		gbc_sycamoreAnimationControlPanel.gridy = 0;
		add(getSycamoreAnimationControlPanel(), gbc_sycamoreAnimationControlPanel);
	}

	/**
	 * Init the JME scene
	 */
	public void initJMEScene()
	{
		getSimulationViewPanel().initJMEScene();
	}

	/**
	 * Dispose the JME Scene
	 */
	public void disposeJMEScene()
	{
		getSimulationViewPanel().disposeJMEScene();
	}

	/**
	 * @return the multiSplitPane
	 */
	private JXMultiSplitPane getMultiSplitPane()
	{
		if (multiSplitPane == null)
		{
			multiSplitPane = new JXMultiSplitPane();
			String layoutDef = "(ROW (COLUMN (LEAF name=top weight=0.6) (LEAF name=bottom weight=0)) (LEAF name=left))";
			MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);

			multiSplitPane.setModel(modelRoot);
			multiSplitPane.add(getPluginsPanel(), "top");
			multiSplitPane.add(getSimulationSettingsPanel(), "bottom");
			multiSplitPane.add(getSimulationViewPanel(), "left");
		}
		return multiSplitPane;
	}

	/**
	 * @return the pluginsPanel
	 */
	private SycamorePluginsListPanel getPluginsPanel()
	{
		if (pluginsPanel == null)
		{
			pluginsPanel = new SycamorePluginsListPanel();
			pluginsPanel.addActionListener(this);
		}
		return pluginsPanel;
	}

	/**
	 * @return the simulationPanel
	 */
	private SycamoreSimulationSettingsPanel getSimulationSettingsPanel()
	{
		if (simulationSettingsPanel == null)
		{
			simulationSettingsPanel = new SycamoreSimulationSettingsPanel();
			simulationSettingsPanel.addActionListener(this);
		}
		return simulationSettingsPanel;
	}

	/**
	 * @param type
	 * @return
	 */
	public SycamoreEngine initEngine(TYPE type)
	{
		if (type != null)
		{
			SycamoreEngine engine = null;

			if (type == TYPE.TYPE_2D)
			{
				engine = new SycamoreEngine2D();
			}
			else if (type == TYPE.TYPE_3D)
			{
				engine = new SycamoreEngine3D();
			}

			SycamoreSystem.setEngine(engine);
			return engine;
		}
		return null;
	}

	/**
	 * Updates passed engine by setting in it the plugins chosen by the user.
	 * 
	 * @param newEngine
	 */
	private void updateEngine(SycamoreEngine newEngine)
	{
		// update plugins
		getSimulationSettingsPanel().updateEngine(newEngine);

		if (this.appMode == APP_MODE.SIMULATOR)
		{
			// set the engine in scheduler threads
			SycamoreSystem.getSchedulerThread().setEngine(newEngine);
			SycamoreSystem.getHumanPilotSchedulerThread().setEngine(newEngine);
		}
		else
		{
			SycamoreSystem.getVisualizerThread().setEngine(newEngine);
		}

		// update the animation speed value
		newEngine.setAnimationSpeedMultiplier(getSycamoreAnimationControlPanel().getAnimationSpeedMultiplier());
	}

	/**
	 * @return the simulationViewPanel
	 */
	private SycamoreSimulationViewPanel getSimulationViewPanel()
	{
		if (simulationViewPanel == null)
		{
			simulationViewPanel = new SycamoreSimulationViewPanel();
			simulationViewPanel.setMinimumSize(new Dimension(10, 10));
			simulationViewPanel.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (e.getSource() instanceof AbstractButton)
					{
						updateVisibleElements((AbstractButton) e.getSource(), e.getActionCommand());
					}

					// forward event
					fireActionEvent(e);
				}
			});
		}
		return simulationViewPanel;
	}

	/**
	 * @return SycamoreAnimationControlPanel
	 */
	private SycamoreAnimationControlPanel getSycamoreAnimationControlPanel()
	{
		if (sycamoreAnimationControlPanel == null)
		{
			if (this.appMode == APP_MODE.SIMULATOR)
			{
				sycamoreAnimationControlPanel = new SycamoreAnimationControlPanel(appMode);
				sycamoreAnimationControlPanel.addActionListener(this);
			}
			else
			{
				sycamoreAnimationControlPanel = new SycamoreVisualizerAnimationControlPanel(appMode);
				sycamoreAnimationControlPanel.addActionListener(this);
			}
		}
		return sycamoreAnimationControlPanel;
	}

	/**
	 * Returns the current app engine
	 * 
	 * @return
	 */
	public SycamoreEngine getAppEngine()
	{
		return appEngine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.diunipi.volpi.sycamore.gui.SycamorePanel#setAppEngine(it.diunipi.volpi.sycamore.engine
	 * .SycamoreEngine)
	 */
	@Override
	public void setAppEngine(SycamoreEngine appEngine)
	{
		if (this.appEngine != null)
		{
			// destroy current engine
			this.appEngine.dispose();
		}

		// set the new engine
		this.appEngine = appEngine;

		// forward the new engine to every panel
		getPluginsPanel().setAppEngine(appEngine);
		getSimulationSettingsPanel().setAppEngine(appEngine);
		getSimulationViewPanel().setAppEngine(appEngine);
		getSycamoreAnimationControlPanel().setAppEngine(appEngine);

		if (appEngine != null)
		{
			appEngine.addActionListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		getPluginsPanel().setEnabled(enabled);
		getSimulationSettingsPanel().setEnabled(enabled);
		getSycamoreAnimationControlPanel().setEnabled(enabled);
		getSimulationViewPanel().setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.gui.SycamorePanel#updateGui()
	 */
	@Override
	public void updateGui()
	{
		getPluginsPanel().updateGui();
		getSimulationSettingsPanel().updateGui();
		getSimulationViewPanel().updateGui();
		getSycamoreAnimationControlPanel().updateGui();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals(SycamoreFiredActionEvents.UPDATE_GUI.name()))
		{
			updateGui();
		}
		else if (command.equals(SycamoreFiredActionEvents.PLAY_ANIMATION.name()))
		{
			getSimulationSettingsPanel().setEnabled(false);
			getPluginsPanel().setEnabled(false);
		}
		else if (command.equals(SycamoreFiredActionEvents.PLUGINS_REFRESHED.name()))
		{
			refreshPlugins();
			updateGui();
		}
		else if (command.equals(SycamoreFiredActionEvents.SIMULATION_FINISHED.name()))
		{
			getSycamoreAnimationControlPanel().setupAfterSimulationFinished();
			getSimulationSettingsPanel().setEnabled(true);
			getPluginsPanel().setEnabled(true);
		}
		else if (e.getActionCommand().equals(SycamoreFiredActionEvents.ADD_ROBOT_IN_SCENE.name()))
		{
			getSimulationSettingsPanel().updateRobotsCountLabel();
			getSimulationViewPanel().addRobotInScene((SycamoreRobot) e.getSource());
		}
		else if (e.getActionCommand().equals(SycamoreFiredActionEvents.REMOVE_ROBOT_FROM_SCENE.name()))
		{
			getSimulationSettingsPanel().updateRobotsCountLabel();
			getSimulationViewPanel().removeRobotFromScene((SycamoreRobot) e.getSource());
		}
		else if (e.getActionCommand().equals(SycamoreFiredActionEvents.SETUP_SCENE.name()))
		{
			TYPE type = getSimulationSettingsPanel().getChosenType();
			getSimulationViewPanel().setupScene(type);
		}
		else if (e.getActionCommand().equals(SycamoreFiredActionEvents.SELECTED_AGREEMENT_CHANGED.name()))
		{
			getSimulationViewPanel().manageAgreementChange();
		}
		else if (e.getActionCommand().equals(SycamoreFiredActionEvents.UPDATE_AGREEMENTS_GRAPHICS.name()))
		{
			getSimulationViewPanel().updateAgreementsGraphics();
		}
		else if (e.getActionCommand().equals(SycamoreFiredActionEvents.SIMULATION_DATA_GOOD.name()))
		{
			TYPE type = getSimulationSettingsPanel().getChosenType();

			// if simulaton data is good, type should not be null. By the way, a check
			// is always better....
			if (type != null)
			{
				if (getAppEngine() != null)
				{
					TYPE currentType = getAppEngine().getType();

					if (type == currentType)
					{
						// engines are of the same type. Don't need to recreate.
						return;
					}
				}

				// in any other case, create a new engine
				SycamoreEngine newEngine = initEngine(type);
				setAppEngine(newEngine);
				updateEngine(newEngine);

				// refresh agreemet axes
				getSimulationViewPanel().manageAgreementChange();
				updateGui();
			}
		}
		else if (e.getActionCommand().equals(SycamoreFiredActionEvents.SIMULATION_DATA_BAD.name()))
		{
			setAppEngine(initEngine(null));

			getSimulationViewPanel().resetJMEScene();
			updateGui();
		}
	}

	/**
	 * Refresh the list of the plugins
	 */
	public void refreshPlugins()
	{
		getSimulationSettingsPanel().updateModels();
	}

	/**
	 * @param source
	 * @param actionCommand
	 */
	public void updateVisibleElements(AbstractButton source, String actionCommand)
	{
		boolean visible = source.isSelected();

		if (actionCommand.equals(SycamoreFiredActionEvents.SHOW_GRID.name()))
		{
			// save visible state
			SycamoreSystem.setGridVisible(visible);
			getSimulationViewPanel().setGridVisible(visible);
		}
		else if (actionCommand.equals(SycamoreFiredActionEvents.SHOW_AXES.name()))
		{
			// save visible state
			SycamoreSystem.setAxesVisible(visible);
			getSimulationViewPanel().setAxesVisible(visible);
		}
		else if (actionCommand.equals(SycamoreFiredActionEvents.SHOW_VISIBILITY_RANGE.name()))
		{
			// save visible state
			SycamoreSystem.setVisibilityRangesVisible(visible);

			// apply to robots
			if (appEngine != null)
			{
				appEngine.setVisibilityRangesVisible(visible);
			}
		}
		else if (actionCommand.equals(SycamoreFiredActionEvents.SHOW_VISIBILITY_GRAPH.name()))
		{
			// save visible state
			SycamoreSystem.setVisibilityGraphVisible(visible);

			// TODO implement
		}
		else if (actionCommand.equals(SycamoreFiredActionEvents.SHOW_MOVEMENT_DIRECTIONS.name()))
		{
			// save visible state
			SycamoreSystem.setMovementDirectionsVisible(visible);

			// apply to robots
			if (appEngine != null)
			{
				appEngine.setMovementDirectionsVisible(visible);
			}
		}
		else if (actionCommand.equals(SycamoreFiredActionEvents.SHOW_LOCAL_COORDINATES.name()))
		{
			// save visible state
			SycamoreSystem.setLocalCoordinatesVisible(visible);
			getSimulationViewPanel().setLocalCoordinatesVisible(visible);
		}
		else if (actionCommand.equals(SycamoreFiredActionEvents.SHOW_BARICENTRUM.name()))
		{
			// save visible state
			SycamoreSystem.setBaricentrumVisible(visible);
			getSimulationViewPanel().setBaricentrumVisible(visible);
		}
		else if (actionCommand.equals(SycamoreFiredActionEvents.SHOW_LIGHTS.name()))
		{
			// save visible state
			SycamoreSystem.setRobotsLightsVisible(visible);

			// apply to robots
			if (appEngine != null)
			{
				appEngine.setRobotLightsVisible(visible);
			}
		}
		else if (actionCommand.equals(SycamoreFiredActionEvents.SHOW_VISUAL_ELEMENTS.name()))
		{
			// save visible state
			SycamoreSystem.setVisualElementsVisible(visible);

			// TODO implement
		}

		// update buttons
		getSimulationViewPanel().updateGui();

		// update menu bar
		fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.UPDATE_GUI.name()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.gui.SycamorePanel#reset()
	 */
	@Override
	public synchronized void reset()
	{
		// reset
		getSimulationViewPanel().reset();
		getSimulationSettingsPanel().reset();
		getSycamoreAnimationControlPanel().reset();

		// set everywhere a null enegine
		setAppEngine(null);
		if (this.appMode == APP_MODE.SIMULATOR)
		{
			SycamoreSystem.getSchedulerThread().setEngine(null);
			SycamoreSystem.getHumanPilotSchedulerThread().setEngine(null);
		}
		else
		{
			SycamoreSystem.getVisualizerThread().setEngine(null);
		}

		getPluginsPanel().setEnabled(true);
		getSimulationSettingsPanel().setEnabled(true);

		updateGui();
	}

	/**
	 * Lets the user load a new plugin by taking it from the file system
	 */
	public void loadPluginFromFileSystem()
	{
		getPluginsPanel().loadPluginFromFileSystem();
	}

	/**
	 * @param type
	 */
	public void setupJMEScene(TYPE type)
	{
		getSimulationViewPanel().setupScene(type);
	}

	/**
	 * 
	 */
	public void pauseAnimation()
	{
		getSycamoreAnimationControlPanel().pauseAnimation();
	}
}