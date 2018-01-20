package org.ilite.frc.robot.controlloop;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.common.config.SystemSettings;

import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

/**
 * A class which uses the WPILIB Notifier mechanic to run our Modules on
 * a set time.  Tune SystemSettings.CONTROL_LOOP_PERIOD to the desired,
 * but monitor CPU usage.
 */
public class ControlLoopManager implements Runnable{
	private ILog mLog = Logger.createLog(ControlLoopManager.class);
	private final INotifier mWpiNotifier;
	private final Object mTaskLock = new Object();
	protected boolean mIsRunning = false;
	protected List<IControlLoop> mControlLoops = new ArrayList<>();  
	private double mLatestTime = 0d;
	private ITimerProvider mtimeProvider;

	public ControlLoopManager() { 
		this(new DefaultNotifier(), new DefaultTimerProvider());
	}

	ControlLoopManager(INotifier notfier, ITimerProvider timeProvider) {
		mWpiNotifier = notfier;
		mWpiNotifier.setRunnable(this);
		mtimeProvider = timeProvider;
	}

	public synchronized void setRunningControlLoops(IControlLoop... pControlLoops) {
		if(pControlLoops != null && pControlLoops.length>0) { 
			mControlLoops.clear();
			for(IControlLoop c : pControlLoops) mControlLoops.add(c);
		}
	}

	public synchronized void start() {
		if(!mIsRunning) {
			mLog.info("Starting control loop");
			synchronized(mTaskLock) {
				mLatestTime = mtimeProvider.getFPGATimestamp();
				for(IControlLoop c : mControlLoops) c.initialize(mLatestTime);
				mIsRunning = true;
			}
			mWpiNotifier.startPeriodic(SystemSettings.CONTROL_LOOP_PERIOD);
		}
	}

	public synchronized void stop() {
		if(mIsRunning) {
			mLog.info("Stopping control loop");
			mWpiNotifier.stop();
			synchronized(mTaskLock) {
				mIsRunning = false;
				mLatestTime = mtimeProvider.getFPGATimestamp();
				for(IControlLoop c : mControlLoops) c.shutdown(mLatestTime);
			}
		}
	}

	@Override
	public void run() {
		synchronized(mTaskLock) {
			try {
				if(mIsRunning) {
					mLatestTime = mtimeProvider.getFPGATimestamp();
					mapSensors();
					for(IControlLoop c : mControlLoops) {
						c.loop(mLatestTime);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void mapSensors() {
		//TODO change timestamp to mLatestTime
		//    ENavX.map(mData.navx, mHardware.getNavX());
	}
}
