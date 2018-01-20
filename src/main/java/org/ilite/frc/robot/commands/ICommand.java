package org.ilite.frc.robot.commands;

public interface ICommand {
	void initialize();
	boolean update();
	void shutdown();
}
