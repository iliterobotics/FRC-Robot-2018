package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.commands.ICommand;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.OwnedSide;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.common.types.ECubeAction;

import org.ilite.frc.common.types.*;

public class GetAutonomous implements ICommand {
	private NetworkTable autonTable;
	private NetworkTableEntry posEntry;
	private NetworkTableEntry crossEntry;
	private NetworkTableEntry cubeActionPrefsEntry;

	private List<ICommand> commands;
	private List<ECubeAction> cubeActionPrefs;
	private EStartingPosition startingPos;
	private ECross crossType;
	private boolean doComplexAutonomous;

	public GetAutonomous(NetworkTable autonTable) {
		this.autonTable = autonTable;
	}

	@Override
	public void initialize() {
		update();
		parseEntries();
	}

	@Override
	public boolean update() {
		try {
			posEntry = autonTable.getEntry("position");
			crossEntry = autonTable.getEntry("cross");
			cubeActionPrefsEntry = autonTable.getEntry("cubeActionList");
		} catch (Exception e) {

		}
		return true;
	}

	public List<ICommand> getAutonomous(DriveTrain driveTrain) {
		commands = new ArrayList<ICommand>();
		commands.clear();

		OwnedSide scaleSide = getScaleData();
		OwnedSide switchSide = getSwitchData();

		if (doComplexAutonomous) {

			for (ECubeAction action : cubeActionPrefs) {
				if (action == ECubeAction.SCALE) {
					if (!onMySide(scaleSide)) {
						cubeActionPrefs.remove(action);
					}

				} else if (action == ECubeAction.SWITCH) {
					if (!onMySide(switchSide)) {
						cubeActionPrefs.remove(action);
					}
				} else if (action == ECubeAction.EXCHANGE) {
					if (!onMySideExchange()) {
						cubeActionPrefs.remove(action);
					}
				}
			}

			if (!cubeActionPrefs.isEmpty()) {
				ECubeAction prefAction = cubeActionPrefs.get(0);
				switch (prefAction) {
				case SCALE:
					doScale();
					break;
				case SWITCH:
					doSwitch();
					break;
				case EXCHANGE:
					doExchange();
					break;
				default:
					break;
				}
			}

		} else {
			// Drive foward
		}

		return commands;

	}

	public void doScale() {
		switch (startingPos) {
		case LEFT:
			break;
		case MIDDLE:
			break;
		case RIGHT:
			break;
		}
	}

	public void doSwitch() {
		switch (startingPos) {
		case LEFT:
			break;
		case MIDDLE:
			break;
		case RIGHT:
			break;
		}
	}

	public void doExchange() {
		switch (startingPos) {
		case LEFT:
			break;
		case MIDDLE:
			break;
		}
	}

	public boolean onMySide(OwnedSide side) {
		if (side == OwnedSide.LEFT && startingPos == EStartingPosition.RIGHT) {
			return false;
		} else if (side == OwnedSide.RIGHT && startingPos == EStartingPosition.LEFT) {
			return false;
		}
		return true;
	}

	public boolean onMySideExchange() {
		if (startingPos == EStartingPosition.RIGHT) {
			return false;
		}
		return true;
	}

	public void parseEntries() {
		int posNum = posEntry.getNumber(new Integer(-1)).intValue();
		int crossNum = crossEntry.getNumber(new Integer(-1)).intValue();
		Integer[] defaultArray = { -1 };
		Number[] cubeArray = cubeActionPrefsEntry.getNumberArray(defaultArray);

		startingPos = EStartingPosition.intToEnum(posNum);
		crossType = ECross.intToEnum(crossNum);
		for (Number n : cubeArray) {
			cubeActionPrefs.add(ECubeAction.intToEnum(n.intValue()));
		}

	}

	public OwnedSide getSwitchData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
	}

	public OwnedSide getScaleData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SCALE);

	}

}
