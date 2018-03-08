package org.ilite.frc.robot.commands;

public class Delay implements ICommand {
	private double mDelayDuration;
	private double triggeredTime;
	public Delay(double pDelayDuration) {
		mDelayDuration = pDelayDuration;
	}
	@Override
	public void initialize(double pNow) {
	  triggeredTime = pNow;

	}

	@Override
	public boolean update(double pNow) {
		if(pNow - triggeredTime < mDelayDuration) {
			return false;
		}
		return true;
		
	}


	@Override
	public void shutdown(double pNow) {

	}

}

