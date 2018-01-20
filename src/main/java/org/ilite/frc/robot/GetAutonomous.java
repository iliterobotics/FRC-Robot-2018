package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.commands.ICommand;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.OwnedSide;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.common.types.ECubeAction;

import javax.swing.JOptionPane;

//Java8
import java.util.stream.Collectors;

public class GetAutonomous implements ICommand {
	private NetworkTable nAutonTable;
	private NetworkTableEntry nPosEntry;
	private NetworkTableEntry nCrossEntry;
	private NetworkTableEntry nCubeActionPrefsEntry;

	private List<ICommand> mCommands;
	private List<ECubeAction> mCubeActionPrefs;
	private EStartingPosition mStartingPos;
	private ECross mCrossType;
	private boolean doComplexAutonomous;
	private OwnedSide mScaleSide;
	private OwnedSide mSwitchSide;

	public GetAutonomous(NetworkTable pAutonTable) {
		this.nAutonTable = pAutonTable;
		doComplexAutonomous = true;
	}

	@Override
	public void initialize() {
		update();
		parseEntries();
	}

	@Override
	public boolean update() {
		try {
			nPosEntry = nAutonTable.getEntry("position");
			nCrossEntry = nAutonTable.getEntry("cross");
			nCubeActionPrefsEntry = nAutonTable.getEntry("cubeActionList");
		} catch (Exception e) {

		}
		return true;
	}

	public List<ICommand> getAutonomous(DriveTrain pDriveTrain) {
		mCommands = new ArrayList<ICommand>();
		mCommands.clear();
		
		mCubeActionPrefs = mCubeActionPrefs
				.stream()
				.filter(cA -> mySide(cA))
				.collect(Collectors.toList());
		JOptionPane.showMessageDialog(null, "Cube Action Prefs List:" + mCubeActionPrefs);
		if (doComplexAutonomous) {

			if (!mCubeActionPrefs.isEmpty()) {
				ECubeAction prefAction = mCubeActionPrefs.get(0);
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
				case NONE:
					crossAutoLine();
					break;
				}
			}
			else {
				crossAutoLine();
			}
			
		} else {
			// Drive forward > minimum necessary autonomous for ranking point.
		}

		return mCommands;

	}

	public void doScale() {
		JOptionPane.showMessageDialog(null, "Scale was chosen");
		switch (mStartingPos) {
		case LEFT:
			break;
		case MIDDLE:
			break;
		case RIGHT:
			break;
		}
	}

	public void doSwitch() {
		JOptionPane.showMessageDialog(null, "Switch was chosen");
		switch (mStartingPos) {
		case LEFT:
			break;
		case MIDDLE:
			break;
		case RIGHT:
			break;
		}
	}
	
	@SuppressWarnings("all")
	public void doExchange() {
		JOptionPane.showMessageDialog(null, "Exchange was chosen");
		switch (mStartingPos) {	
		case LEFT:
			break;
		case MIDDLE:
			break;
		}
	}

	public boolean onMySide(OwnedSide side) {
		if (side == OwnedSide.LEFT && mStartingPos == EStartingPosition.RIGHT) {
			return false;
		} else if (side == OwnedSide.RIGHT && mStartingPos == EStartingPosition.LEFT) {
			return false;
		}
		return true;
	}

	public boolean onMySideExchange() {
		if (mStartingPos == EStartingPosition.RIGHT) {
			return false;
		}
		return true;
	}

	public void parseEntries() {
		int posNum = nPosEntry.getNumber(new Integer(-1)).intValue();
		int crossNum = nCrossEntry.getNumber(new Integer(-1)).intValue();
		Integer[] defaultArray = { -1 };
		Number[] cubeArray = nCubeActionPrefsEntry.getNumberArray(defaultArray);

		mStartingPos = EStartingPosition.intToEnum(posNum);
		mCrossType = ECross.intToEnum(crossNum);
		for (Number n : cubeArray) {
			mCubeActionPrefs.add(ECubeAction.intToEnum(n.intValue()));
		}

	}

	public OwnedSide getSwitchData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
	}

	public OwnedSide getScaleData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SCALE);

	}

	public boolean mySide(ECubeAction c) {
		if (c == ECubeAction.EXCHANGE) {
			return onMySideExchange();
		} else if (c == ECubeAction.SWITCH) {
			return onMySide(mSwitchSide);
		}
		return onMySide(mScaleSide);
	}
	
	public void crossAutoLine() {
		switch(mStartingPos) {
		case LEFT:
			break;
		case MIDDLE:
			break;
		case RIGHT:
			break;
		}
	}

	public void testReceiveData(List<ECubeAction> pActions, ECross pCross, EStartingPosition pPos,
			OwnedSide pSwitchSide, OwnedSide pScaleSide) {
		mCubeActionPrefs = pActions;
		mCrossType = pCross;
		mStartingPos = pPos;
		mSwitchSide = pSwitchSide;
		mScaleSide = pScaleSide;
	}

}
