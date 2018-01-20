package org.ilite.frc.robot.commands;

public interface Command {
	void initialize();
	boolean update();
	void shutdown();
}
