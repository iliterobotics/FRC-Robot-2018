package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.robot.modules.DriveTrain;
import edu.wpi.first.wpilibj.command.Command;
import openrio.powerup.MatchData;
import org.ilite.frc.common.types.*;

public class GetAutonomous {
	private EStartingPosition startingPos;
	private ECubeAction cubeAction;
	private ECross crossType;
	private boolean doComplexAutonomous = false;

	public List<Command> getAutonomous(DriveTrain driveTrain) {
		List<Command> commands = new ArrayList<Command>();
		commands.clear();

		MatchData.OwnedSide switchSide = getSwitchData();
		MatchData.OwnedSide scaleSide = getScaleData();
		
		if(doComplexAutonomous) {
			
		}
		else
		{
			//Drive foward (really complex).
		}
		
	}
	
	

	public MatchData.OwnedSide getSwitchData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
	}

	public MatchData.OwnedSide getScaleData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SCALE);

	}

}
