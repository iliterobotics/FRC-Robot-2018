package org.ilite.frc.robot.commands;

public interface ICommand {
	void initialize(double pNow);
	boolean update(double pNow);
	void shutdown(double pNow);
}
