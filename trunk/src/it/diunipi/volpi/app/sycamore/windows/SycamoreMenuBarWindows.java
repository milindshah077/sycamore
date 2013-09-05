package it.diunipi.volpi.app.sycamore.windows;

import it.diunipi.volpi.app.sycamore.SycamoreMenuBar;

import javax.swing.JSeparator;

/**
 * The Sycamore menu bar for Windows
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
public class SycamoreMenuBarWindows extends SycamoreMenuBar
{
	private static final long	serialVersionUID					= 5893076147267552708L;

	/**
	 * Default constructor
	 */
	public SycamoreMenuBarWindows(SycamoreAppWindows application)
	{
		super(application);
		setupMenuBar();
	}

	/* (non-Javadoc)
	 * @see it.diunipi.volpi.app.sycamore.SycamoreMenuBar#setupMenuBar()
	 */
	@Override
	protected void setupMenuBar()
	{
		// add the preferences menu
		this.setupPreferencesmenu();

		// add menu items under File menu
		getMenu_file().add(getMenuItem_new());
		getMenu_file().add(getMenuItem_newBatch());
		getMenu_file().add(getMenuItem_open());
		getMenu_file().add(getMenu_openRecent());
		getMenu_file().add(new JSeparator());
		getMenu_file().add(getMenuItem_save());
		getMenu_file().add(getMenuItem_saveAs());

		// add menu items under Edit menu
		getMenu_file().add(getMenuItem_Import());
		getMenu_file().add(getMenuItem_Export());
		getMenu_file().add(new JSeparator());
		getMenu_file().add(getMenu_switchWorkspace());
		getMenu_file().add(new JSeparator());
		getMenu_file().add(getMenuItem_preferences());
		
		// add menu items under View menu
		getMenu_view().add(getCheckBoxmenuItem_axes());
		getMenu_view().add(getCheckBoxmenuItem_grid());
		getMenu_view().add(getCheckBoxmenuItem_visibilityRange());
		getMenu_view().add(getCheckBoxmenuItem_visibilityGraph());
		getMenu_view().add(getCheckBoxmenuItem_directions());
		getMenu_view().add(getCheckBoxmenuItem_localCoords());
		getMenu_view().add(getCheckBoxmenuItem_baricentrum());
		getMenu_view().add(getCheckBoxmenuItem_lights());
		getMenu_view().add(getCheckBoxmenuItem_visualSupports());

		// add menu items under Help menu
		getMenu_help().add(getMenuItem_help());
		getMenu_help().add(getMenuItem_about());

		// add menus to menubar
		this.add(getMenu_file());
		this.add(getMenu_edit());
		this.add(getMenu_view());
		this.add(getMenu_help());
	}

	/* (non-Javadoc)
	 * @see it.diunipi.volpi.app.sycamore.SycamoreMenuBar#setupPreferencesmenu()
	 */
	@Override
	protected void setupPreferencesmenu()
	{
		
	}
	
	/* (non-Javadoc)
	 * @see it.diunipi.volpi.app.sycamore.SycamoreMenuBar#setupAboutMenu()
	 */
	@Override
	protected void setupAboutMenu()
	{
		
	}
}
