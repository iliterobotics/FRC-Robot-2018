package org.ilite.frc.robot;

import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

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


//TODO: Remove JOptionPanes, swing import, and testing methods.

public class GetAutonomous implements ICommand {
	//Network Table instance variables.
	private NetworkTable nAutonTable;
	private NetworkTableEntry nPosEntry;
	private NetworkTableEntry nCrossEntry;
	private NetworkTableEntry nCubeActionPrefsEntry;
	
	
	private Queue<ICommand> mCommands;//Commands queue to be returned by getAutonomous(...)
	
	//Decision variables to be set by networktable entries.
	private List<ECubeAction> mCubeActionPrefs;
	private EStartingPosition mStartingPos;
	private ECross mCrossType;
	private boolean doComplexAutonomous;
	
	//Game Data - Jaci's API
	private OwnedSide mScaleSide;
	private OwnedSide mSwitchSide;
	
	/**
	 * 
	 * @param pAutonTable - Autonomous network table to be passed in from Robot.java
	 */
	public GetAutonomous(NetworkTable pAutonTable) {
		this.nAutonTable = pAutonTable;
		doComplexAutonomous = true;
	}
	
	@Override
	public void initialize() {
		update();
		parseEntries();
	}
	
	/**
	 * Updates the decision matrix variables from NetworkTable entries.
	 */
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
	
	@Override
	public void shutdown() {
		
	}
	
	/**
	 * "Main" method that returns a Command Queue to robot Java.
	 * @return Command Queue of autonomous commands.
	 */
	public Queue<ICommand> getAutonomous() {
		mCommands = new LinkedList<ICommand>();
		mCommands.clear();
		
		mCubeActionPrefs = mCubeActionPrefs
				.stream()
				.filter(cA -> mySide(cA))
				.collect(Collectors.toList());
		
		JOptionPane.showMessageDialog(null, "Cube Action Prefs List:" + mCubeActionPrefs);
		
		if (doComplexAutonomous) {

			if (!mCubeActionPrefs.isEmpty()) {
				ECubeAction prefAction = mCubeActionPrefs.get(0);//Does most preferred driver selection.
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
				crossAutoLine();//Default
			}
			
		} else {
			// Drive forward > minimum necessary autonomous for ranking point.
		}

		return mCommands;

	}
	/**
	 * Do scale autonomous; switch based on starting position.
	 */
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
	
	/**
	 * Do switch autonomous; switch based on starting position.
	 */
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
	
	/**
	 * Place cube in exchange autonomous; switch based on starting position.
	 */
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
	
	/**
	 * Determines whether or not our starting position corresponds to the pre-configured owned side.
	 * @param side - Corresponding side received from match data.
	 * @return - Whether or not the starting position matches the owned side from match data.
	 */
	public boolean onMySide(OwnedSide side) {
		if (side == OwnedSide.LEFT && mStartingPos == EStartingPosition.RIGHT) {
			return false;
		} else if (side == OwnedSide.RIGHT && mStartingPos == EStartingPosition.LEFT) {
			return false;
		}
		return true;
	}
	/**
	 * Determines whether or not our starting position corresponds to the exchange.
	 * @return - Whether or not our starting position is viable to place a cube into the exchange.
	 */
	public boolean onMySideExchange() {
		if (mStartingPos == EStartingPosition.RIGHT) {
			return false;
		}
		return true;
	}
	
	/**
	 * Parses the network table entries and stores the values into the decision variables.
	 * Converts the number representation of the network table entry into an enum.
	 */
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
	
	/**
	 * Game data method. Returns the owned side of the switch at beginning of match.
	 * @return - OwnedSide.LEFT or OwnedSide.RIGHT
	 */
	public OwnedSide getSwitchData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
	}
	
	/**
	 * Game data method. Returns the owned side of the scale at beginning of match.
	 * @return - OwnedSide.LEFT or OwnedSide.RIGHT
	 */
	public OwnedSide getScaleData() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SCALE);

	}
	
	/**
	 * Utilizes the previous to mySide methods.
	 * @param c - Type of cube action, SCALE, SWITCH, EXCHANGE.
	 * @return - Whether or not our starting position is viable to perform the given cube action.
	 */
	public boolean mySide(ECubeAction c) {
		if (c == ECubeAction.EXCHANGE) {
			return onMySideExchange();
		} else if (c == ECubeAction.SWITCH) {
			return onMySide(mSwitchSide);
		}
		return onMySide(mScaleSide);
	}
	
	/**
	 * Crosses autonomous line based on starting position.
	 */
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
	
	/**
	 * Testing method.
	 * @param pActions
	 * @param pCross
	 * @param pPos
	 * @param pSwitchSide
	 * @param pScaleSide
	 */
	public void testReceiveData(List<ECubeAction> pActions, ECross pCross, EStartingPosition pPos,
		OwnedSide pSwitchSide, OwnedSide pScaleSide) {
		mCubeActionPrefs = pActions;
		mCrossType = pCross;
		mStartingPos = pPos;
		mSwitchSide = pSwitchSide;
		mScaleSide = pScaleSide;
	}

}
