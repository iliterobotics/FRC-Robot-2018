package org.ilite.frc.robot.controlloop;

import edu.wpi.first.wpilibj.Notifier;

public class DefaultNotifier implements INotifier {
	
	

	private Notifier notif;

	@Override
	public void startPeriodic(double periodic) {
		if(notif != null) {
			notif.startPeriodic(periodic);
		}

	}

	@Override
	public void setRunnable(Runnable run) {
		notif = new Notifier(run);
		
	}
	
	@Override
	public void stop() {
		
		if(notif != null) { 
			notif.stop();
		}
		
	}

}
