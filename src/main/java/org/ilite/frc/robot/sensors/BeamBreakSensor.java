package org.ilite.frc.robot.sensors;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.robot.modules.IModule;

import edu.wpi.first.wpilibj.DigitalInput;

public class BeamBreakSensor implements IModule {
	private DigitalInput beamInput;
	
	public BeamBreakSensor(int ID) {
		beamInput = new DigitalInput(ID);
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
		
		return false;
	}
	
	public boolean isBroken() {
		return beamInput.get();
	}
	

}
