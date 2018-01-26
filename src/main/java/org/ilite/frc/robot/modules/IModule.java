package org.ilite.frc.robot.modules;

import org.ilite.frc.robot.controlloop.IControlLoop;

public interface IModule {
	//public abstract void init(double pNow);
  	/**
  	 * Called to update the module in teleop
  	 * @param pNow
  	 */
	//public abstract void update(double pNow);
	public abstract void shutdown(double pNow);
	void initialize(double pNow);
	boolean update(double pNow);
}
