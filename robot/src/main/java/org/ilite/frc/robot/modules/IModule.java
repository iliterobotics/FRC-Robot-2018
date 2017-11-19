package org.ilite.frc.robot.modules;

public interface IModule {
  public void initialize(double pNow);
	public void update(double pNow);
	public void shutdown(double pNow);
}
