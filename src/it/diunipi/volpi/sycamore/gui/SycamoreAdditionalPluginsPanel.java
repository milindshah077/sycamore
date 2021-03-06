/**
 * 
 */
package it.diunipi.volpi.sycamore.gui;

import it.diunipi.volpi.sycamore.engine.SycamoreEngine;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.plugins.SycamorePluginManager;
import it.diunipi.volpi.sycamore.plugins.agreements.Agreement;
import it.diunipi.volpi.sycamore.plugins.initialconditions.InitialConditions;
import it.diunipi.volpi.sycamore.plugins.measures.Measure;
import it.diunipi.volpi.sycamore.plugins.memory.Memory;
import it.diunipi.volpi.sycamore.plugins.visibilities.Visibility;
import it.diunipi.volpi.sycamore.util.SycamoreFiredActionEvents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * A panel that lets the user manage all the plugins excepting for schedulers and algorithms.
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
public class SycamoreAdditionalPluginsPanel extends SycamorePanel
{
	private static final long	serialVersionUID					= -6792307871712640592L;
	private SycamoreEngine		appEngine							= null;
	private JLabel				label_measures						= null;
	private JScrollPane			scrollPane_measures					= null;
	private JXTable				table_measures						= null;
	private JLabel				label_visibility					= null;
	private JComboBox			comboBox_visibility					= null;
	private JLabel				label_agreement						= null;
	private JComboBox			comboBox_agreement					= null;
	private JTextPane			message_wrongVisibility				= null;
	private JLabel				label_initialConditions				= null;
	private JComboBox			comboBox_initialConditions			= null;
	private JLabel				label_memory						= null;
	private JComboBox			comboBox_memory						= null;
	private JTextPane			message_wrongAgreement				= null;
	private JTextPane			message_wrongInitialConditions		= null;
	private JTextPane			message_wrongMemory					= null;
	private JLabel				label_nKnown						= null;
	private SwitchToggle		switchToggle_nKnown					= null;
	private JLabel				label_multiplictyDetection			= null;
	private SwitchToggle		switchToggle_multiplicityDetection	= null;

	/**
	 * Default constructor.
	 */
	public SycamoreAdditionalPluginsPanel()
	{
		setPreferredSize(new Dimension(400, 600));
		initialize();
	}

	/**
	 * @param appEngine
	 *            the appEngine to set
	 */
	public void setAppEngine(SycamoreEngine appEngine)
	{
		this.appEngine = appEngine;
	}

	/**
	 * @return the appEngine
	 */
	public SycamoreEngine getAppEngine()
	{
		return appEngine;
	}

	/**
	 * Init the Gui.
	 */
	private void initialize()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{ 0, 0 };
		gridBagLayout.rowHeights = new int[]
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[]
		{ 0.0, 1.0 };
		gridBagLayout.rowWeights = new double[]
		{ 0.0, 10.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		GridBagConstraints gbc_label_measures = new GridBagConstraints();
		gbc_label_measures.gridwidth = 2;
		gbc_label_measures.anchor = GridBagConstraints.SOUTHWEST;
		gbc_label_measures.insets = new Insets(2, 2, 2, 2);
		gbc_label_measures.gridx = 0;
		gbc_label_measures.gridy = 0;
		add(getLabel_measures(), gbc_label_measures);

		GridBagConstraints gbc_scrollPane_measures = new GridBagConstraints();
		gbc_scrollPane_measures.gridwidth = 2;
		gbc_scrollPane_measures.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_measures.insets = new Insets(2, 2, 2, 2);
		gbc_scrollPane_measures.gridx = 0;
		gbc_scrollPane_measures.gridy = 1;
		add(getScrollPane_measures(), gbc_scrollPane_measures);

		GridBagConstraints gbc_label_visibility = new GridBagConstraints();
		gbc_label_visibility.gridwidth = 2;
		gbc_label_visibility.insets = new Insets(2, 2, 2, 2);
		gbc_label_visibility.anchor = GridBagConstraints.SOUTHWEST;
		gbc_label_visibility.gridx = 0;
		gbc_label_visibility.gridy = 2;
		add(getLabel_visibility(), gbc_label_visibility);

		GridBagConstraints gbc_comboBox_visibility = new GridBagConstraints();
		gbc_comboBox_visibility.gridwidth = 2;
		gbc_comboBox_visibility.insets = new Insets(2, 2, 2, 2);
		gbc_comboBox_visibility.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_visibility.gridx = 0;
		gbc_comboBox_visibility.gridy = 3;
		add(getComboBox_visibility(), gbc_comboBox_visibility);

		GridBagConstraints gbc_message_wrongVisibility = new GridBagConstraints();
		gbc_message_wrongVisibility.gridwidth = 2;
		gbc_message_wrongVisibility.insets = new Insets(2, 2, 2, 2);
		gbc_message_wrongVisibility.fill = GridBagConstraints.BOTH;
		gbc_message_wrongVisibility.gridx = 0;
		gbc_message_wrongVisibility.gridy = 4;
		add(getMessage_wrongVisibility(), gbc_message_wrongVisibility);

		GridBagConstraints gbc_label_agreement = new GridBagConstraints();
		gbc_label_agreement.gridwidth = 2;
		gbc_label_agreement.anchor = GridBagConstraints.SOUTHWEST;
		gbc_label_agreement.insets = new Insets(2, 2, 2, 2);
		gbc_label_agreement.gridx = 0;
		gbc_label_agreement.gridy = 5;
		add(getLabel_agreement(), gbc_label_agreement);

		GridBagConstraints gbc_comboBox_agreement = new GridBagConstraints();
		gbc_comboBox_agreement.gridwidth = 2;
		gbc_comboBox_agreement.insets = new Insets(2, 2, 2, 2);
		gbc_comboBox_agreement.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_agreement.gridx = 0;
		gbc_comboBox_agreement.gridy = 6;
		add(getComboBox_agreement(), gbc_comboBox_agreement);

		GridBagConstraints gbc_message_wrongAgreement = new GridBagConstraints();
		gbc_message_wrongAgreement.gridwidth = 2;
		gbc_message_wrongAgreement.insets = new Insets(2, 2, 2, 2);
		gbc_message_wrongAgreement.fill = GridBagConstraints.BOTH;
		gbc_message_wrongAgreement.gridx = 0;
		gbc_message_wrongAgreement.gridy = 7;
		add(getMessage_wrongAgreement(), gbc_message_wrongAgreement);

		GridBagConstraints gbc_label_initialConditions = new GridBagConstraints();
		gbc_label_initialConditions.gridwidth = 2;
		gbc_label_initialConditions.anchor = GridBagConstraints.WEST;
		gbc_label_initialConditions.insets = new Insets(2, 2, 2, 2);
		gbc_label_initialConditions.gridx = 0;
		gbc_label_initialConditions.gridy = 8;
		add(getLabel_initialConditions(), gbc_label_initialConditions);

		GridBagConstraints gbc_comboBox_initialConditions = new GridBagConstraints();
		gbc_comboBox_initialConditions.gridwidth = 2;
		gbc_comboBox_initialConditions.insets = new Insets(2, 2, 2, 2);
		gbc_comboBox_initialConditions.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_initialConditions.gridx = 0;
		gbc_comboBox_initialConditions.gridy = 9;
		add(getComboBox_initialConditions(), gbc_comboBox_initialConditions);

		GridBagConstraints gbc_message_wrongInitialConditions = new GridBagConstraints();
		gbc_message_wrongInitialConditions.gridwidth = 2;
		gbc_message_wrongInitialConditions.insets = new Insets(2, 2, 2, 2);
		gbc_message_wrongInitialConditions.fill = GridBagConstraints.BOTH;
		gbc_message_wrongInitialConditions.gridx = 0;
		gbc_message_wrongInitialConditions.gridy = 10;
		add(getMessage_wrongInitialConditions(), gbc_message_wrongInitialConditions);

		GridBagConstraints gbc_label_memory = new GridBagConstraints();
		gbc_label_memory.gridwidth = 2;
		gbc_label_memory.anchor = GridBagConstraints.WEST;
		gbc_label_memory.insets = new Insets(2, 2, 2, 2);
		gbc_label_memory.gridx = 0;
		gbc_label_memory.gridy = 11;
		add(getLabel_memory(), gbc_label_memory);

		GridBagConstraints gbc_comboBox_memory = new GridBagConstraints();
		gbc_comboBox_memory.gridwidth = 2;
		gbc_comboBox_memory.insets = new Insets(2, 2, 2, 2);
		gbc_comboBox_memory.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_memory.gridx = 0;
		gbc_comboBox_memory.gridy = 12;
		add(getComboBox_memory(), gbc_comboBox_memory);

		GridBagConstraints gbc_message_wrongMemory = new GridBagConstraints();
		gbc_message_wrongMemory.gridwidth = 2;
		gbc_message_wrongMemory.insets = new Insets(2, 2, 2, 2);
		gbc_message_wrongMemory.fill = GridBagConstraints.BOTH;
		gbc_message_wrongMemory.gridx = 0;
		gbc_message_wrongMemory.gridy = 13;
		add(getMessage_wrongMemory(), gbc_message_wrongMemory);

		GridBagConstraints gbc_label_nKnown = new GridBagConstraints();
		gbc_label_nKnown.insets = new Insets(2, 2, 2, 2);
		gbc_label_nKnown.anchor = GridBagConstraints.WEST;
		gbc_label_nKnown.gridx = 0;
		gbc_label_nKnown.gridy = 14;
		add(getLabel_nKnown(), gbc_label_nKnown);

		GridBagConstraints gbc_onOffButton_nKnown = new GridBagConstraints();
		gbc_onOffButton_nKnown.anchor = GridBagConstraints.WEST;
		gbc_onOffButton_nKnown.insets = new Insets(2, 30, 2, 2);
		gbc_onOffButton_nKnown.gridx = 1;
		gbc_onOffButton_nKnown.gridy = 14;
		add(getSwitchToggle_nKnown(), gbc_onOffButton_nKnown);

		GridBagConstraints gbc_label_multiplictyDetection = new GridBagConstraints();
		gbc_label_multiplictyDetection.anchor = GridBagConstraints.WEST;
		gbc_label_multiplictyDetection.insets = new Insets(2, 2, 2, 2);
		gbc_label_multiplictyDetection.gridx = 0;
		gbc_label_multiplictyDetection.gridy = 15;
		add(getLabel_multiplictyDetection(), gbc_label_multiplictyDetection);

		GridBagConstraints gbc_onOffButton_multiplicityDetection = new GridBagConstraints();
		gbc_onOffButton_multiplicityDetection.anchor = GridBagConstraints.WEST;
		gbc_onOffButton_multiplicityDetection.insets = new Insets(2, 30, 2, 2);
		gbc_onOffButton_multiplicityDetection.gridx = 1;
		gbc_onOffButton_multiplicityDetection.gridy = 15;
		add(getSwitchToggle_multiplicityDetection(), gbc_onOffButton_multiplicityDetection);
	}

	/**
	 * @return the label_measures
	 */
	private JLabel getLabel_measures()
	{
		if (label_measures == null)
		{
			label_measures = new JLabel("Select Measures:");
		}
		return label_measures;
	}

	/**
	 * @return the scrollPane_measures
	 */
	private JScrollPane getScrollPane_measures()
	{
		if (scrollPane_measures == null)
		{
			scrollPane_measures = new JScrollPane();
			scrollPane_measures.setViewportView(getTable_measures());
		}
		return scrollPane_measures;
	}

	/**
	 * @return the table_measures
	 */
	private JXTable getTable_measures()
	{
		if (table_measures == null)
		{
			table_measures = new JXTable();

			// apply a PluginSelectionTableModel with generics for Measures to this table
			ArrayList<Measure> measures = SycamorePluginManager.getSharedInstance().getLoadedMeasures();
			PluginSelectionTableModel<Measure> measureModel = new PluginSelectionTableModel<Measure>(measures);
			table_measures.setModel(measureModel);

			table_measures.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			table_measures.addHighlighter(HighlighterFactory.createAlternateStriping());

			ListSelectionModel selectionModel = table_measures.getSelectionModel();
			selectionModel.addListSelectionListener(new ListSelectionListener()
			{
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					if (appEngine != null)
					{
						Vector<Measure> measures = appEngine.getCurrentMeasures();
						measures.removeAllElements();

						int[] selected = table_measures.getSelectedRows();
						for (int i : selected)
						{
							Measure measure = (Measure) table_measures.getValueAt(i, 0);
							try
							{
								appEngine.createAndAddNewMeasureInstance(measure);
							}
							catch (Exception e1)
							{
								e1.printStackTrace();
							}

						}
					}
				}
			});
		}
		return table_measures;
	}

	/**
	 * @return the label_visibility
	 */
	private JLabel getLabel_visibility()
	{
		if (label_visibility == null)
		{
			label_visibility = new JLabel("Select visibility: (default is unbounded visibility)");
		}
		return label_visibility;
	}

	/**
	 * @return the comboBox_visibility
	 */
	private JComboBox getComboBox_visibility()
	{
		if (comboBox_visibility == null)
		{
			comboBox_visibility = new JComboBox();

			// apply a PluginSelectionComboboxModel with generics for Visibilities to this combobox
			ArrayList<Visibility> visibilities = SycamorePluginManager.getSharedInstance().getLoadedVisibilities();
			PluginSelectionComboboxModel<Visibility> visibilityModel = new PluginSelectionComboboxModel<Visibility>(visibilities);

			comboBox_visibility.setModel(visibilityModel);
			comboBox_visibility.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (!changeLock)
					{
						PluginSelectionComboboxModel<Visibility> model = (PluginSelectionComboboxModel<Visibility>) getComboBox_visibility().getModel();
						Visibility visibility = (Visibility) model.getSelectedItem();

						if (appEngine != null)
						{
							// prepare data
							boolean typesOK = true;
							TYPE engineType = TYPE.TYPE_2D;
							TYPE pluginType = TYPE.TYPE_2D;

							// check types, if any
							if (visibility != null)
							{
								engineType = appEngine.getType();
								pluginType = visibility.getType();

								typesOK = (engineType == pluginType);
							}

							if (typesOK)
							{
								// hide error message
								getMessage_wrongVisibility().setVisible(false);
							}
							else
							{
								// show error message
								updateWrongVisibilityText(engineType, pluginType);
								getMessage_wrongVisibility().setVisible(true);

								// visibility is null
								visibility = null;
							}

							// set in engine
							try
							{
								appEngine.createAndSetNewVisibilityInstance(visibility);
							}
							catch (Exception exc)
							{
								exc.printStackTrace();
							}
						}

						fireActionEvent(new ActionEvent(SycamoreAdditionalPluginsPanel.this, 0, SycamoreFiredActionEvents.SELECTED_VISIBILITY_CHANGED.name()));
					}
				}
			});
		}
		return comboBox_visibility;
	}

	/**
	 * @return the label_agreement
	 */
	private JLabel getLabel_agreement()
	{
		if (label_agreement == null)
		{
			label_agreement = new JLabel("Select agreement: (default is total agreement)");
		}
		return label_agreement;
	}

	/**
	 * @return the comboBox_agreement
	 */
	private JComboBox getComboBox_agreement()
	{
		if (comboBox_agreement == null)
		{
			comboBox_agreement = new JComboBox();

			// apply a PluginSelectionComboboxModel with generics for Agreement to this combobox
			ArrayList<Agreement> agreements = SycamorePluginManager.getSharedInstance().getLoadedAgreements();
			PluginSelectionComboboxModel<Agreement> agreementModel = new PluginSelectionComboboxModel<Agreement>(agreements);

			comboBox_agreement.setModel(agreementModel);
			comboBox_agreement.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (!changeLock)
					{
						PluginSelectionComboboxModel<Agreement> model = (PluginSelectionComboboxModel<Agreement>) getComboBox_agreement().getModel();
						Agreement agreement = (Agreement) model.getSelectedItem();

						// update message
						if (appEngine != null)
						{
							// prepare data
							boolean typesOK = true;
							TYPE engineType = TYPE.TYPE_2D;
							TYPE pluginType = TYPE.TYPE_2D;

							// check types, if any
							if (agreement != null)
							{
								engineType = appEngine.getType();
								pluginType = agreement.getType();

								typesOK = (engineType == pluginType);
							}

							if (typesOK)
							{
								// hide error message
								getMessage_wrongAgreement().setVisible(false);
							}
							else
							{
								// show error message
								updateWrongAgreementyText(engineType, pluginType);
								getMessage_wrongAgreement().setVisible(true);

								// agreement is null
								agreement = null;
							}

							// set in engine
							try
							{
								appEngine.createAndSetNewAgreementInstance(agreement);
							}
							catch (Exception exc)
							{
								exc.printStackTrace();
							}
						}

						fireActionEvent(new ActionEvent(SycamoreAdditionalPluginsPanel.this, 0, SycamoreFiredActionEvents.SELECTED_AGREEMENT_CHANGED.name()));
					}
				}
			});
		}
		return comboBox_agreement;
	}

	/**
	 * @param engineType
	 * @param visibilityType
	 */
	private void updateWrongVisibilityText(TYPE engineType, TYPE visibilityType)
	{
		getMessage_wrongVisibility().setText(
				"The type of the chosen visibility is " + visibilityType.getShortDescription() + ", but the type of the scene is " + engineType.getShortDescription()
						+ ". The default visibility (unbounded) will be used until you choose a visibility whose type is consistent with the scene's one.");
	}

	/**
	 * @return message_wrongVisibility
	 */
	private JTextPane getMessage_wrongVisibility()
	{
		if (message_wrongVisibility == null)
		{
			message_wrongVisibility = new JTextPane();
			message_wrongVisibility.setPreferredSize(new Dimension(300, 60));
			message_wrongVisibility.setMinimumSize(new Dimension(300, 60));
			message_wrongVisibility.setMaximumSize(new Dimension(300, 60));
			message_wrongVisibility.setOpaque(false);
			message_wrongVisibility.setForeground(Color.RED);
			message_wrongVisibility.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
			message_wrongVisibility.setVisible(false);
		}
		return message_wrongVisibility;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.gui.SycamorePanel#updateGui()
	 */
	@Override
	public void updateGui()
	{
		changeLock = true;

		if (appEngine != null)
		{
			// select right visibility
			ComboBoxModel visibilityModel = getComboBox_visibility().getModel();
			for (int i = 0; i < visibilityModel.getSize(); i++)
			{
				Visibility item = (Visibility) visibilityModel.getElementAt(i);
				Visibility current = appEngine.getCurrentVisibility();
				if (item != null && current != null && item.getPluginName().equals(current.getPluginName()))
				{
					getComboBox_visibility().setSelectedIndex(i);
				}
			}

			// update visibility message
			Visibility visibility = (Visibility) getComboBox_visibility().getSelectedItem();
			if (visibility != null)
			{
				TYPE type = appEngine.getType();
				TYPE visibilityType = visibility.getType();

				if (type == visibilityType)
				{
					getMessage_wrongVisibility().setVisible(false);
				}
				else
				{
					updateWrongVisibilityText(type, visibilityType);
					getMessage_wrongVisibility().setVisible(true);
				}
			}

			// select right agreement
			ComboBoxModel agreementModel = getComboBox_agreement().getModel();
			for (int i = 0; i < agreementModel.getSize(); i++)
			{
				Agreement item = (Agreement) agreementModel.getElementAt(i);
				Agreement current = appEngine.getCurrentAgreement();
				if (item != null && current != null && item.getPluginName().equals(current.getPluginName()))
				{
					getComboBox_agreement().setSelectedIndex(i);
				}
			}

			// update agreement message
			Agreement agreement = (Agreement) getComboBox_agreement().getSelectedItem();
			if (agreement != null)
			{
				TYPE type = appEngine.getType();
				TYPE agreementType = agreement.getType();

				if (type == agreementType)
				{
					getMessage_wrongAgreement().setVisible(false);
				}
				else
				{
					updateWrongAgreementyText(type, agreementType);
					getMessage_wrongAgreement().setVisible(true);
				}
			}

			// select right initial conditions
			ComboBoxModel initialConditionsModel = getComboBox_initialConditions().getModel();
			for (int i = 0; i < initialConditionsModel.getSize(); i++)
			{
				InitialConditions item = (InitialConditions) initialConditionsModel.getElementAt(i);
				InitialConditions current = appEngine.getCurrentInitialConditions();
				if (item != null && current != null && item.getPluginName().equals(current.getPluginName()))
				{
					getComboBox_initialConditions().setSelectedIndex(i);
				}
			}

			// update initial conditions message
			InitialConditions initialCondition = (InitialConditions) getComboBox_initialConditions().getSelectedItem();
			if (initialCondition != null)
			{
				TYPE type = appEngine.getType();
				TYPE initialConditionType = initialCondition.getType();

				if (type == initialConditionType)
				{
					getMessage_wrongInitialConditions().setVisible(false);
				}
				else
				{
					updateWrongInitialConditionsText(type, initialConditionType);
					getMessage_wrongInitialConditions().setVisible(true);
				}
			}

			// select right memory
			ComboBoxModel memoryModel = getComboBox_memory().getModel();
			for (int i = 0; i < memoryModel.getSize(); i++)
			{
				Memory item = (Memory) memoryModel.getElementAt(i);
				Memory current = appEngine.getCurrentMemory();
				if (item != null && current != null && item.getPluginName().equals(current.getPluginName()))
				{
					getComboBox_memory().setSelectedIndex(i);
				}
			}

			// update memory message
			Memory memory = (Memory) getComboBox_memory().getSelectedItem();
			if (memory != null)
			{
				TYPE type = appEngine.getType();
				TYPE memoryType = memory.getType();

				if (type == memoryType)
				{
					getMessage_wrongMemory().setVisible(false);
				}
				else
				{
					updateWrongMemoryText(type, memoryType);
					getMessage_wrongMemory().setVisible(true);
				}
			}
		}

		changeLock = false;
	}

	/**
	 * @return label_initialConditions
	 */
	private JLabel getLabel_initialConditions()
	{
		if (label_initialConditions == null)
		{
			label_initialConditions = new JLabel("Select initial conditions: (default is random points)");
		}
		return label_initialConditions;
	}

	/**
	 * @return comboBox_initialConditions
	 */
	private JComboBox getComboBox_initialConditions()
	{
		if (comboBox_initialConditions == null)
		{
			comboBox_initialConditions = new JComboBox();

			// apply a PluginSelectionComboboxModel with generics for InitialConditions to this
			// combobox
			ArrayList<InitialConditions> initialConditions = SycamorePluginManager.getSharedInstance().getLoadedInitialConditions();
			PluginSelectionComboboxModel<InitialConditions> initialConditionsModel = new PluginSelectionComboboxModel<InitialConditions>(initialConditions);

			comboBox_initialConditions.setModel(initialConditionsModel);
			comboBox_initialConditions.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (!changeLock)
					{
						PluginSelectionComboboxModel<InitialConditions> model = (PluginSelectionComboboxModel<InitialConditions>) getComboBox_initialConditions().getModel();
						InitialConditions initialCondition = (InitialConditions) model.getSelectedItem();

						// update message
						if (appEngine != null)
						{
							// prepare data
							boolean typesOK = true;
							TYPE engineType = TYPE.TYPE_2D;
							TYPE pluginType = TYPE.TYPE_2D;

							// check types, if any
							if (initialCondition != null)
							{
								engineType = appEngine.getType();
								pluginType = initialCondition.getType();

								typesOK = (engineType == pluginType);
							}

							if (typesOK)
							{
								// hide error message
								getMessage_wrongInitialConditions().setVisible(false);
							}
							else
							{
								// show error message
								updateWrongInitialConditionsText(engineType, pluginType);
								getMessage_wrongInitialConditions().setVisible(true);

								// initialCondition is null
								initialCondition = null;
							}

							// set in engine
							try
							{
								appEngine.createAndSetNewInitialConditionsInstance(initialCondition);
							}
							catch (Exception exc)
							{
								exc.printStackTrace();
							}
						}

						fireActionEvent(new ActionEvent(SycamoreAdditionalPluginsPanel.this, 0, SycamoreFiredActionEvents.SELECTED_INITIAL_CONDITION_CHANGED.name()));
					}
				}
			});
		}
		return comboBox_initialConditions;
	}

	/**
	 * @return
	 */
	private JLabel getLabel_memory()
	{
		if (label_memory == null)
		{
			label_memory = new JLabel("Select Memory: (default is no memory, oblivious robots)");
		}
		return label_memory;
	}

	/**
	 * @return
	 */
	private JComboBox getComboBox_memory()
	{
		if (comboBox_memory == null)
		{
			comboBox_memory = new JComboBox();

			// apply a PluginSelectionComboboxModel with generics for Memory to this combobox
			ArrayList<Memory> memory = SycamorePluginManager.getSharedInstance().getLoadedMemories();
			PluginSelectionComboboxModel<Memory> memoryModel = new PluginSelectionComboboxModel<Memory>(memory);

			comboBox_memory.setModel(memoryModel);
			comboBox_memory.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (!changeLock)
					{
						PluginSelectionComboboxModel<Memory> model = (PluginSelectionComboboxModel<Memory>) getComboBox_memory().getModel();
						Memory memory = (Memory) model.getSelectedItem();

						// update message
						if (appEngine != null)
						{
							// prepare data
							boolean typesOK = true;
							TYPE engineType = TYPE.TYPE_2D;
							TYPE pluginType = TYPE.TYPE_2D;

							// check types, if any
							if (memory != null)
							{
								engineType = appEngine.getType();
								pluginType = memory.getType();

								typesOK = (engineType == pluginType);
							}

							if (typesOK)
							{
								// hide error message
								getMessage_wrongMemory().setVisible(false);
							}
							else
							{
								// show error message
								updateWrongMemoryText(engineType, pluginType);
								getMessage_wrongMemory().setVisible(true);

								// memory is null
								memory = null;
							}

							// set in engine
							try
							{
								appEngine.createAndSetNewMemoryInstance(memory);
							}
							catch (Exception exc)
							{
								exc.printStackTrace();
							}
						}

						fireActionEvent(new ActionEvent(SycamoreAdditionalPluginsPanel.this, 0, SycamoreFiredActionEvents.SELECTED_MEMORY_CHANGED.name()));
					}
				}
			});

		}
		return comboBox_memory;
	}

	/**
	 * @param engineType
	 * @param visibilityType
	 */
	private void updateWrongAgreementyText(TYPE engineType, TYPE visibilityType)
	{
		getMessage_wrongAgreement().setText(
				"The type of the chosen agreement is " + visibilityType.getShortDescription() + ", but the type of the scene is " + engineType.getShortDescription()
						+ ". The default agreement (Absolute Agreement) will be used until you choose an agreement whose type is consistent with the scene's one.");
	}

	/**
	 * @return message_wrongAgreement
	 */
	private JTextPane getMessage_wrongAgreement()
	{
		if (message_wrongAgreement == null)
		{
			message_wrongAgreement = new JTextPane();
			message_wrongAgreement.setOpaque(false);
			message_wrongAgreement.setMaximumSize(new Dimension(300, 60));
			message_wrongAgreement.setMinimumSize(new Dimension(300, 60));
			message_wrongAgreement.setPreferredSize(new Dimension(300, 60));
			message_wrongAgreement.setForeground(Color.RED);
			message_wrongAgreement.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
			message_wrongAgreement.setVisible(false);
		}
		return message_wrongAgreement;
	}

	/**
	 * @param engineType
	 * @param initialConditionType
	 */
	private void updateWrongInitialConditionsText(TYPE engineType, TYPE initialConditionType)
	{
		getMessage_wrongInitialConditions().setText(
				"The type of the chosen initial condition is " + initialConditionType.getShortDescription() + ", but the type of the scene is " + engineType.getShortDescription()
						+ ". The default initial condition (random points) will be used until you choose an initial condition whose type is consistent with the scene's one.");
	}

	/**
	 * @return message_wrongInitialConditions
	 */
	private JTextPane getMessage_wrongInitialConditions()
	{
		if (message_wrongInitialConditions == null)
		{
			message_wrongInitialConditions = new JTextPane();
			message_wrongInitialConditions.setOpaque(false);
			message_wrongInitialConditions.setPreferredSize(new Dimension(300, 60));
			message_wrongInitialConditions.setMinimumSize(new Dimension(300, 60));
			message_wrongInitialConditions.setMaximumSize(new Dimension(300, 60));
			message_wrongInitialConditions.setForeground(Color.RED);
			message_wrongInitialConditions.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
			message_wrongInitialConditions.setVisible(false);
		}
		return message_wrongInitialConditions;
	}

	/**
	 * @param engineType
	 * @param memoryType
	 */
	private void updateWrongMemoryText(TYPE engineType, TYPE memoryType)
	{
		getMessage_wrongMemory().setText(
				"The type of the chosen memory is " + memoryType.getShortDescription() + ", but the type of the scene is " + engineType.getShortDescription()
						+ ". The default memory (oblivious robots) will be used until you choose a memory whose type is consistent with the scene's one.");
	}

	/**
	 * @return message_wrongMemory
	 */
	private JTextPane getMessage_wrongMemory()
	{
		if (message_wrongMemory == null)
		{
			message_wrongMemory = new JTextPane();
			message_wrongMemory.setOpaque(false);
			message_wrongMemory.setPreferredSize(new Dimension(300, 60));
			message_wrongMemory.setMinimumSize(new Dimension(300, 60));
			message_wrongMemory.setMaximumSize(new Dimension(300, 60));
			message_wrongMemory.setForeground(Color.RED);
			message_wrongMemory.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
			message_wrongMemory.setVisible(false);
		}
		return message_wrongMemory;
	}

	/**
	 * @return label_nKnown
	 */
	private JLabel getLabel_nKnown()
	{
		if (label_nKnown == null)
		{
			label_nKnown = new JLabel("N (# of robts) is known:");
		}
		return label_nKnown;
	}

	/**
	 * @return switchToggle_nKnown
	 */
	private SwitchToggle getSwitchToggle_nKnown()
	{
		if (switchToggle_nKnown == null)
		{
			switchToggle_nKnown = new SwitchToggle();
			switchToggle_nKnown.setMinimumSize(new Dimension(71, 25));
			switchToggle_nKnown.setMaximumSize(new Dimension(71, 25));
			switchToggle_nKnown.setPreferredSize(new Dimension(71, 25));
			switchToggle_nKnown.setSelected(SycamoreSystem.isNKnown());
			switchToggle_nKnown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					boolean selected = switchToggle_nKnown.isSelected();
					SycamoreSystem.setNKnown(selected);
				}
			});
		}
		return switchToggle_nKnown;
	}

	/**
	 * @return label_multiplictyDetection
	 */
	private JLabel getLabel_multiplictyDetection()
	{
		if (label_multiplictyDetection == null)
		{
			label_multiplictyDetection = new JLabel("Multiplicity detection:");
		}
		return label_multiplictyDetection;
	}

	/**
	 * @return switchToggle_multiplicityDetection
	 */
	private SwitchToggle getSwitchToggle_multiplicityDetection()
	{
		if (switchToggle_multiplicityDetection == null)
		{
			switchToggle_multiplicityDetection = new SwitchToggle();
			switchToggle_multiplicityDetection.setMaximumSize(new Dimension(71, 25));
			switchToggle_multiplicityDetection.setMinimumSize(new Dimension(71, 25));
			switchToggle_multiplicityDetection.setPreferredSize(new Dimension(71, 25));
			switchToggle_multiplicityDetection.setSelected(true);
			switchToggle_multiplicityDetection.setEnabled(false);
			switchToggle_multiplicityDetection.setToolTipText("Multiplicity detection is always on. Option is coming soon...");
		}
		return switchToggle_multiplicityDetection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.gui.SycamorePanel#reset()
	 */
	@Override
	public void reset()
	{
		changeLock = true;

		getTable_measures().clearSelection();

		getComboBox_visibility().setSelectedIndex(-1);
		getComboBox_agreement().setSelectedIndex(-1);
		getComboBox_initialConditions().setSelectedIndex(-1);
		getComboBox_memory().setSelectedIndex(-1);

		getMessage_wrongVisibility().setVisible(false);
		getMessage_wrongAgreement().setVisible(false);
		getMessage_wrongInitialConditions().setVisible(false);
		getMessage_wrongMemory().setVisible(false);

		changeLock = false;
	}

	/**
	 * Updates the combo box and table models
	 */
	public void updateModels()
	{
		// apply a PluginSelectionTableModel with generics for Measures to this table
		ArrayList<Measure> measures = SycamorePluginManager.getSharedInstance().getLoadedMeasures();
		PluginSelectionTableModel<Measure> measureModel = new PluginSelectionTableModel<Measure>(measures);
		getTable_measures().setModel(measureModel);

		// apply a PluginSelectionComboboxModel with generics for Visibilities to this combobox
		ArrayList<Visibility> visibilities = SycamorePluginManager.getSharedInstance().getLoadedVisibilities();
		PluginSelectionComboboxModel<Visibility> visibilityModel = new PluginSelectionComboboxModel<Visibility>(visibilities);
		getComboBox_visibility().setModel(visibilityModel);

		// apply a PluginSelectionComboboxModel with generics for Agreement to this combobox
		ArrayList<Agreement> agreements = SycamorePluginManager.getSharedInstance().getLoadedAgreements();
		PluginSelectionComboboxModel<Agreement> agreementModel = new PluginSelectionComboboxModel<Agreement>(agreements);
		getComboBox_agreement().setModel(agreementModel);

		// apply a PluginSelectionComboboxModel with generics for InitialConditions to this combobox
		ArrayList<InitialConditions> initialConditions = SycamorePluginManager.getSharedInstance().getLoadedInitialConditions();
		PluginSelectionComboboxModel<InitialConditions> initialConditionsModel = new PluginSelectionComboboxModel<InitialConditions>(initialConditions);
		getComboBox_initialConditions().setModel(initialConditionsModel);

		// apply a PluginSelectionComboboxModel with generics for Memory to this combobox
		ArrayList<Memory> memory = SycamorePluginManager.getSharedInstance().getLoadedMemories();
		PluginSelectionComboboxModel<Memory> memoryModel = new PluginSelectionComboboxModel<Memory>(memory);
		getComboBox_memory().setModel(memoryModel);
	}

	/**
	 * @param newEngine
	 */
	public void updateEngine(SycamoreEngine newEngine)
	{
		try
		{
			// set measures
			newEngine.getCurrentMeasures().removeAllElements();
			int[] rows = getTable_measures().getSelectedRows();
			for (int i = 0; i < rows.length; i++)
			{
				int row = rows[i];
				Measure measure = (Measure) getTable_measures().getValueAt(row, 0);
				newEngine.createAndAddNewMeasureInstance(measure);
			}

			// prepare the new visibility
			PluginSelectionComboboxModel<Visibility> visibilityModel = (PluginSelectionComboboxModel<Visibility>) getComboBox_visibility().getModel();
			Visibility visibility = (Visibility) visibilityModel.getSelectedItem();

			if (visibility != null)
			{
				// create and set new visibility
				newEngine.createAndSetNewVisibilityInstance(visibility);
			}

			// prepare the new agreement
			PluginSelectionComboboxModel<Agreement> agreementModel = (PluginSelectionComboboxModel<Agreement>) getComboBox_agreement().getModel();
			Agreement agreement = (Agreement) agreementModel.getSelectedItem();

			if (agreement != null)
			{
				// create and set new agreement
				newEngine.createAndSetNewAgreementInstance(agreement);
			}

			// prepare the new initialConditions
			PluginSelectionComboboxModel<InitialConditions> initialConditionsModel = (PluginSelectionComboboxModel<InitialConditions>) getComboBox_initialConditions().getModel();
			InitialConditions initialConditions = (InitialConditions) initialConditionsModel.getSelectedItem();

			if (initialConditions != null)
			{
				// create and set new initialConditions
				newEngine.createAndSetNewInitialConditionsInstance(initialConditions);
			}

			// prepare the new memory
			PluginSelectionComboboxModel<Memory> memoryModel = (PluginSelectionComboboxModel<Memory>) getComboBox_memory().getModel();
			Memory memory = (Memory) memoryModel.getSelectedItem();

			if (memory != null)
			{
				// create and set new memory
				newEngine.createAndSetNewMemoryInstance(memory);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
