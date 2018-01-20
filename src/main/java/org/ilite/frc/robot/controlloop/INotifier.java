package org.ilite.frc.robot.controlloop;

public interface INotifier {
	
	void setRunnable(Runnable run);

	void startPeriodic(double cONTROL_LOOP_PERIOD);

	void stop();

}
