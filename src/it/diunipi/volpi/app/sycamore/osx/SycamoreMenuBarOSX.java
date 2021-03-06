package it.diunipi.volpi.app.sycamore.osx;

import it.diunipi.volpi.app.sycamore.SycamoreMenuBar;
import it.diunipi.volpi.app.sycamore.SycamoreApp.APP_MODE;
import it.diunipi.volpi.sycamore.gui.SycamoreInfoPanel;
import it.diunipi.volpi.sycamore.gui.SycamorePrefsPane;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AboutHandler;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;

/**
 * The Sycamore menu bar for Mac OS X
 * 
 * @author Valerio Volpi - vale.v@me.com
 */
public class SycamoreMenuBarOSX extends SycamoreMenuBar
{
	private static final long	serialVersionUID	= 5893076147267552708L;

	/**
	 * Default constructor
	 */
	public SycamoreMenuBarOSX(SycamoreAppOSX application)
	{
		super(application);
		setupMenuBar();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.app.sycamore.SycamoreMenuBar#setupMenuBar()
	 */
	@Override
	protected void setupMenuBar()
	{
		// add the preferences menu and the about menu
		this.setupPreferencesmenu();
		this.setupAboutMenu();

		// add menu items under File menu
		if (application.getAppMode() == APP_MODE.SIMULATOR)
		{
			// new simulation is added just in SIMULATOR mode
			getMenu_file().add(getMenuItem_new());
			getMenu_file().add(getMenuItem_newBatch());
		}
		
		// open menus are added always
		getMenu_file().add(getMenuItem_open());
		getMenu_file().add(getMenu_openRecent());
		
		if (application.getAppMode() == APP_MODE.SIMULATOR)
		{
			// the following menus are added just in SIMULATOR mode
			getMenu_file().add(new JSeparator());
			getMenu_file().add(getMenuItem_save());
			getMenu_file().add(getMenuItem_saveAs());
			getMenu_file().add(new JSeparator());
			getMenu_file().add(getMenuItem_Import());
			getMenu_file().add(getMenuItem_Export());
			getMenu_file().add(new JSeparator());
			getMenu_file().add(getMenu_switchWorkspace());
		}

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

		// add menus to menubar
		this.add(getMenu_file());
		this.add(getMenu_view());
		this.add(getMenu_help());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.app.sycamore.SycamoreMenuBar#setupPreferencesmenu()
	 */
	@Override
	protected void setupPreferencesmenu()
	{
		Application app = Application.getApplication();
		app.setPreferencesHandler(new PreferencesHandler()
		{
			@Override
			public void handlePreferences(PreferencesEvent arg0)
			{
				ImageIcon icon = new ImageIcon(getClass().getResource("/it/diunipi/volpi/sycamore/resources/settings_64x64.png"));
				JOptionPane.showMessageDialog(null, new SycamorePrefsPane(), "Sycamore preferences", JOptionPane.INFORMATION_MESSAGE, icon);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.app.sycamore.SycamoreMenuBar#setupAboutMenu()
	 */
	@Override
	protected void setupAboutMenu()
	{
		Application app = Application.getApplication();
		app.setAboutHandler(new AboutHandler()
		{
			@Override
			public void handleAbout(AboutEvent arg0)
			{
				ImageIcon icon = new ImageIcon(getClass().getResource("/it/diunipi/volpi/sycamore/resources/sycamore_64x64.png"));
				JOptionPane.showMessageDialog(null, new SycamoreInfoPanel(), "About Sycamore", JOptionPane.INFORMATION_MESSAGE, icon);
			}
		});
	}
}
