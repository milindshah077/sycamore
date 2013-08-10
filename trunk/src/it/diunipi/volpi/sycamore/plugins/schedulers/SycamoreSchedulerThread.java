/**
 * 
 */
package it.diunipi.volpi.sycamore.plugins.schedulers;

import it.diunipi.volpi.sycamore.engine.SycamoreEngine;
import it.diunipi.volpi.sycamore.gui.SycamoreSystem;

/**
 * @author Vale
 * 
 */
public class SycamoreSchedulerThread extends Thread
{
	/**
	 * The possible states of the scheduler
	 * 
	 * @author Vale
	 */
	public static enum SCHEDULER_STATE
	{
		/**
		 * Scheduler not started yet
		 */
		NOT_STARTED,
		/**
		 * Scheduler running
		 */
		RUNNING,
		/**
		 * Scheduler paused
		 */
		PAUSED;
	}

	private SycamoreEngine	engine			= null;
	private int				roundCounter	= 0;
	private SCHEDULER_STATE	state			= SCHEDULER_STATE.NOT_STARTED;

	/**
	 * @param engine
	 *            the engine to set
	 */
	public void setEngine(SycamoreEngine engine)
	{
		this.engine = engine;
	}

	/**
	 * @return the engine
	 */
	public SycamoreEngine getEngine()
	{
		return engine;
	}

	/**
	 * Check if the thread is in paused state, and in this case starts waiting
	 */
	private void checkState()
	{
		if (state == SCHEDULER_STATE.PAUSED)
		{
			this.doWaitGui();
		}
	}

	/**
	 * Wait after a GUI pressure of the pause button
	 */
	public synchronized void doWaitGui()
	{
		synchronized (SycamoreSystem.getSchedulerGuiSynchronizer())
		{
			try
			{
				SycamoreSystem.getSchedulerGuiSynchronizer().wait();
			}
			catch (InterruptedException e)
			{
				System.err.println("Interrupted thread while sleeping.");
			}
		}
	}

	/**
	 * Continue execution after a wait
	 */
	public void play()
	{
		this.state = SCHEDULER_STATE.RUNNING;
		synchronized (SycamoreSystem.getSchedulerGuiSynchronizer())
		{
			SycamoreSystem.getSchedulerGuiSynchronizer().notifyAll();
		}
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void pause()
	{
		this.state = SCHEDULER_STATE.PAUSED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		super.run();

		// scheduler starts paused. The user will have to press play to start
		this.pause();
		while (true)
		{
			// check the state, if is paused or not
			checkState();
			
			if (engine != null && engine.getCurrentScheduler() != null)
			{
				if (engine.isSimulationFinished())
				{
					// manage the simulation finished
					manageSimulationFinished();
				}
				else
				{
					SchedulerImpl schedulerImpl = (SchedulerImpl) engine.getCurrentScheduler();
					
					if (roundCounter == 0)
					{
						// manage the runloop start
						schedulerImpl.runLoop_pre();
						schedulerImpl.updateTimelines();
						engine.performMeasuresSimulationStart();
					}

					// runloop iteration
					schedulerImpl.runLoopIteration();
					schedulerImpl.updateTimelines();
					
					engine.performMeasuresSimulationStep();

					roundCounter++;
					
					// sleep to have fixed frequency
					try
					{
						Thread.sleep((long) (SycamoreSystem.getSchedulerFrequency() * 1000));
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Perform operations after a finish of the simulation
	 */
	private void manageSimulationFinished()
	{
		if (engine != null)
		{
			SchedulerImpl schedulerImpl = (SchedulerImpl) engine.getCurrentScheduler();
			schedulerImpl.runLoop_post();
			schedulerImpl.updateTimelines();
			
			engine.performMeasuresSimulationEnd();
			engine.manageSimulationFinished(false);
		}

		roundCounter = 0;
		this.pause();
	}
}
