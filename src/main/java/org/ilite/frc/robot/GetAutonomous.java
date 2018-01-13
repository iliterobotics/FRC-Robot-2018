package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.robot.modules.DriveTrain;
import edu.wpi.first.wpilibj.command.Command;
import openrio.powerup.MatchData;
import org.ilite.frc.common.types.*;

public class GetAutonomous {
	private List<Command> commands;
	private EStartingPosition startingPos;
	private ECubeAction cubeAction;
	private ECross crossType;
	private boolean doComplexAutonomous;

	public List<Command> getAutonomous(DriveTrain driveTrain) {
		commands = new ArrayList<Command>();
		commands.clear();
		
		MatchData.OwnedSide scaleSide = getScaleData();
		MatchData.OwnedSide switchSide = getSwitchData();

		if (doComplexAutonomous) {

			switch (cubeAction) {
			case SWITCH:
				doSwitch(scaleSide, switchSide);
				break;
			case SCALE:
				doScale(scaleSide, switchSide);
				break;
			case EXCHANGE:
				doExchange();
				break;
			case NONE: 
				//drive foward?
				break;
			}

		} else {
			// Drive foward
		}

	}

	public void doScale(MatchData.OwnedSide scaleSide, MatchData.OwnedSide switchSide) {

		if (startingPos == EStartingPosition.LEFT && scaleSide == MatchData.OwnedSide.LEFT) {
			switch (crossType) {
			case CARPET:
				break;
			case PLATFORM:
				break;
			case NONE:
				break;
			}
		} else if (startingPos == EStartingPosition.RIGHT && scaleSide == MatchData.OwnedSide.RIGHT) {
			switch (crossType) {
			case CARPET:
				break;
			case PLATFORM:
				break;
			case NONE:
				break;
			}
		} else if (startingPos == EStartingPosition.MIDDLE || switchSide == scaleSide) {
			switch (crossType) {
			case CARPET:
				break;
			case PLATFORM:
				break;
			case NONE:
				break;
			}
		} else {// ignore driver preference.
				doSwitch(scaleSide, switchSide);
		}
	}

	public void doSwitch(MatchData.OwnedSide scaleSide, MatchData.OwnedSide switchSide) {// Switch autonomous routine.

		if (startingPos == EStartingPosition.LEFT && switchSide == MatchData.OwnedSide.LEFT) {
			switch (crossType) {
			case CARPET:
				break;
			case PLATFORM:
				break;
			case NONE:
				break;
			}
		} else if (startingPos == EStartingPosition.RIGHT && switchSide == MatchData.OwnedSide.RIGHT) {
			switch (crossType) {
			case CARPET:
				break;
			case PLATFORM:
				break;
			case NONE:
				break;
			}
		} else if (startingPos == EStartingPosition.MIDDLE || switchSide == scaleSide) {
			switch (crossType) {
			case CARPET:
				break;
			case PLATFORM:
				break;
			case NONE:
				break;
			}
		} else {// ignore driver preference.
			doScale(scaleSide, switchSide);
		}
	}

	public void doExchange() {// Exchange autonomous routine.
		switch (startingPos) {
		case LEFT:

			break;
		case MIDDLE:

			break;
		case RIGHT:

			break;
		}
	}

	public MatchData.OwnedSide getSwitchData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
	}

	public MatchData.OwnedSide getScaleData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SCALE);

	}

}
