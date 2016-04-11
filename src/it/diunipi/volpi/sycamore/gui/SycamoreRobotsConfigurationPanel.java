package it.diunipi.volpi.sycamore.gui;

import it.diunipi.volpi.sycamore.engine.SycamoreEngine;
import it.diunipi.volpi.sycamore.engine.SycamoreRobot;
import it.diunipi.volpi.sycamore.engine.SycamoreEngine.TYPE;
import it.diunipi.volpi.sycamore.engine.SycamoreRobotMatrix;
import it.diunipi.volpi.sycamore.plugins.SycamorePluginManager;
import it.diunipi.volpi.sycamore.plugins.algorithms.Algorithm;
import it.diunipi.volpi.sycamore.util.ApplicationProperties;
import it.diunipi.volpi.sycamore.util.PropertyManager;
import it.diunipi.volpi.sycamore.util.SycamoreFiredActionEvents;
import it.diunipi.volpi.sycamore.util.SycamoreUtil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme3.math.ColorRGBA;

/**
 * The panel that lets the user configure the robots in the GUI. It lets the user choose an
 * algorithm and define how many robots of that algorithm are present. It also lets the user
 * determine how many lights there are and the robot's speed. The number of chosen algorithms is
 * unlimited.
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
public class SycamoreRobotsConfigurationPanel extends SycamorePanel
{
	/**
	 * The model for the combobox with colors.
	 * 
	 * @author Valerio Volpi - vale.v@me.com
	 */
	private class ColorComboboxModel extends DefaultComboBoxModel
	{
		private static final long	serialVersionUID	= -4342951066410729560L;
		private Vector<ColorRGBA>	colors				= null;

		public ColorComboboxModel(Vector<ColorRGBA> colors)
		{
			this.colors = new Vector<ColorRGBA>();
			this.colors.addAll(colors);
		}

		/**
		 * Constructor.
		 */
		public ColorComboboxModel()
		{
			this.colors = new Vector<ColorRGBA>();

			this.colors.add(new ColorRGBA(1, 0, 0, 1));
			this.colors.add(new ColorRGBA(0.96f, 0.90f, 0.09f, 1));
			this.colors.add(new ColorRGBA(0, 0, 1, 1));
			this.colors.add(new ColorRGBA(0, 0.64f, 0.32f, 1));
			this.colors.add(new ColorRGBA(0.55f, 0.55f, 0.55f, 1));
			this.colors.add(new ColorRGBA(0.94f, 0.4f, 0.18f, 1));
			this.colors.add(new ColorRGBA(0.78f, 0.69f, 0.6f, 1));
			this.colors.add(new ColorRGBA(0.37f, 0.21f, 0.09f, 1));
		}

		/**
		 * @return the colors
		 */
		public Vector<ColorRGBA> getColors()
		{
			return colors;
		}

		/**
		 * @param color
		 */
		public void addColor(ColorRGBA color)
		{
			this.colors.add(color);
			fireIntervalAdded(this, colors.size() - 1, colors.size() - 1);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.DefaultComboBoxModel#getSize()
		 */
		@Override
		public int getSize()
		{
			return colors.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.DefaultComboBoxModel#getElementAt(int)
		 */
		@Override
		public Object getElementAt(int index)
		{
			return colors.elementAt(index);
		}
	}

	/**
	 * The color icon
	 * 
	 * @author Valerio Volpi - vale.v@me.com
	 */
	private class ColorIcon implements Icon
	{
		private final Color	color;

		/**
		 * Default constructor.
		 */
		public ColorIcon(Color color)
		{
			super();
			this.color = color;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
		 */
		@Override
		public synchronized void paintIcon(Component c, Graphics g, int x, int y)
		{
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			c.setBackground(new Color(1, 1, 1, 0));

			g2d.setColor(this.color);
			g2d.fillOval(x, y, getIconWidth(), getIconHeight());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconWidth()
		 */
		@Override
		public int getIconWidth()
		{
			return 15;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconHeight()
		 */
		@Override
		public int getIconHeight()
		{
			return 15;
		}
	}

	/**
	 * The renderer for the combobox with colors. Needs the model to be already defined.
	 * 
	 * @author Valerio Volpi - vale.v@me.com
	 */
	private class ColorComboboxRenderer extends DefaultListCellRenderer
	{
		private static final long	serialVersionUID	= -4775750018943766592L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList,
		 * java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			ColorRGBA passedColor = (ColorRGBA) value;

			// return new ColorComboboxCellComponent((Color) value);
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setHorizontalAlignment(CENTER);
			label.setText("");
			label.setIcon(new ColorIcon(SycamoreUtil.convertColor(passedColor)));

			return label;
		}

	}

	/**
	 * The class that is used to instantiate the combo boxes additional to the first one. It is just
	 * a JComboBox with an index associated to it.
	 * 
	 * @author Valerio Volpi - vale.v@me.com
	 */
	private class AdditionalComboBox extends JComboBox
	{
		private static final long	serialVersionUID	= 2811748931625679228L;
		private int					index				= 0;

		/**
		 * Constructor. Wants an index to be given to it.
		 * 
		 * @param index
		 */
		public AdditionalComboBox(int index)
		{
			this.index = index;
		}

		/**
		 * @param index
		 */
		public void setIndex(int index)
		{
			this.index = index;
		}

		/**
		 * @return index
		 */
		public int getIndex()
		{
			return index;
		}
	}

	/**
	 * The class that is used to instantiate the minus buttons additional to the first one. It is
	 * just a JButton with an index associated to it.
	 * 
	 * @author Valerio Volpi - vale.v@me.com
	 */
	private class AdditionalButton extends JButton
	{
		private static final long	serialVersionUID	= -8698188448913295429L;
		private int					index				= 0;

		/**
		 * Constructor. Wants an index to be given to it.
		 * 
		 * @param index
		 */
		public AdditionalButton(int index)
		{
			this.index = index;
		}

		/**
		 * @param index
		 */
		public void setIndex(int index)
		{
			this.index = index;
		}

		/**
		 * @return index
		 */
		public int getIndex()
		{
			return index;
		}
	}

	/**
	 * The class that is used to instantiate the comboboxes additional to the first one. It is just
	 * a JComboBox with an index associated to it.
	 * 
	 * @author Valerio Volpi - vale.v@me.com
	 */
	private class AdditionalSpinner extends JSpinner
	{
		private static final long	serialVersionUID	= 2811748931625679228L;
		private int					index				= 0;

		/**
		 * Constructor. Wants an index to be given to it.
		 * 
		 * @param index
		 */
		public AdditionalSpinner(int index)
		{
			this.index = index;
		}

		/**
		 * @param index
		 */
		public void setIndex(int index)
		{
			this.index = index;
		}

		/**
		 * @return index
		 */
		public int getIndex()
		{
			return index;
		}
	}

	private static final long			serialVersionUID			= 2180815597431189002L;
	private SycamoreEngine				appEngine					= null;
	private JComboBox					comboBox_algorithmSelection	= null;
	private JComboBox					comboBox_colorSelection		= null;
	private JSpinner					spinner_lightsNumber		= null;
	private JSpinner					spinner_robotsNumber		= null;
	private JButton						button_addRow				= null;

	private Vector<AdditionalComboBox>	additionalComboboxes		= null;
	private Vector<AdditionalComboBox>	additionalColorsComboboxes	= null;
	private Vector<AdditionalSpinner>	additionalSpeedSpinners		= null;
	private Vector<AdditionalSpinner>	additionalLightSpinners		= null;
	private Vector<AdditionalSpinner>	additionalSpinners			= null;
	private Vector<AdditionalButton>	additionalButtons			= null;

	private HashMap<JComboBox, TYPE>	types						= null;
	private JLabel						label_algorithm				= null;
	private JLabel						label_color					= null;
	private JLabel						label_lightsNum				= null;
	private JLabel						label_robotsNum				= null;
	private JLabel						label_speed;
	private JSpinner					spinner_speed;

	/**
	 * Default constructor.
	 */
	public SycamoreRobotsConfigurationPanel()
	{
		this.additionalComboboxes = new Vector<AdditionalComboBox>();
		this.additionalColorsComboboxes = new Vector<AdditionalComboBox>();
		this.additionalSpeedSpinners = new Vector<AdditionalSpinner>();
		this.additionalLightSpinners = new Vector<AdditionalSpinner>();
		this.additionalSpinners = new Vector<AdditionalSpinner>();
		this.additionalButtons = new Vector<AdditionalButton>();

		// the types hashmap contains the type of the algorithm selected in each combobox. If a
		// combobox has no selected item, it does not contain that combobox.
		this.types = new HashMap<JComboBox, TYPE>();

		initialize();
	}

	/**
	 * Init the Gui
	 */
	private void initialize()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]
		{ 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[]
		{ 0.0, 1.0 };
		setLayout(gridBagLayout);
		GridBagConstraints gbc_label_algorithm = new GridBagConstraints();
		gbc_label_algorithm.insets = new Insets(2, 2, 2, 2);
		gbc_label_algorithm.gridx = 0;
		gbc_label_algorithm.gridy = 0;
		add(getLabel_algorithm(), gbc_label_algorithm);
		GridBagConstraints gbc_label_color = new GridBagConstraints();
		gbc_label_color.insets = new Insets(2, 2, 2, 2);
		gbc_label_color.gridx = 1;
		gbc_label_color.gridy = 0;
		add(getLabel_color(), gbc_label_color);
		GridBagConstraints gbc_label_speed = new GridBagConstraints();
		gbc_label_speed.insets = new Insets(2, 2, 2, 2);
		gbc_label_speed.gridx = 2;
		gbc_label_speed.gridy = 0;
		add(getLabel_speed(), gbc_label_speed);
		GridBagConstraints gbc_label_lightsNum = new GridBagConstraints();
		gbc_label_lightsNum.insets = new Insets(2, 2, 2, 2);
		gbc_label_lightsNum.gridx = 3;
		gbc_label_lightsNum.gridy = 0;
		add(getLabel_lightsNum(), gbc_label_lightsNum);

		GridBagConstraints gbc_label_robotsNum = new GridBagConstraints();
		gbc_label_robotsNum.insets = new Insets(2, 2, 2, 2);
		gbc_label_robotsNum.gridx = 4;
		gbc_label_robotsNum.gridy = 0;
		add(getLabel_robotsNum(), gbc_label_robotsNum);

		// create the first row with the comboboxes, the spinner and a Plus button
		GridBagConstraints gbc_comboBox_algorithmSelection = new GridBagConstraints();
		gbc_comboBox_algorithmSelection.anchor = GridBagConstraints.NORTH;
		gbc_comboBox_algorithmSelection.insets = new Insets(2, 2, 2, 2);
		gbc_comboBox_algorithmSelection.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_algorithmSelection.gridx = 0;
		gbc_comboBox_algorithmSelection.gridy = 1;
		gbc_comboBox_algorithmSelection.weightx = 1;
		add(getComboBox_algorithmSelection(), gbc_comboBox_algorithmSelection);

		GridBagConstraints gbc_comboBox_colorSelection = new GridBagConstraints();
		gbc_comboBox_colorSelection.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_colorSelection.anchor = GridBagConstraints.NORTH;
		gbc_comboBox_colorSelection.insets = new Insets(2, 2, 2, 2);
		gbc_comboBox_colorSelection.gridx = 1;
		gbc_comboBox_colorSelection.gridy = 1;
		gbc_comboBox_colorSelection.weightx = 1;
		add(getComboBox_colorSelection(), gbc_comboBox_colorSelection);

		GridBagConstraints gbc_spinner_speed = new GridBagConstraints();
		gbc_spinner_speed.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_speed.anchor = GridBagConstraints.NORTH;
		gbc_spinner_speed.insets = new Insets(2, 2, 2, 2);
		gbc_spinner_speed.gridx = 2;
		gbc_spinner_speed.gridy = 1;
		add(getSpinner_speed(), gbc_spinner_speed);

		GridBagConstraints gbc_spinner_lightsNumber = new GridBagConstraints();
		gbc_spinner_lightsNumber.anchor = GridBagConstraints.NORTH;
		gbc_spinner_lightsNumber.insets = new Insets(2, 2, 2, 2);
		gbc_spinner_lightsNumber.gridx = 3;
		gbc_spinner_lightsNumber.gridy = 1;
		add(getSpinner_lightsNumber(), gbc_spinner_lightsNumber);

		GridBagConstraints gbc_spinner_robotsNumber = new GridBagConstraints();
		gbc_spinner_robotsNumber.anchor = GridBagConstraints.NORTH;
		gbc_spinner_robotsNumber.insets = new Insets(2, 2, 2, 2);
		gbc_spinner_robotsNumber.gridx = 4;
		gbc_spinner_robotsNumber.gridy = 1;
		add(getSpinner_robotsNumber(), gbc_spinner_robotsNumber);

		GridBagConstraints gbc_button_addRow = new GridBagConstraints();
		gbc_button_addRow.insets = new Insets(2, 2, 2, 2);
		gbc_button_addRow.anchor = GridBagConstraints.NORTH;
		gbc_button_addRow.gridx = 5;
		gbc_button_addRow.gridy = 1;
		add(getButton_addRow(), gbc_button_addRow);
	}

	/**
	 * @return true if the types in the hashmap are all consistent
	 */
	private boolean isTypeGood()
	{
		// check first type
		boolean isGood = !types.isEmpty();

		// check all elements and modify isGood eventualy
		if (isGood)
		{
			Set<JComboBox> keys = this.types.keySet();

			// call once next() on the iterator. It will return the first element, since we know
			// that types is not empty.
			JComboBox first = keys.iterator().next();
			TYPE firstElement = this.types.get(first);

			// check following types
			for (JComboBox key : keys)
			{
				TYPE element = this.types.get(key);
				if (element != firstElement)
				{
					isGood = false;
					break;
				}
			}
		}

		return isGood;
	}

	/**
	 * Check that all the elements in the types hashmap has the same type. In this case fires an
	 * event with command SIMULATION_DATA_GOOD. IN the other cases fires an event with
	 * SIMULATION_DATA_BAD command.
	 */
	private boolean checkTypes()
	{
		boolean typesGood = isTypeGood();
		// fire the corresponding event.
		if (typesGood)
		{
			// SIMULATION_DATA_CHANGED event means that types are consistent. The parent panels
			// are now able to create the engine. The engine will be hopefuly recreated just if
			// needed.
			fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.SIMULATION_DATA_GOOD.name()));
			fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.SETUP_SCENE.name()));
		}
		else
		{
			if (appEngine != null)
			{
				// SIMULATION_DATA_BAD event means that types are not consistent and simulation
				// cannot
				// start. The engine is expected to become null. If it is already null, the event is
				// not
				// fired to avoid useless operations.
				fireActionEvent(new ActionEvent(this, 0, SycamoreFiredActionEvents.SIMULATION_DATA_BAD.name()));

				JOptionPane.showMessageDialog(null, "You have chosen mixed Algorithm types, 2D and 3D.\nThis is not allowed, please check the algorithms you have chosen.", "Wrong Algorithms types",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		return typesGood;
	}

	/**
	 * Sets up the row weights of the current layout based on the number of additional components
	 */
	private void setupLayoutRowWeights()
	{
		GridBagLayout layout = (GridBagLayout) getLayout();
		double[] rowWeights = new double[additionalComboboxes.size() + 2];

		// supposing n rows to be present, the firs n - 1 weights are zero
		for (int i = 0; i < additionalComboboxes.size(); i++)
		{
			rowWeights[i] = 0.0;
		}
		// the last weight is 1.0
		rowWeights[rowWeights.length - 1] = 1.0;

		layout.rowWeights = rowWeights;
	}

	/**
	 * Adds a new row, first in the vectors of additional components, then in the GUI.
	 */
	private void addRowFromAdditionalsComponents()
	{
		// first of all create new components
		additionalComboboxes.add(getNewComboBox_algorithmSelection());
		additionalColorsComboboxes.add(getNewComboBox_colorSelection());
		additionalSpeedSpinners.add(getNewSpinner_speed());
		additionalLightSpinners.add(getNewSpinner_lightsNumber());
		additionalSpinners.add(getNewSpinner_robotsNumber());
		additionalButtons.add(getNewButton_removeRow());

		int y = this.additionalComboboxes.size() + 1;

		// setup row weights
		this.setupLayoutRowWeights();

		// add to GUI
		GridBagConstraints gbc_comboBox_algorithmSelection = new GridBagConstraints();
		gbc_comboBox_algorithmSelection.anchor = GridBagConstraints.NORTH;
		gbc_comboBox_algorithmSelection.insets = new Insets(2, 2, 2, 2);
		gbc_comboBox_algorithmSelection.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_algorithmSelection.gridx = 0;
		gbc_comboBox_algorithmSelection.gridy = y;
		gbc_comboBox_algorithmSelection.weightx = 1;
		add(additionalComboboxes.get(y - 2), gbc_comboBox_algorithmSelection);

		GridBagConstraints gbc_comboBox_colorSelection = new GridBagConstraints();
		gbc_comboBox_colorSelection.anchor = GridBagConstraints.NORTH;
		gbc_comboBox_colorSelection.insets = new Insets(2, 2, 2, 2);
		gbc_comboBox_colorSelection.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_colorSelection.gridx = 1;
		gbc_comboBox_colorSelection.gridy = y;
		gbc_comboBox_colorSelection.weightx = 1;
		add(additionalColorsComboboxes.get(y - 2), gbc_comboBox_colorSelection);

		GridBagConstraints gbc_spinner_speed = new GridBagConstraints();
		gbc_spinner_speed.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_speed.anchor = GridBagConstraints.NORTH;
		gbc_spinner_speed.insets = new Insets(2, 2, 2, 2);
		gbc_spinner_speed.gridx = 2;
		gbc_spinner_speed.gridy = y;
		add(additionalSpeedSpinners.get(y - 2), gbc_spinner_speed);

		GridBagConstraints gbc_spinner_lightsNumber = new GridBagConstraints();
		gbc_spinner_lightsNumber.anchor = GridBagConstraints.NORTH;
		gbc_spinner_lightsNumber.insets = new Insets(2, 2, 2, 2);
		gbc_spinner_lightsNumber.gridx = 3;
		gbc_spinner_lightsNumber.gridy = y;
		add(additionalLightSpinners.get(y - 2), gbc_spinner_lightsNumber);

		GridBagConstraints gbc_spinner_robotsNumber = new GridBagConstraints();
		gbc_spinner_robotsNumber.anchor = GridBagConstraints.NORTH;
		gbc_spinner_robotsNumber.insets = new Insets(2, 2, 2, 2);
		gbc_spinner_robotsNumber.gridx = 4;
		gbc_spinner_robotsNumber.gridy = y;
		add(additionalSpinners.get(y - 2), gbc_spinner_robotsNumber);

		GridBagConstraints gbc_button_removeRow = new GridBagConstraints();
		gbc_button_removeRow.insets = new Insets(2, 2, 2, 2);
		gbc_button_removeRow.anchor = GridBagConstraints.NORTH;
		gbc_button_removeRow.gridx = 5;
		gbc_button_removeRow.gridy = y;
		add(additionalButtons.get(y - 2), gbc_button_removeRow);

		revalidate();
	}

	/**
	 * Removes passed row, first in the vectors of additional components, then in the GUI. The
	 * indexes of the remaining components are adjusted properly.
	 * 
	 * @param index
	 */
	private void removeRowFromAdditionalComponents(int index)
	{
		// remove from vectors
		JComboBox comboBox = additionalComboboxes.remove(index - 1);
		JComboBox colorComboBox = additionalColorsComboboxes.remove(index - 1);
		JSpinner speedSpinner = additionalSpeedSpinners.remove(index - 1);
		JSpinner lightSpinner = additionalLightSpinners.remove(index - 1);
		JSpinner spinner = additionalSpinners.remove(index - 1);
		AdditionalButton button = additionalButtons.remove(index - 1);
		types.remove(comboBox);

		// updates
		updateComponentsIndexes();
		updateComponentsConstraints();

		// remove from GUI
		this.remove(comboBox);
		this.remove(colorComboBox);
		this.remove(speedSpinner);
		this.remove(lightSpinner);
		this.remove(spinner);
		this.remove(button);

		setupLayoutRowWeights();

		revalidate();
		repaint();
	}

	/**
	 * Updates the GridBagConstraints of all the components in the GUI, to reflect a removal or a
	 * modification of the number of components.
	 */
	private void updateComponentsConstraints()
	{
		GridBagLayout layout = (GridBagLayout) getLayout();

		for (int i = 0; i < additionalButtons.size(); i++)
		{
			GridBagConstraints gbc_comboBox_algorithmSelection = new GridBagConstraints();
			gbc_comboBox_algorithmSelection.anchor = GridBagConstraints.NORTH;
			gbc_comboBox_algorithmSelection.insets = new Insets(2, 2, 2, 2);
			gbc_comboBox_algorithmSelection.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox_algorithmSelection.gridx = 0;
			gbc_comboBox_algorithmSelection.gridy = i + 2;
			gbc_comboBox_algorithmSelection.weightx = 1;
			layout.setConstraints(additionalComboboxes.get(i), gbc_comboBox_algorithmSelection);

			GridBagConstraints gbc_comboBox_colorSelection = new GridBagConstraints();
			gbc_comboBox_colorSelection.anchor = GridBagConstraints.NORTH;
			gbc_comboBox_colorSelection.insets = new Insets(2, 2, 2, 2);
			gbc_comboBox_colorSelection.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox_colorSelection.gridx = 1;
			gbc_comboBox_colorSelection.gridy = i + 2;
			gbc_comboBox_colorSelection.weightx = 1;
			layout.setConstraints(additionalColorsComboboxes.get(i), gbc_comboBox_colorSelection);

			GridBagConstraints gbc_spinner_speed = new GridBagConstraints();
			gbc_spinner_speed.fill = GridBagConstraints.HORIZONTAL;
			gbc_spinner_speed.anchor = GridBagConstraints.NORTH;
			gbc_spinner_speed.insets = new Insets(2, 2, 2, 2);
			gbc_spinner_speed.gridx = 2;
			gbc_spinner_speed.gridy = i + 2;
			layout.setConstraints(additionalSpeedSpinners.get(i), gbc_spinner_speed);

			GridBagConstraints gbc_spinner_lightsNumber = new GridBagConstraints();
			gbc_spinner_lightsNumber.anchor = GridBagConstraints.NORTH;
			gbc_spinner_lightsNumber.insets = new Insets(2, 2, 2, 2);
			gbc_spinner_lightsNumber.gridx = 3;
			gbc_spinner_lightsNumber.gridy = i + 2;
			layout.setConstraints(additionalLightSpinners.get(i), gbc_spinner_lightsNumber);

			GridBagConstraints gbc_spinner_robotsNumber = new GridBagConstraints();
			gbc_spinner_robotsNumber.anchor = GridBagConstraints.NORTH;
			gbc_spinner_robotsNumber.insets = new Insets(2, 2, 2, 2);
			gbc_spinner_robotsNumber.gridx = 4;
			gbc_spinner_robotsNumber.gridy = i + 2;
			layout.setConstraints(additionalSpinners.get(i), gbc_spinner_robotsNumber);

			GridBagConstraints gbc_button_removeRow = new GridBagConstraints();
			gbc_button_removeRow.insets = new Insets(2, 2, 2, 2);
			gbc_button_removeRow.anchor = GridBagConstraints.NORTH;
			gbc_button_removeRow.gridx = 5;
			gbc_button_removeRow.gridy = i + 2;
			layout.setConstraints(additionalButtons.get(i), gbc_button_removeRow);
		}

		layout.invalidateLayout(this);
	}

	/**
	 * Update the indexes of the components, after a remove operation.
	 */
	private void updateComponentsIndexes()
	{
		for (int i = 0; i < additionalButtons.size(); i++)
		{
			additionalButtons.get(i).setIndex(i + 1);
			additionalComboboxes.get(i).setIndex(i + 1);
			additionalColorsComboboxes.get(i).setIndex(i + 1);
			additionalSpeedSpinners.get(i).setIndex(i + 1);
			additionalLightSpinners.get(i).setIndex(i + 1);
			additionalSpinners.get(i).setIndex(i + 1);
		}
	}

	/**
	 * @return the comboBox_algorithmSelection
	 */
	private JComboBox getComboBox_colorSelection()
	{
		if (comboBox_colorSelection == null)
		{
			comboBox_colorSelection = new JComboBox();
			comboBox_colorSelection.setMaximumSize(new Dimension(40, 27));
			comboBox_colorSelection.setMinimumSize(new Dimension(40, 27));
			comboBox_colorSelection.setPreferredSize(new Dimension(40, 27));

			ColorComboboxModel model = new ColorComboboxModel();
			ColorComboboxRenderer renderer = new ColorComboboxRenderer();

			comboBox_colorSelection.setModel(model);
			comboBox_colorSelection.setRenderer(renderer);
			comboBox_colorSelection.setSelectedIndex(0);

			comboBox_colorSelection.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (!changeLock && appEngine != null)
					{
						// get the color
						ColorRGBA color = (ColorRGBA) getComboBox_colorSelection().getSelectedItem();

						// get the algorithm
						PluginSelectionComboboxModel<Algorithm> model = (PluginSelectionComboboxModel<Algorithm>) comboBox_algorithmSelection.getModel();
						Algorithm algorithm = (Algorithm) model.getSelectedItem();

						if (algorithm != null)
						{
							// set color in engine
							boolean isHumanPilot = algorithm.isHumanPilot();
							appEngine.setRobotsColor(0, color, isHumanPilot);
						}
					}
				}
			});
		}
		return comboBox_colorSelection;
	}

	/**
	 * @return spinner_speed
	 */
	private JSpinner getSpinner_speed()
	{
		if (spinner_speed == null)
		{
			spinner_speed = new JSpinner();
			spinner_speed.setMaximumSize(new Dimension(80, 27));
			spinner_speed.setMinimumSize(new Dimension(80, 27));
			spinner_speed.setPreferredSize(new Dimension(80, 27));
			
			float speedValue = PropertyManager.getSharedInstance().getFloatProperty(ApplicationProperties.DEFAULT_ROBOT_SPEED);
			int speed = (int) (speedValue * 100);
			
			spinner_speed.setModel(new SpinnerNumberModel(speed, 0, 99, 1));
			spinner_speed.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if (!changeLock && appEngine != null)
					{
						// get the algorithm
						PluginSelectionComboboxModel<Algorithm> model = (PluginSelectionComboboxModel<Algorithm>) comboBox_algorithmSelection.getModel();
						Algorithm algorithm = (Algorithm) model.getSelectedItem();

						if (algorithm != null)
						{
							// set lights number in engine
							boolean isHumanPilot = algorithm.isHumanPilot();

							int value = (Integer) spinner_speed.getValue();
							float speed = ((float) value) / 100.0f;

							appEngine.setRobotsSpeed(0, speed, isHumanPilot);
						}
					}
				}
			});
		}
		return spinner_speed;
	}

	/**
	 * @return the spinner_lightsNumber
	 */
	private JSpinner getSpinner_lightsNumber()
	{
		if (spinner_lightsNumber == null)
		{
			spinner_lightsNumber = new JSpinner();
			spinner_lightsNumber.setMaximumSize(new Dimension(80, 27));
			spinner_lightsNumber.setMinimumSize(new Dimension(80, 27));
			spinner_lightsNumber.setPreferredSize(new Dimension(80, 27));
			spinner_lightsNumber.setModel(new SpinnerNumberModel(0, 0, 99, 1));
			spinner_lightsNumber.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if (!changeLock && appEngine != null)
					{
						// get the algorithm
						PluginSelectionComboboxModel<Algorithm> model = (PluginSelectionComboboxModel<Algorithm>) comboBox_algorithmSelection.getModel();
						Algorithm algorithm = (Algorithm) model.getSelectedItem();

						if (algorithm != null)
						{
							// set lights number in engine
							boolean isHumanPilot = algorithm.isHumanPilot();
							appEngine.setRobotsMaxLights(0, (Integer) spinner_lightsNumber.getValue(), isHumanPilot);
						}
					}
				}
			});
		}
		return spinner_lightsNumber;
	}

	/**
	 * @return the comboBox_algorithmSelection
	 */
	private JComboBox getComboBox_algorithmSelection()
	{
		if (comboBox_algorithmSelection == null)
		{
			comboBox_algorithmSelection = new JComboBox();
			comboBox_algorithmSelection.setPrototypeDisplayValue("XXXXXXX");
			comboBox_algorithmSelection.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
			comboBox_algorithmSelection.setMaximumSize(new Dimension(150, 27));
			comboBox_algorithmSelection.setMinimumSize(new Dimension(150, 27));
			comboBox_algorithmSelection.setPreferredSize(new Dimension(150, 27));

			// apply a PluginSelectionComboboxModel with generics for Algorithms to this combobox
			comboBox_algorithmSelection.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (!changeLock)
					{
						// the selection produces a modification of the array of types, and a call
						// to
						// checkTypes.
						PluginSelectionComboboxModel<Algorithm> model = (PluginSelectionComboboxModel<Algorithm>) comboBox_algorithmSelection.getModel();
						Algorithm algorithm = (Algorithm) model.getSelectedItem();

						if (algorithm != null)
						{
							types.put(comboBox_algorithmSelection, algorithm.getType());

							// check types to update the GUI
							if (checkTypes() && appEngine != null)
							{
								// update speed values
								float speedvalue = PropertyManager.getSharedInstance().getFloatProperty(ApplicationProperties.DEFAULT_ROBOT_SPEED);
								if (algorithm.isHumanPilot())
								{
									int speed = (int) (speedvalue * 50);
									getSpinner_speed().setValue(speed);
								}
								else
								{
									int speed = (int) (speedvalue * 100);
									getSpinner_speed().setValue(speed);
								}

								// eventually create robots in engine
								updateRobotsInEngine(0, appEngine);
							}
						}
						else
						{
							types.remove(comboBox_algorithmSelection);
							if (appEngine != null)
							{
								updateRobotsInEngine(0, appEngine);
							}
						}
					}
				}
			});
		}
		return comboBox_algorithmSelection;
	}

	/**
	 * @return the spinner_robotsNumber
	 */
	private JSpinner getSpinner_robotsNumber()
	{
		if (spinner_robotsNumber == null)
		{
			spinner_robotsNumber = new JSpinner();
			spinner_robotsNumber.setMaximumSize(new Dimension(80, 27));
			spinner_robotsNumber.setMinimumSize(new Dimension(80, 27));
			spinner_robotsNumber.setPreferredSize(new Dimension(80, 27));
			spinner_robotsNumber.setModel(new SpinnerNumberModel(1, 1, 100, 1));
			spinner_robotsNumber.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if (!changeLock && appEngine != null)
					{
						updateRobotsInEngine(0, appEngine);
					}
				}
			});
		}
		return spinner_robotsNumber;
	}

	/**
	 * @return the button_addRow
	 */
	private JButton getButton_addRow()
	{
		if (button_addRow == null)
		{
			button_addRow = new JButton();
			button_addRow.setIcon(new ImageIcon(getClass().getResource("/it/diunipi/volpi/sycamore/resources/plus_green_16x16.png")));
			button_addRow.setPreferredSize(new Dimension(27, 27));
			button_addRow.setMinimumSize(new Dimension(27, 27));
			button_addRow.setMaximumSize(new Dimension(27, 27));
			button_addRow.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (!changeLock)
					{
						// the plus button adds a new row
						addRowFromAdditionalsComponents();
						if (appEngine != null)
						{
							appEngine.addNewRobotListElement();
						}
					}
				}
			});
		}
		return button_addRow;
	}

	/**
	 * Each call returns a new instance!
	 * 
	 * @return the comboBox_algorithmSelection
	 */
	private AdditionalComboBox getNewComboBox_colorSelection()
	{
		int index = additionalColorsComboboxes.size() + 1;

		final AdditionalComboBox comboBox_additionalColorSelection = new AdditionalComboBox(index);
		comboBox_additionalColorSelection.setMaximumSize(new Dimension(40, 27));
		comboBox_additionalColorSelection.setMinimumSize(new Dimension(40, 27));
		comboBox_additionalColorSelection.setPreferredSize(new Dimension(40, 27));

		ColorComboboxModel model = new ColorComboboxModel(((ColorComboboxModel) getComboBox_colorSelection().getModel()).getColors());
		ColorComboboxRenderer renderer = new ColorComboboxRenderer();

		if (index == (model.getSize() - 1))
		{
			// add a new Color in every model
			ColorRGBA newColor = SycamoreUtil.getRandomColor();

			model.addColor(newColor);
			((ColorComboboxModel) getComboBox_colorSelection().getModel()).addColor(newColor);

			for (AdditionalComboBox combobox : additionalColorsComboboxes)
			{
				((ColorComboboxModel) combobox.getModel()).addColor(newColor);
			}
		}

		comboBox_additionalColorSelection.setModel(model);
		comboBox_additionalColorSelection.setRenderer(renderer);
		comboBox_additionalColorSelection.setSelectedIndex(index + 1);

		comboBox_additionalColorSelection.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!changeLock && appEngine != null)
				{
					// get color
					ColorRGBA color = (ColorRGBA) comboBox_additionalColorSelection.getSelectedItem();
					int index = comboBox_additionalColorSelection.getIndex();

					// geth index-th algorithm
					PluginSelectionComboboxModel<Algorithm> model = (PluginSelectionComboboxModel<Algorithm>) additionalComboboxes.elementAt(index - 1).getModel();
					Algorithm algorithm = (Algorithm) model.getSelectedItem();

					if (algorithm != null)
					{
						// set color in engine
						boolean isHumanPilot = algorithm.isHumanPilot();
						appEngine.setRobotsColor(index, color, isHumanPilot);
					}
				}
			}
		});

		return comboBox_additionalColorSelection;
	}

	/**
	 * @return spinner_additionalSpeed
	 */
	private AdditionalSpinner getNewSpinner_speed()
	{
		final AdditionalSpinner spinner_additionalSpeed = new AdditionalSpinner(additionalSpeedSpinners.size() + 1);
		spinner_additionalSpeed.setMaximumSize(new Dimension(80, 27));
		spinner_additionalSpeed.setMinimumSize(new Dimension(80, 27));
		spinner_additionalSpeed.setPreferredSize(new Dimension(80, 27));
		
		float speedValue = PropertyManager.getSharedInstance().getFloatProperty(ApplicationProperties.DEFAULT_ROBOT_SPEED);
		int speed = (int) (speedValue * 100);
		
		spinner_additionalSpeed.setModel(new SpinnerNumberModel(speed, 0, 99, 1));
		spinner_additionalSpeed.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (!changeLock && appEngine != null)
				{
					int index = spinner_additionalSpeed.getIndex();

					// get index-th algorithm
					PluginSelectionComboboxModel<Algorithm> model = (PluginSelectionComboboxModel<Algorithm>) additionalComboboxes.elementAt(index - 1).getModel();
					Algorithm algorithm = (Algorithm) model.getSelectedItem();

					if (algorithm != null)
					{
						// set lights number in engine
						boolean isHumanPilot = algorithm.isHumanPilot();

						int value = (Integer) spinner_additionalSpeed.getValue();
						float speed = ((float) value) / 100.0f;

						appEngine.setRobotsSpeed(index, speed, isHumanPilot);
					}
				}
			}
		});

		return spinner_additionalSpeed;
	}

	/**
	 * @return the spinner_lightsNumber
	 */
	private AdditionalSpinner getNewSpinner_lightsNumber()
	{
		final AdditionalSpinner spinner_additionalLightsNumber = new AdditionalSpinner(additionalLightSpinners.size() + 1);
		spinner_additionalLightsNumber.setMaximumSize(new Dimension(80, 27));
		spinner_additionalLightsNumber.setMinimumSize(new Dimension(80, 27));
		spinner_additionalLightsNumber.setPreferredSize(new Dimension(80, 27));
		spinner_additionalLightsNumber.setModel(new SpinnerNumberModel(0, 0, 99, 1));
		spinner_additionalLightsNumber.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (!changeLock && appEngine != null)
				{
					int index = spinner_additionalLightsNumber.getIndex();

					// get index-th algorithm
					PluginSelectionComboboxModel<Algorithm> model = (PluginSelectionComboboxModel<Algorithm>) additionalComboboxes.elementAt(index - 1).getModel();
					Algorithm algorithm = (Algorithm) model.getSelectedItem();

					if (algorithm != null)
					{
						// set lights number in engine
						boolean isHumanPilot = algorithm.isHumanPilot();
						appEngine.setRobotsMaxLights(index, (Integer) additionalLightSpinners.elementAt(index - 1).getValue(), isHumanPilot);
					}
				}
			}
		});
		return spinner_additionalLightsNumber;
	}

	/**
	 * Each call returns a new instance!
	 * 
	 * @return the comboBox_algorithmSelection
	 */
	private AdditionalComboBox getNewComboBox_algorithmSelection()
	{
		final AdditionalComboBox comboBox_additionalAlgorithmSelection = new AdditionalComboBox(additionalComboboxes.size() + 1);
		comboBox_additionalAlgorithmSelection.setPrototypeDisplayValue("XXXXXXX");
		comboBox_additionalAlgorithmSelection.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		comboBox_additionalAlgorithmSelection.setMaximumSize(new Dimension(70, 27));
		comboBox_additionalAlgorithmSelection.setMinimumSize(new Dimension(70, 27));
		comboBox_additionalAlgorithmSelection.setPreferredSize(new Dimension(70, 27));

		// apply a PluginSelectionComboboxModel with generics for Algorithms to this combobox
		ArrayList<Algorithm> algorithms = SycamorePluginManager.getSharedInstance().getLoadedAlgorithms();
		PluginSelectionComboboxModel<Algorithm> algorithmsModel = new PluginSelectionComboboxModel<Algorithm>(algorithms);

		comboBox_additionalAlgorithmSelection.setModel(algorithmsModel);
		comboBox_additionalAlgorithmSelection.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!changeLock)
				{
					// the selection produces a modification of the array of types, and a call to
					// checkTypes.
					PluginSelectionComboboxModel<Algorithm> model = (PluginSelectionComboboxModel<Algorithm>) comboBox_additionalAlgorithmSelection.getModel();
					Algorithm algorithm = (Algorithm) model.getSelectedItem();
					int index = comboBox_additionalAlgorithmSelection.getIndex();

					if (algorithm != null)
					{
						types.put(comboBox_additionalAlgorithmSelection, algorithm.getType());

						// check types to update the GUI
						if (checkTypes())
						{
							if (appEngine != null)
							{
								// update speed values
								float speedvalue = PropertyManager.getSharedInstance().getFloatProperty(ApplicationProperties.DEFAULT_ROBOT_SPEED);
								if (algorithm.isHumanPilot())
								{
									int speed = (int) (speedvalue * 50);
									additionalSpeedSpinners.elementAt(index - 1).setValue(speed);
								}
								else
								{
									int speed = (int) (speedvalue * 100);
									additionalSpeedSpinners.elementAt(index - 1).setValue(speed);
								}
								
								// eventually create robots in engine
								updateRobotsInEngine(index, appEngine);
							}
						}
					}
					else
					{
						types.remove(comboBox_algorithmSelection);

						if (appEngine != null)
						{
							updateRobotsInEngine(index, appEngine);
						}
					}
				}
			}
		});
		return comboBox_additionalAlgorithmSelection;
	}

	/**
	 * Each call returns a new instance!
	 * 
	 * @return the spinner_robotsNumber
	 */
	private AdditionalSpinner getNewSpinner_robotsNumber()
	{
		final AdditionalSpinner spinner_additionalRobotsNumber = new AdditionalSpinner(additionalSpinners.size() + 1);
		spinner_additionalRobotsNumber.setMaximumSize(new Dimension(80, 27));
		spinner_additionalRobotsNumber.setMinimumSize(new Dimension(80, 27));
		spinner_additionalRobotsNumber.setPreferredSize(new Dimension(80, 27));
		spinner_additionalRobotsNumber.setModel(new SpinnerNumberModel(1, 1, 999, 1));
		spinner_additionalRobotsNumber.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (!changeLock && appEngine != null)
				{
					updateRobotsInEngine(spinner_additionalRobotsNumber.getIndex(), appEngine);
				}
			}
		});

		return spinner_additionalRobotsNumber;
	}

	/**
	 * Each call returns a new instance!
	 * 
	 * @return the button_addRow
	 */
	private AdditionalButton getNewButton_removeRow()
	{
		final AdditionalButton button_removeRow = new AdditionalButton(additionalButtons.size() + 1);

		button_removeRow.setIcon(new ImageIcon(getClass().getResource("/it/diunipi/volpi/sycamore/resources/minus_red_16x16.png")));
		button_removeRow.setPreferredSize(new Dimension(27, 27));
		button_removeRow.setMinimumSize(new Dimension(27, 27));
		button_removeRow.setMaximumSize(new Dimension(27, 27));
		button_removeRow.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!changeLock)
				{
					// the minus button removes the current row
					int index = button_removeRow.getIndex();
					removeRowFromAdditionalComponents(index);

					if (appEngine != null)
					{

						Vector<SycamoreRobot> robotsToRemove = appEngine.removeRobotListElement(index);

						for (SycamoreRobot robot : robotsToRemove)
						{
							fireActionEvent(new ActionEvent(robot, 0, SycamoreFiredActionEvents.REMOVE_ROBOT_FROM_SCENE.name()));
						}
					}

					checkTypes();
				}
			}
		});

		return button_removeRow;
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

	/**
	 * Changes the current app engine replacing it with passed one.
	 * 
	 * @param appEngine
	 */
	public void setAppEngine(SycamoreEngine appEngine)
	{
		this.appEngine = appEngine;
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
		getComboBox_algorithmSelection().setEnabled(enabled);
		getComboBox_colorSelection().setEnabled(enabled);
		getSpinner_robotsNumber().setEnabled(enabled);
		getButton_addRow().setEnabled(enabled);

		for (int i = 0; i < additionalComboboxes.size(); i++)
		{
			additionalComboboxes.get(i).setEnabled(enabled);
			additionalColorsComboboxes.get(i).setEnabled(enabled);
			additionalSpinners.get(i).setEnabled(enabled);
			additionalButtons.get(i).setEnabled(enabled);
		}
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
			SycamoreRobotMatrix robotMatrix = appEngine.getRobots();
			int rows = robotMatrix.robotsRowCount();

			// first robot row
			{
				updateGuiRow(0);
			}

			for (int i = 0; i < (rows - 1); i++)
			{
				if (i >= additionalComboboxes.size())
				{
					addRowFromAdditionalsComponents();
				}
				updateGuiRow(i + 1);
			}
		}

		changeLock = false;
	}

	/**
	 * @param index
	 */
	private void updateGuiRow(int index)
	{
		SycamoreRobotMatrix robotMatrix = appEngine.getRobots();

		// get robots. Since just one vector between robots and human pilot robots has data
		// inside, I can merge them without worry
		Vector<SycamoreRobot> robots = new Vector<SycamoreRobot>();
		robots.addAll(robotMatrix.getRobotRow(index));
		robots.addAll(robotMatrix.getHumanPilotRow(index));

		JComboBox combobox_algorithmSelection = (index == 0 ? getComboBox_algorithmSelection() : additionalComboboxes.get(index - 1));
		JComboBox combobox_colorSelection = (index == 0 ? getComboBox_colorSelection() : additionalColorsComboboxes.get(index - 1));
		JSpinner spinner_speed = (index == 0 ? getSpinner_speed() : additionalSpeedSpinners.get(index - 1));
		JSpinner spinner_lightsNumber = (index == 0 ? getSpinner_lightsNumber() : additionalLightSpinners.get(index - 1));
		JSpinner spinner_robotsNumber = (index == 0 ? getSpinner_robotsNumber() : additionalSpinners.get(index - 1));

		if (!robots.isEmpty())
		{
			SycamoreRobot robot = robots.firstElement();

			// write algorithm
			ComboBoxModel model = combobox_algorithmSelection.getModel();
			for (int j = 0; j < model.getSize(); j++)
			{
				Algorithm item = (Algorithm) model.getElementAt(j);
				Algorithm current = robot.getAlgorithm();
				if (item != null && current != null && item.getPluginName().equals(current.getPluginName()))
				{
					combobox_algorithmSelection.setSelectedIndex(j);
				}
			}

			// write color
			ComboBoxModel colorModel = combobox_colorSelection.getModel();
			for (int j = 0; j < colorModel.getSize(); j++)
			{
				ColorRGBA item = (ColorRGBA) colorModel.getElementAt(j);
				ColorRGBA current = robot.getColor();
				if (item != null && current != null && item.equals(current))
				{
					combobox_colorSelection.setSelectedIndex(j);
				}
			}

			// write lights number
			spinner_speed.setValue((int) (robot.getSpeed() * 100));

			// write lights number
			spinner_lightsNumber.setValue(robot.getLights().size());

			// write robots number
			spinner_robotsNumber.setValue(robots.size());
		}
	}

	/**
	 * Updates the number of robots in the engine to match the one defined by the user using the
	 * spinners provided by this class.
	 */
	private void updateRobotsInEngine(int index, SycamoreEngine engine)
	{
		if (engine != null)
		{
			JSpinner selectedSpinner = (index == 0 ? getSpinner_robotsNumber() : additionalSpinners.get(index - 1));
			JComboBox algorithmCombobox = (index == 0 ? getComboBox_algorithmSelection() : additionalComboboxes.get(index - 1));
			JComboBox colorCombobox = (index == 0 ? getComboBox_colorSelection() : additionalColorsComboboxes.get(index - 1));
			JSpinner speedSpinner = (index == 0 ? getSpinner_speed() : additionalSpeedSpinners.get(index - 1));
			JSpinner lightSpinner = (index == 0 ? getSpinner_lightsNumber() : additionalLightSpinners.get(index - 1));

			// get selected algorithm, and the corresponding type and robot type
			Algorithm algorithm = (Algorithm) algorithmCombobox.getSelectedItem();
			ColorRGBA color = (ColorRGBA) colorCombobox.getSelectedItem();

			if (algorithm != null)
			{
				boolean isHumanPilot = algorithm.isHumanPilot();
				Vector<SycamoreRobot> robotsVector = isHumanPilot ? engine.getHumanPilotRobots(index) : engine.getRobots(index);
				Vector<SycamoreRobot> otherVector = isHumanPilot ? engine.getRobots(index) : engine.getHumanPilotRobots(index);

				// remove all elements from other vector
				int size = otherVector.size();
				for (int i = 0; i < size; i++)
				{
					SycamoreRobot robot = otherVector.remove(0);
					fireActionEvent(new ActionEvent(robot, 0, SycamoreFiredActionEvents.REMOVE_ROBOT_FROM_SCENE.name()));
				}

				// check if robots has to be added or removed
				if ((Integer) selectedSpinner.getValue() > robotsVector.size())
				{
					// add robots. Compute how many
					int numRobots = (Integer) selectedSpinner.getValue() - robotsVector.size();

					// compute default lights number
					int maxLights = (Integer) lightSpinner.getValue();
					int speedValue = (Integer) speedSpinner.getValue();
					float speed = ((float) speedValue) / 100.0f;

					for (int i = 0; i < numRobots; i++)
					{
						// add in engine
						SycamoreRobot robot = engine.createAndAddNewRobotInstance(isHumanPilot, index, color, maxLights, speed);
						fireActionEvent(new ActionEvent(robot, 0, SycamoreFiredActionEvents.ADD_ROBOT_IN_SCENE.name()));
					}
				}
				else
				{
					// remove robots. Compute how many
					int numRobots = robotsVector.size() - (Integer) selectedSpinner.getValue();

					for (int i = 0; i < numRobots; i++)
					{
						// remove from engine
						SycamoreRobot robot = engine.removeRobot(isHumanPilot, index);
						fireActionEvent(new ActionEvent(robot, 0, SycamoreFiredActionEvents.REMOVE_ROBOT_FROM_SCENE.name()));
					}
				}

				// set alg
				try
				{
					engine.createAndSetNewAlgorithmInstance(algorithm, index);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				// one of these vector will be empty, the other will not
				Vector<SycamoreRobot> robots = appEngine.getRobots().getRobotRow(index);
				Vector<SycamoreRobot> humanPilotRobots = appEngine.getRobots().getHumanPilotRow(index);

				// remove robots
				for (SycamoreRobot robot : robots)
				{
					fireActionEvent(new ActionEvent(robot, 0, SycamoreFiredActionEvents.REMOVE_ROBOT_FROM_SCENE.name()));
				}
				robots.removeAllElements();

				// remove human pilot robots
				for (SycamoreRobot humanPilotRobot : humanPilotRobots)
				{
					fireActionEvent(new ActionEvent(humanPilotRobot, 0, SycamoreFiredActionEvents.REMOVE_ROBOT_FROM_SCENE.name()));
				}
				humanPilotRobots.removeAllElements();
			}
		}
	}

	/**
	 * Returns the type chosen by the user. If more than one type is chosen, returns one of them if
	 * they are all consistent, otherwise return null.
	 * 
	 * @return
	 */
	public TYPE getChosenType()
	{
		if (isTypeGood())
		{
			Set<JComboBox> keys = this.types.keySet();

			// call once next() on the iterator. It will return the first element, since we know
			// that types is not empty because types are good.
			JComboBox first = keys.iterator().next();
			return this.types.get(first);
		}
		else
			return null;
	}

	/**
	 * Updates passed engine to make it have the robots number and settings consistent with the
	 * values selected in the user controls provided by this class.
	 * 
	 * @param newEngine
	 */
	public void updateEngine(SycamoreEngine newEngine)
	{
		// size + 1 includes the first combobox
		for (int i = 0; i < additionalComboboxes.size() + 1; i++)
		{
			JComboBox combobox = null;
			if (i == 0)
			{
				combobox = getComboBox_algorithmSelection();
			}
			else
			{
				combobox = additionalComboboxes.elementAt(i - 1);
			}

			newEngine.addNewRobotListElement();
			if (combobox.getSelectedItem() != null)
			{
				updateRobotsInEngine(i, newEngine);
			}
		}
	}

	/**
	 * Updates the combo box models
	 */
	public void updateModels()
	{
		// apply a PluginSelectionComboboxModel with generics for Algorithms to this combobox
		ArrayList<Algorithm> algorithms = SycamorePluginManager.getSharedInstance().getLoadedAlgorithms();
		PluginSelectionComboboxModel<Algorithm> algorithmsModel = new PluginSelectionComboboxModel<Algorithm>(algorithms);

		getComboBox_algorithmSelection().setModel(algorithmsModel);

		for (AdditionalComboBox comboBox : additionalComboboxes)
		{
			comboBox.setModel(new PluginSelectionComboboxModel<Algorithm>(algorithms));
		}
	}

	/**
	 * @return label_algorithm
	 */
	private JLabel getLabel_algorithm()
	{
		if (label_algorithm == null)
		{
			label_algorithm = new JLabel("Algorithm:");
		}
		return label_algorithm;
	}

	/**
	 * @return label_color
	 */
	private JLabel getLabel_color()
	{
		if (label_color == null)
		{
			label_color = new JLabel("Color:");
		}
		return label_color;
	}

	/**
	 * @return
	 */
	private JLabel getLabel_speed()
	{
		if (label_speed == null)
		{
			label_speed = new JLabel("Speed:");
		}
		return label_speed;
	}

	/**
	 * @return label_lightsNum
	 */
	private JLabel getLabel_lightsNum()
	{
		if (label_lightsNum == null)
		{
			label_lightsNum = new JLabel("Lights num:");
			label_lightsNum.setHorizontalAlignment(SwingConstants.CENTER);
			label_lightsNum.setHorizontalTextPosition(SwingConstants.CENTER);
			label_lightsNum.setPreferredSize(new Dimension(90, 16));
		}
		return label_lightsNum;
	}

	/**
	 * @return label_robotsNum
	 */
	private JLabel getLabel_robotsNum()
	{
		if (label_robotsNum == null)
		{
			label_robotsNum = new JLabel("Robots num:");
			label_robotsNum.setHorizontalTextPosition(SwingConstants.CENTER);
			label_robotsNum.setHorizontalAlignment(SwingConstants.CENTER);
			label_robotsNum.setPreferredSize(new Dimension(90, 16));
		}
		return label_robotsNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.sycamore.gui.SycamorePanel#reset()
	 */
	@Override
	public void reset()
	{
		types.clear();

		this.removeAll();

		additionalButtons.clear();
		additionalColorsComboboxes.clear();
		additionalComboboxes.clear();
		additionalLightSpinners.clear();
		additionalSpinners.clear();

		if (button_addRow != null)
		{
			button_addRow = null;
		}
		if (comboBox_algorithmSelection != null)
		{
			comboBox_algorithmSelection = null;
		}
		if (comboBox_colorSelection != null)
		{
			comboBox_colorSelection = null;
		}
		if (spinner_lightsNumber != null)
		{
			spinner_lightsNumber = null;
		}
		if (spinner_robotsNumber != null)
		{
			spinner_robotsNumber = null;
		}

		this.initialize();
		this.revalidate();
		this.repaint();

		this.updateModels();
	}
}
