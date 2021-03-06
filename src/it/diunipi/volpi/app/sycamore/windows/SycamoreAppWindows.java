package it.diunipi.volpi.app.sycamore.windows;

import it.diunipi.volpi.app.sycamore.SycamoreApp;

/**
 * The Windows version of Sycamore. Contains Windows specific code and does not start on any system
 * different from Microsof Windows.
 * 
 * @author Valerio Volpi - vale.v@me.com
 * 
 */
public class SycamoreAppWindows extends SycamoreApp
{
	private static final long	serialVersionUID	= 710781679614398606L;
	private SycamoreMenuBarWindows	menuBar_main		= null;

	/**
	 * Main method. Creates and runs a new SycamoreApp
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		APP_MODE mode;
		
		try
		{
			mode = APP_MODE.valueOf(args[0]);
		}
		catch (Exception e)
		{
			mode = APP_MODE.SIMULATOR;
		}
		new SycamoreAppWindows(mode);
	}

	/**
	 * Constructor for SycamoreApp
	 */
	public SycamoreAppWindows(APP_MODE appMode)
	{
		super(appMode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.app.sycamore.SycamoreApp#initialize_pre()
	 */
	@Override
	protected void initialize_pre()
	{
		super.initialize_pre();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.diunipi.volpi.app.sycamore.SycamoreApp#initialize()
	 */
	@Override
	protected void initialize()
	{
		super.initialize();

		// apply the menubar
		setJMenuBar(getMenuBar_main());
	}


	/* (non-Javadoc)
	 * @see it.diunipi.volpi.app.sycamore.SycamoreApp#getMenuBar_main()
	 */
	@Override
	protected SycamoreMenuBarWindows getMenuBar_main()
	{
		if (menuBar_main == null)
		{
			menuBar_main = new SycamoreMenuBarWindows(this);
		}
		return menuBar_main;
	}
}
