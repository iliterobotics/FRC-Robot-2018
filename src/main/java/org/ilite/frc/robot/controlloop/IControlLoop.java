package org.ilite.frc.robot.controlloop;

import org.ilite.frc.robot.modules.IModule;

public interface IControlLoop extends IModule {
	public void loop(double pNow);
}
