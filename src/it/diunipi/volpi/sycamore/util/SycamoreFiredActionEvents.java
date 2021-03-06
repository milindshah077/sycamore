package it.diunipi.volpi.sycamore.util;

/**
 * The list of ActionEvents that are fired in the whole Sycamore Application
 * 
 * @author Vale
 */
public enum SycamoreFiredActionEvents
{
	JME_SCENE_READY("3D Scene is now ready to be used"),
	PLAY_ANIMATION("Play animation"),
	PAUSE_ANIMATION("Pause animation"),
	SETUP_SCENE("Setup the 3D Scene to change representation"),
	SIMULATION_DATA_GOOD("The user has selected all right simulation data"),
	SIMULATION_DATA_BAD("The user has selected some incompatible simulation data, or some data is missing"),
	SIMULATION_FINISHED("Simulation finished"),
	ADD_ROBOT_IN_SCENE("Add robot in JME scene"),
	REMOVE_ROBOT_FROM_SCENE("Remove robot from JME scene"),
	PLUGINS_REFRESHED("Plugins has been refreshed"),
	CAMERA_ON_ORIGIN("Camera on origin"),
	CAMERA_ON_BARICENTRUM("Camera on baricentrum"),
	SHOW_GRID("Show grid"),
	SHOW_AXES("Show coordinate axes"),
	SHOW_LIGHTS("Show robot lights"),
	SHOW_VISIBILITY_RANGE("Show visibility ranges"),
	SHOW_VISIBILITY_GRAPH("Show visibility graph"),
	SHOW_BARICENTRUM("Show system baricentrum"),
	SHOW_MOVEMENT_DIRECTIONS("Show movement directions"),
	SHOW_VISUAL_ELEMENTS("Show visual support elements"), 
	SHOW_LOCAL_COORDINATES("Show local coordinate systems"),
	SELECTED_VISIBILITY_CHANGED("Selected visibility changed"),
	SELECTED_AGREEMENT_CHANGED("Selected agreement changed"),
	SELECTED_INITIAL_CONDITION_CHANGED("Selected initial condition changed"),
	SELECTED_MEMORY_CHANGED("Selected memory changed"),
	UPDATE_GUI("Update the GUI"), 
	UPDATE_AGREEMENTS_GRAPHICS("Update the agreements graphics"), 
	LOAD_PLUGIN("Load a new plugin"),
	ROBOT_DIRECTION_CHANGED("The robot direction is now changed"),
	ROBOT_RATIO_CHANGED("The robot ratio is now changed"),
	ROBOT_DID_LOOK("The robot did perform a look"),
	ROBOT_DID_COMPUTE("The robot did perform a compute"),
	ROBOT_DID_MOVE("The robot did perform a move"),
	SWITCH_TOGGLE_SELECTED("Switch toggle selected");

	private String	description	= null;

	/**
	 * Constructor.
	 * 
	 * @param command
	 * @param description
	 */
	SycamoreFiredActionEvents(String description)
	{
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}
}
