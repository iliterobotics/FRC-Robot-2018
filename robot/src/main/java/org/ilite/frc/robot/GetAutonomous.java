package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.command.Command;

public class GetAutonomous {
	boolean switchAutonomous = false; // Value will be determined by driver in future.
	
	
	public List<Command> getAutonomous() {
		List<Command> commands = new ArrayList<Command>();
		commands.clear();

		MatchData.OwnedSide switchSide = getSwitchData();
		MatchData.Ownedside scaleSide = getScaleData();

		if (switchAutonomous) {

			switch (switchSide) {

			case MatchData.OwnedSide.LEFT:

				break;
			case MatchData.OwnedSide.RIGHT:

				break;

			default:
				// default case for switch.
				break;

			}

		} else if (!switchAutonomous) {// scale

			switch (scaleSide) {
			case MatchData.OwnedSide.LEFT:

				break;

			case MatchData.OwnedSide.RIGHT:

				break;

			default:
				// default case for scale
				break;
			}
		}

		else {
			// Last resort
		}

	}

	public MatchData.OwnedSide getSwitchData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
		// Will return either MatchData.OwnedSide.LEFT or .RIGHT for switch.
	}

	public MatchData.getOwnedSide getScaleData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SCALE_NEAR);
		// Will return either MatchData.OwnedSide.LEFT or .RIGHT for scale.
	}

}
