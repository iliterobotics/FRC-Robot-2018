package org.ilite.frc.robot.sensors;
import org.ilite.frc.robot.modules.IModule;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class BeamBreakSensor implements IModule {
	private DigitalInput beamInput;
	private static final double TRIGGER_DELAY_SEC = 0.3;
	private double triggerTime;
	private double startTime;
	private boolean isTriggerScheduled = false;
	private boolean isBroken = true;
	
	public BeamBreakSensor(int ID) {
		this(new DigitalInput(ID));
	}
	
	public BeamBreakSensor(DigitalInput pInput) {
	  this.beamInput = pInput;
    isBroken = true;
	}
	
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(double pNow) {
	}

	@Override
	public boolean update(double pNow) {
		if(get() && !isTriggerScheduled) {
		  schedule();
		} else if(!get() && isTriggerScheduled) {
		  resetScheduling();
		}
		if(isTriggerScheduled && Timer.getFPGATimestamp() >= triggerTime) {
		  isBroken = true;
		}
		return false;
	}
	
	private boolean get() {
	  return beamInput.get();
	}
	
	public boolean isBroken() {
		return isBroken;
	}
	
	private void resetScheduling() {
	  isTriggerScheduled = false;
	  triggerTime = 0;
	  startTime = 0;
	  isBroken = false;
	}
	
	private void schedule() {
    isTriggerScheduled = true;
    startTime = Timer.getFPGATimestamp();
    triggerTime = startTime + TRIGGER_DELAY_SEC;
	}
	

}
