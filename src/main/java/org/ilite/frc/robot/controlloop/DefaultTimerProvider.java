package org.ilite.frc.robot.controlloop;

import edu.wpi.first.wpilibj.Timer;

public class DefaultTimerProvider implements ITimerProvider{
	
	@Override
	public double getFPGATimestamp() {
		return Timer.getFPGATimestamp();
	}

}
