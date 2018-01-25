package org.ilite.frc.robot.sensors;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.modules.IModule;

import edu.wpi.first.wpilibj.DigitalInput;

public class BeamBreakSensor implements IModule {
	private DigitalInput beamInput;
	
	public BeamBreakSensor() {
		beamInput = new DigitalInput(SystemSettings.BEAM_INPUT_CHANNEL);
	}
	@Override
	public void shutdown(double pNow) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(double pNow) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean update(double pNow) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isBroken() {
		return beamInput.get();
	}

}
