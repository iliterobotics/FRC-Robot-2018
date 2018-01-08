package org.ilite.frc.robot.modules;

import org.ilite.frc.robot.controlloop.IControlLoop;

public interface IModule {
	public abstract void initialize(double pNow);
  	/**
  	 * Called to update the module in teleop
  	 * @param pNow
  	 */
	public abstract void update(double pNow);
	public abstract void shutdown(double pNow);
}
