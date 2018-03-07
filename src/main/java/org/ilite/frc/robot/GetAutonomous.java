package org.ilite.frc.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
//Java8
import java.util.stream.Collectors;

import org.ilite.frc.common.sensors.Pigeon;
import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.robot.auto.FieldAdapter;
import org.ilite.frc.robot.commands.Delay;
import org.ilite.frc.robot.commands.DriveStraight;
import org.ilite.frc.robot.commands.ElevatorToPosition;
import org.ilite.frc.robot.commands.GyroTurn;
import org.ilite.frc.robot.commands.ICommand;
import org.ilite.frc.robot.commands.ParallelCommand;
import org.ilite.frc.robot.commands.ReleaseCube;
import org.ilite.frc.robot.modules.Carriage;
import org.ilite.frc.robot.modules.Carriage.CarriageState;
import org.ilite.frc.robot.modules.DriveTrain;
import org.ilite.frc.robot.modules.Elevator;
import org.ilite.frc.robot.modules.Elevator.ElevatorPosition;

import edu.wpi.first.networktables.NetworkTableEntry;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.OwnedSide;

public class GetAutonomous {
	// Network Table instance variables.
	private SimpleNetworkTable nAutonTable;
	private NetworkTableEntry nPosEntry;
	private NetworkTableEntry nCrossEntry;
	private NetworkTableEntry nCubeActionPrefsEntry;
	private NetworkTableEntry nDelayEntry;
	
	private FieldAdapter mField;
	
	private Elevator mElevator;
	private Carriage mCarriage;
	
	private DriveTrain mDriveTrain;
	private Pigeon mPigeon;
	private Data mData;

	// Decision variables to be set by networktable entries.
	private List<ECubeAction> mCubeActionPrefs;
	private EStartingPosition mStartingPos;
	private ECross mCrossType;
	private double mDelay;
	private boolean doComplexAutonomous;

	// Game Data - Jaci's API
	private OwnedSide mScaleSide;
	private OwnedSide mSwitchSide;
	
  private Queue<ICommand> mCommands;

	// Used for turning. Starting on left side = 1, starting on right side = -1;
	// Unknown or middle = 0
	private int mTurnScalar = 0;

	/**
	 * 
	 * @param pAutonTable
	 *            - Autonomous network table to be passed in from Robot.java
	 */
	public GetAutonomous(SimpleNetworkTable pAutonTable, Elevator pElevator, Carriage pCarriage, Pigeon pPigeon, DriveTrain pDriveTrain, Data pData) {
	  this.mElevator = pElevator;
	  this.mCarriage = pCarriage;
	  this.mPigeon = pPigeon;
	  this.mDriveTrain = pDriveTrain;
	  this.mData = pData;
	  
	  this.mField = new FieldAdapter();
	  
		this.nAutonTable = pAutonTable;
		nAutonTable.initKeys();
		doComplexAutonomous = true;
		
		mCommands = new LinkedList<ICommand>();
	}

	/**
	 * "Main" method that returns a Command Queue to robot Java.
	 * 
	 * @return Command Queue of autonomous commands.
	 */
	public Queue<ICommand> getAutonomousCommands() {
	  getSides();
		parseEntries();
		
		mCubeActionPrefs = getCubeActionsOnMySide();
		
		if (!mCubeActionPrefs.isEmpty()) {
			ECubeAction prefAction = mCubeActionPrefs.get(0);// Does most preferred driver selection.
			System.out.println("Autonomous chose: " + prefAction.toString());
			if(mDelay > 15) {
				mDelay = 15; //Cannot delay the autonomus for over 15 seconds.
			}
			mCommands.add(new Delay(mDelay)); //Delays autonomous with the given value from network table.
			nAutonTable.putString("Chosen Autonomous", String.format("Position: %s Cross: %s Cube Action: %s",
					mStartingPos, mCrossType, mCubeActionPrefs.get(0)));
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
		
		if(mCommands.isEmpty()) crossAutoLine();

		return mCommands;

	}

	/**
	 * Do scale autonomous; switch based on starting position.
	 */ 
	public void doScale() {
		// TODO replace with turning scalar
		System.out.printf("Doing scale autonomous starting on %s\n", mStartingPos);
		switch (mStartingPos) {
		case LEFT:
		  mCommands.add(new ParallelCommand( new DriveStraight(mDriveTrain, mData, Utils.feetToInches(mField.getLeftScaleX() - mField.getLeftStartingPosX())),
		                                     new ElevatorToPosition(mElevator, ElevatorPosition.THIRD_TAPE, 4)));
		  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, 45, 3));
		  mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
			break;
		case MIDDLE:
			break;
		case RIGHT:
		  mCommands.add(new ParallelCommand( new DriveStraight(mDriveTrain, mData, Utils.feetToInches(mField.getRightScaleX() - mField.getRightStartingPosX())),
          new ElevatorToPosition(mElevator, ElevatorPosition.THIRD_TAPE, 4)));
      mCommands.add(new GyroTurn(mDriveTrain, mPigeon, -45, 3));
      mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
			break;
		}
	}

	/**
	 * Do switch autonomous; switch based on starting position.
	 */
	public void doSwitch() {
		// TODO replace with turning scalar
		System.out.printf("Doing switch autonomous starting on %s\n", mStartingPos);
		switch (mStartingPos) {
		case LEFT:
			mCommands.add(new ParallelCommand( new DriveStraight(mDriveTrain, mData, Utils.feetToInches(mField.getLeftSideSwitchX() - mField.getLeftStartingPosX())),
			                                   new ElevatorToPosition(mElevator, ElevatorPosition.FIRST_TAPE, 3)));
			mCommands.add(new GyroTurn(mDriveTrain, mPigeon, 90, 3));
			mCommands.add(new DriveStraight(mDriveTrain, mData, Utils.feetToInches(mField.getLeftStartingPosY() - mField.getLeftSideSwitchY())));
			mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
			break;
		case MIDDLE:
			switch(mSwitchSide) {
			case LEFT:
			  // TODO
			  break;
			case RIGHT:
			  mCommands.add(new ParallelCommand( new DriveStraight(mDriveTrain, mData, Utils.feetToInches(mField.getRightFrontSwitchX() - mField.getMiddleStartingPosX())),
			                                     new ElevatorToPosition(mElevator, ElevatorPosition.FIRST_TAPE, 3)));
			  mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
			  break;
			}
			break;
		case RIGHT:
		  mCommands.add(new ParallelCommand( new DriveStraight(mDriveTrain, mData, Utils.feetToInches(mField.getRightSideSwitchX() - mField.getRightStartingPosX())),
      new ElevatorToPosition(mElevator, ElevatorPosition.FIRST_TAPE, 3)));
      mCommands.add(new GyroTurn(mDriveTrain, mPigeon, -90, 3));
      mCommands.add(new DriveStraight(mDriveTrain, mData, Utils.feetToInches(mField.getRightSideSwitchY() - mField.getRightStartingPosY())));
      mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
			break;
		}
	}

	/**
	 * Place cube in exchange autonomous; switch based on starting position.
	 */
	@SuppressWarnings("all")
	public void doExchange() {
		// TODO replace with turning scalar
		System.out.printf("Doing exchange autonomous starting on %s\n", mStartingPos);
		switch (mStartingPos) {
		case LEFT:
			switch(mCrossType) {
			case NONE: break;
			case CARPET: break;
			case PLATFORM: break;
			}
			break;
		case MIDDLE:
			switch(mCrossType) {
			case NONE: break;
			case CARPET: break;
			case PLATFORM: break;
			}
			break;
		}
	}

	/**
	 * Crosses autonomous line based on starting position.
	 */
	public void crossAutoLine() {
		// TODO replace with turning scalar
		System.out.printf("Doing auto line autonomous starting on %s\n", mStartingPos);
		mCommands.add(new DriveStraight(mDriveTrain, mData, Utils.feetToInches(mField.getRightFrontSwitchX() - mField.getRightStartingPosX())));
	}

	/**
	 * Determines whether or not our starting position corresponds to the
	 * pre-configured owned side.
	 * 
	 * @param side
	 *            - Corresponding side received from match data.
	 * @return - Whether or not the starting position matches the owned side from
	 *         match data.
	 */
	public boolean isOnMySide(OwnedSide side) {
		switch (side) {
		case LEFT:
			return mStartingPos == EStartingPosition.LEFT || mStartingPos == EStartingPosition.MIDDLE;
		case RIGHT:
			return mStartingPos == EStartingPosition.RIGHT || mStartingPos == EStartingPosition.MIDDLE;
		case UNKNOWN:
		default:
			return false;
		}
	}

	/**
	 * Determines whether or not our starting position corresponds to the exchange.
	 * 
	 * @return - Whether or not our starting position is viable to place a cube into
	 *         the exchange.
	 */
	public boolean isExchangeOnMySide() {
		return mStartingPos == EStartingPosition.MIDDLE || mStartingPos == EStartingPosition.LEFT;
	}

	/**
	 * Parses the network table entries and stores the values into the decision
	 * variables. Converts the number representation of the network table entry into
	 * an enum.
	 */
	private void parseEntries() {
		int posNum = nPosEntry.getNumber(EStartingPosition.UNKNOWN.ordinal()).intValue();
		int crossNum = nCrossEntry.getNumber(ECross.NONE.ordinal()).intValue();
		Integer[] defaultArray = { ECubeAction.NONE.ordinal() };
		Number[] cubeArray = nCubeActionPrefsEntry.getNumberArray(defaultArray);

		mDelay = nDelayEntry.getDouble(-1);
		mStartingPos = EStartingPosition.intToEnum(posNum);
		mCrossType = ECross.intToEnum(crossNum);
		mCubeActionPrefs = new ArrayList<ECubeAction>();
		System.out.println(Arrays.toString(nCubeActionPrefsEntry.getNumberArray(defaultArray)));
		for (Number n : cubeArray) {
			if (n.intValue() == -1)
				continue;
			mCubeActionPrefs.add(ECubeAction.intToEnum(n.intValue()));

		}
		switch (mStartingPos) {
		case LEFT:
			mTurnScalar = 1;
			break;

		case RIGHT:
			mTurnScalar = -1;
			break;

		case MIDDLE:
		case UNKNOWN:
		default:
			mTurnScalar = 0;
			break;
		}
	}

	/**
	 * Game data method. Returns the owned side of the switch at beginning of match.
	 * 
	 * @return - OwnedSide.LEFT or OwnedSide.RIGHT
	 */
	public OwnedSide getSwitchOwnedSide() {
		System.out.println(MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR));
		return MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
	}

	/**
	 * Game data method. Returns the owned side of the scale at beginning of match.
	 * 
	 * @return - OwnedSide.LEFT or OwnedSide.RIGHT
	 */
	public OwnedSide getScaleOwnedSide() {
		System.out.println(MatchData.getOwnedSide(MatchData.GameFeature.SCALE));
		return MatchData.getOwnedSide(MatchData.GameFeature.SCALE);
	}

	/**
	 * Utilizes the previous to mySide methods.
	 * 
	 * @param c
	 *            - Type of cube action, SCALE, SWITCH, EXCHANGE.
	 * @return - Whether or not our starting position is viable to perform the given
	 *         cube action.
	 */
	public boolean isCubeActionOnMySide(ECubeAction c) {
		switch (c) {
		case EXCHANGE:
			return isExchangeOnMySide();
		case SCALE:
			return isOnMySide(mScaleSide);
		case SWITCH:
			return isOnMySide(mSwitchSide);
		case NONE:
		default:
			return true;
		}
	}

	/**
	 * Utilizes the previous to mySide methods.
	 * 
	 * @param c
	 *            - Type of cube action, SCALE, SWITCH, EXCHANGE.
	 * @return - Whether or not our starting position is viable to perform the given
	 *         cube action.
	 */
	public boolean isCubeActionOtherSide(ECubeAction c) {
		switch (c) {
		case EXCHANGE:
			return false;
		case SCALE:
			return !isOnMySide(mScaleSide);
		case SWITCH:
			return !isOnMySide(mSwitchSide);
		case NONE:
		default:
			return true;
		}
	}

	/* package */ List<ECubeAction> getCubeActionsOnMySide() {
		return mCubeActionPrefs.stream().filter(cA -> isCubeActionOnMySide(cA)).collect(Collectors.toList());
	}

	/* package */ List<ECubeAction> getCubeActionsOnOtherSide() {
		return mCubeActionPrefs.stream().filter(cA -> isCubeActionOtherSide(cA)).collect(Collectors.toList());
	}
	
	private void getSides() {
	   try {
	      nPosEntry = nAutonTable.getEntry(EStartingPosition.class.getSimpleName());
	      nCrossEntry = nAutonTable.getEntry(ECross.class.getSimpleName());
	      nCubeActionPrefsEntry = nAutonTable.getEntry(ECubeAction.class.getSimpleName());
	      nDelayEntry = nAutonTable.getEntry("Delay");
	    } catch (Exception e) {

	    }
	    mScaleSide = getScaleOwnedSide();
	    mSwitchSide = getSwitchOwnedSide();
	}

	/**
	 * Testing method.
	 * 
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
