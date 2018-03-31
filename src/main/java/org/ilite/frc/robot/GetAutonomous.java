package org.ilite.frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.OwnedSide;
import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.sensors.Pigeon;
import org.ilite.frc.common.types.ECross;
import org.ilite.frc.common.types.ECubeAction;
import org.ilite.frc.common.types.EStartingPosition;
import org.ilite.frc.robot.auto.AutoDimensions;
import org.ilite.frc.robot.commands.*;
import org.ilite.frc.robot.modules.*;
import org.ilite.frc.robot.modules.Carriage.CarriageState;

import java.util.*;
import java.util.stream.Collectors;

//Java8

public class GetAutonomous {
	// Network Table instance variables.
	private SimpleNetworkTable nAutonTable;
	private NetworkTableEntry nPosEntry;
	private NetworkTableEntry nCrossEntry;
	private NetworkTableEntry nCubeActionPrefsEntry;
	private NetworkTableEntry mDelayEntry;
	
	private Intake mIntake;
	private Elevator mElevator;
	private Carriage mCarriage;
	
	private DriveTrain mDriveTrain;
	private Pigeon mPigeon;
	private Data mData;

	// Decision variables to be set by networktable entries.
	private List<ECubeAction> mReceivedCubeActionPrefs, mSameSideCubeActionPrefs, mOtherSideCubeActionPrefs, mAvailableCubeActions;
	private EStartingPosition mStartingPos = EStartingPosition.LEFT;
	private ECross mCrossType = ECross.NONE;
	private double mDelay;
	private boolean mDoComplexAutonomous;

	// Game Data - Jaci's API
	private OwnedSide mScaleSide;
	private OwnedSide mSwitchSide;
	
  private Queue<ICommand> mCommands;

	// Used for turning. Starting on left side = 1, starting on right side = -1;
	// Unknown or middle = 0
	private int mTurnScalar = 0;
	
	private long triggeredTime = 0;

	/**
	 * 
	 * @param pAutonTable
	 *            - Autonomous network table to be passed in from Robot.java
	 */
	public GetAutonomous(SimpleNetworkTable pAutonTable, Intake pIntake, Elevator pElevator, Carriage pCarriage, Pigeon pPigeon, DriveTrain pDriveTrain, Data pData) {
	  this.mIntake = pIntake;
	  this.mElevator = pElevator;
	  this.mCarriage = pCarriage;
	  this.mPigeon = pPigeon;
	  this.mDriveTrain = pDriveTrain;
	  this.mData = pData;
	  
		this.nAutonTable = pAutonTable;
		this.mData = pData;
		
		mDoComplexAutonomous = true;
		mReceivedCubeActionPrefs = new ArrayList<>();
    mSameSideCubeActionPrefs = new ArrayList<>();
    mOtherSideCubeActionPrefs = new ArrayList<>();
    mAvailableCubeActions = new ArrayList<>();
		mCommands = new LinkedList<ICommand>();
	}

	/**
	 * "Main" method that returns a Command Queue to robot Java.
	 * 
	 * @return Command Queue of autonomous commands.
	 */
	public Queue<ICommand> getAutonomousCommands() {
	  System.out.println("STARTING");
	  mCommands.clear();
	  getSides();
//    parseEntries();
    double timerStart = System.currentTimeMillis();
    while(System.currentTimeMillis() < timerStart + 1) {
      getSides();
    }
	  boolean received = parseEntries();
		mSameSideCubeActionPrefs = getCubeActionsOnMySide();
		mOtherSideCubeActionPrefs = getCubeActionsOnOtherSide();
		mAvailableCubeActions = getAvailableCubeActions();
		System.out.println("RECEIVED: " + mReceivedCubeActionPrefs);
		System.out.println("SAME SIDE: " + mSameSideCubeActionPrefs);
		System.out.println("OTHER SIDE: " + mOtherSideCubeActionPrefs);
		System.out.println("AVAILABLE:" + mAvailableCubeActions);
		
		if(!mAvailableCubeActions.isEmpty()) {
		  ECubeAction prefAction = mAvailableCubeActions.get(0);
		  
		  switch(prefAction) {
		  case SCALE:
		    if(mSameSideCubeActionPrefs.contains(ECubeAction.SCALE)) {
		      doScale();
		    } else if(mOtherSideCubeActionPrefs.contains(ECubeAction.SCALE)) {
		      doOppositeScale();
		    }
		    break;
		  case SWITCH:
		    if(mSameSideCubeActionPrefs.contains(ECubeAction.SWITCH)) {
          doSwitch();
        } else if(mOtherSideCubeActionPrefs.contains(ECubeAction.SWITCH)) {
          doOppositeSwitch();
        }
		    break;
		  case EXCHANGE:
		    if(mSameSideCubeActionPrefs.contains(ECubeAction.EXCHANGE)) {
          doExchange();
        } 
		    break;
		  case NONE:
		    break;
		  }
		}
		
//		if (!mSameSideCubeActionPrefs.isEmpty()) {
//			ECubeAction prefAction = mSameSideCubeActionPrefs.get(0);// Does most preferred driver selection.
////			System.out.println("=================== Autonomous chose: " + prefAction.toString());
////			if(mDelay > 15) {
////				mDelay = 15; //Cannot delay the autonomus for over 15 seconds.
////			}
////			mCommands.add(new Delay(mDelay)); //Delays autonomous with the given value from network table.
////			nAutonTable.putString("Chosen Autonomous", String.format("Position: %s Cross: %s Cube Action: %s",
////					mStartingPos, mCrossType, mSameSideCubeActionPrefs.get(0)));
//			switch (prefAction) {
//			case SCALE:
//				doScale();
//				break;
//			case SWITCH:
//				doSwitch();
//				break;
//			case EXCHANGE:
//				doExchange();
//				break;
//			case NONE:
//				crossAutoLine();
//				break;
//			}
//		} else if(!mOtherSideCubeActionPrefs.isEmpty() && mCrossType != ECross.NONE){
//		  ECubeAction prefAction = mOtherSideCubeActionPrefs.get(0);// Does most preferred driver selection.
////    System.out.println("=================== Autonomous chose: " + prefAction.toString());
//      if(mDelay > 15) {
//        mDelay = 15; //Cannot delay the autonomus for over 15 seconds.
//      }
//  //    mCommands.add(new Delay(mDelay)); //Delays autonomous with the given value from network table.
//  //    nAutonTable.putString("Chosen Autonomous", String.format("Position: %s Cross: %s Cube Action: %s",
//  //        mStartingPos, mCrossType, mSameSideCubeActionPrefs.get(0)));
//      switch (prefAction) {
//      case SCALE:
//        doOppositeScale();
//        break;
//      case SWITCH:
//        doOppositeSwitch();
//        break;
//      case NONE:
//        crossAutoLine();
//        break;
//      }
//		}
		
		if(mCommands.isEmpty()) {
		  System.out.println("====================== COMMAND QUEUE IS EMPTY - CROSSING AUTO LINE");
		  crossAutoLine();
		}
		System.out.println("ENDING");
		return mCommands;

	}

	/**
	 * Do scale autonomous; switch based on starting position.
	 */ 
	public void doScale() {
	  double scaleTurnDegrees = 55d;
		// TODO replace with turning scalar
		System.out.printf("Doing scale autonomous starting on %s\n", mStartingPos);
		switch (mStartingPos) {
		case LEFT:
		case RIGHT:
		  // Drive 
		  mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SCALE_TO_NULL_ZONE, 0.2));
      
      // Turn into scale, drive forward, kick
		  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * scaleTurnDegrees, 8));
      mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.THIRD_TAPE, 1.5));
      mCommands.add(new DriveStraight(mDriveTrain, mData, 6, 0.4, true));
      mCommands.add(new Delay(0.5));
		  mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
		  
//		  //Back up from scale
		  mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SCALE_BACK_UP, 0.6));
//		  
//		  // Reset elevator
      mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.FIRST_TAPE, 0.5d));
//      
//      // Turn back for a 2nd cube
//      mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * 180-scaleTurnDegrees, 2));
//      mCommands.add(new DriveStraight(mDriveTrain, mData, 36d));
//      mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * -45, 2));
////		  mCommands.add(new IntakeCube(mIntake, mCarriage, 0.7, 5, true));
			break;
		case MIDDLE:
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
		case RIGHT:
			mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SWITCH_CROSS_LINE, 0.2));
			mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * 90, 3));
      mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.SECOND_TAPE, 1));
			mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SWITCH_TO_SWITCH, 0.4, true));
			mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
			mCommands.add(new Delay(2));
			mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SWITCH_BACK_UP, 0.4, true));
			mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.FIRST_TAPE, 1d));
//			mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * -90, 3));
//			mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SWITCH_TO_NULL_ZONE));
			break;
		case MIDDLE:
			switch(mSwitchSide) {
			case LEFT:
				//26 inches is how far we need to move off the alliance station wall without worrying about hitting it
			  mCommands.add(new DriveStraight(mDriveTrain, mData, 26.0, 0.4));
			  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * -41, 3));
			  //98 inches is the distance to the left of the switch
			  mCommands.add(new DriveStraight(mDriveTrain, mData, 98.0, 0.2));
			  //turn back to be aligned square with the switch
			  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * 41, 3));
			  //0.1 set to timeout just to make sure that it gets past its initial spike
			  mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.SECOND_TAPE, 0.1));
			  //distance is actually 40 inches, but we want to make sure that we actually hit the switch
			  mCommands.add(new DriveStraight(mDriveTrain, mData, 40 + 1, 0.4));
			  mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
			  mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.BOTTOM, 0.1));
			  //move back same distance that we drive to the switch
			  mCommands.add(new DriveStraight(mDriveTrain, mData, -40, 0.4));
			  //67 degrees is about what we need to line up with a cube in the cube stack (highly subject to change)
			  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, -67, 3));
			  //64 inches is about the distance needed to drive into the cube in the cube stack. Probably an overshoot
			  mCommands.add(new DriveStraight(mDriveTrain, mData, 64, 0.4));
			  //intake the cube
			  mCommands.add(new IntakeCube(mIntake, mCarriage, 0.5, 0.1, true));
			  //Drive backwards the same distance as it took to get to the cube, while over compensating for an inch
			  mCommands.add(new DriveStraight(mDriveTrain, mData, -65, 0.4));
			  //Turn amount of degrees to become realigned with switch
			  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, 67, 3));
			  //Try and physically hit the switch
			  mCommands.add(new DriveStraight(mDriveTrain, mData, 40 + 1));
			  mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
			  mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.BOTTOM, 0.1));
			  
			  
			  
			  break;
			case RIGHT:
				//26 inches is how far we need to move off the alliance station wall without worrying about hitting it
				  mCommands.add(new DriveStraight(mDriveTrain, mData, 26.0, 0.4));
				  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * -36, 3));
				  //92 inches is the distance to the left of the switch
				  mCommands.add(new DriveStraight(mDriveTrain, mData, 92.0, 0.2));
				  //turn back to be aligned square with the switch
				  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * 36, 3));
				  //0.1 set to timeout just to make sure that it gets past its initial spike
				  mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.SECOND_TAPE, 0.1));
				  //distance is actually 40 inches, but we want to make sure that we actually hit the switch
				  mCommands.add(new DriveStraight(mDriveTrain, mData, 40 + 1, 0.4));
				  mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
				  mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.BOTTOM, 0.1));
				  //move back same distance that we drive to the switch
				  mCommands.add(new DriveStraight(mDriveTrain, mData, -40, 0.4));
				  //67 degrees is about what we need to line up with a cube in the cube stack (highly subject to change)
				  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, -67, 3));
				  //64 inches is about the distance needed to drive into the cube in the cube stack. Probably an overshoot
				  mCommands.add(new DriveStraight(mDriveTrain, mData, 64, 0.4));
				  //intake the cube
				  mCommands.add(new IntakeCube(mIntake, mCarriage, 0.5, 0.1, true));
				  //Drive backwards the same distance as it took to get to the cube, while over compensating for an inch
				  mCommands.add(new DriveStraight(mDriveTrain, mData, -65, 0.4));
				  //Turn amount of degrees to become realigned with switch
				  mCommands.add(new GyroTurn(mDriveTrain, mPigeon, 67, 3));
				  //Try and physically hit the switch
				  mCommands.add(new DriveStraight(mDriveTrain, mData, 40 + 1));
				  mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
				  mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.BOTTOM, 0.1));
			  break;
			}
			break;
		}
	}
	
	/**
   * Do scale autonomous; switch based on starting position.
   */ 
  public void doOppositeScale() {
    // TODO replace with turning scalar
    System.out.printf("Doing opposite scale autonomous starting on %s\n", mStartingPos);
    switch (mStartingPos) {
    case LEFT:
    case RIGHT:
      mCommands.add(new DriveStraight(mDriveTrain, mData, 16 * 12));
      mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * 90d, 5));
      mCommands.add(new DriveStraight(mDriveTrain, mData, 15.5 * 12));
      mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * -90d, 5));
//      mCommands.add(new DriveStraight(mDriveTrain, mData, Utils.feetToInches(0.5d)));
      mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.THIRD_TAPE, 3));
      mCommands.add(new DriveStraight(mDriveTrain, mData, 9 , 0.2));
      mCommands.add(new Delay(0.1));
      mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 1));
      mCommands.add(new DriveStraight(mDriveTrain, mData, -12));
      mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.FIRST_TAPE, 4));
      break;
    case MIDDLE:
      break;
    }
  }

  /**
   * Do switch autonomous; switch based on starting position.
   */
  public void doOppositeSwitch() {
    // TODO replace with turning scalar
    System.out.printf("Doing opposite switch autonomous starting on %s\n", mStartingPos);
    switch (mStartingPos) {
    case LEFT:
    case RIGHT:
//      mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SWITCH_CROSS_LINE));
//      mCommands.add(new ElevatorToPosition(mElevator, EElevatorPosition.SECOND_TAPE, 3));
//      mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * 90, 3));
//      mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SWITCH_TO_SWITCH));
//      mCommands.add(new ReleaseCube(mCarriage, CarriageState.KICKING, 3));
//      mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SWITCH_BACK_UP));
//      mCommands.add(new GyroTurn(mDriveTrain, mPigeon, mTurnScalar * -90, 3));
//      mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.SAME_SIDE_SWITCH_TO_NULL_ZONE));
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
			break;
			
		 case MIDDLE:
			break;
			
	   case RIGHT: 
	     break;
		}
	}

	/**
	 * Crosses autonomous line based on starting position.
	 */
	public void crossAutoLine() {
		// TODO replace with turning scalar
		System.out.printf("Doing auto line autonomous starting on %s\n", mStartingPos);
		mCommands.add(new DriveStraight(mDriveTrain, mData, AutoDimensions.AUTO_LINE_CROSS));
    mCommands.add(new GyroTurn(mDriveTrain, mPigeon, 180, 10));
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
	
	public boolean isOnOtherSide(OwnedSide side) {
    switch (side) {
    case LEFT:
      return mStartingPos == EStartingPosition.RIGHT;
    case RIGHT:
      return mStartingPos == EStartingPosition.LEFT;
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
	private boolean parseEntries() {
		int posNum = nPosEntry.getNumber(EStartingPosition.LEFT.ordinal()).intValue();
		int crossNum = nCrossEntry.getNumber(ECross.CARPET.ordinal()).intValue();
		Number[] cubeArray = nCubeActionPrefsEntry.getNumberArray(SystemSettings.AUTO_DEFAULT_CUBE_ACTIONS);
		boolean received = false;
		mDelay = mDelayEntry.getDouble(-1);
		mStartingPos = EStartingPosition.intToEnum(posNum);
		mCrossType = ECross.intToEnum(crossNum);
		mReceivedCubeActionPrefs = new ArrayList<>();
		mSameSideCubeActionPrefs = new ArrayList<>();
		mOtherSideCubeActionPrefs = new ArrayList<>();
		mAvailableCubeActions = new ArrayList<>();

		for (Number n : cubeArray) {
			if (n.intValue() == -1)
				continue;
			mReceivedCubeActionPrefs.add(ECubeAction.intToEnum(n.intValue()));

		}
		
		if(mReceivedCubeActionPrefs.isEmpty()) {
		  received = false;
		  Arrays.asList(SystemSettings.AUTO_DEFAULT_CUBE_ACTIONS).forEach(e -> mReceivedCubeActionPrefs.add(ECubeAction.intToEnum(e)));
		} else {
		  received = true;
		}

//    if(mStartingPos != EStartingPosition.LEFT) mStartingPos = EStartingPosition.LEFT;
		
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
		
		return received;
	}

	/**
	 * Game data method. Returns the owned side of the switch at beginning of match.
	 * 
	 * @return - OwnedSide.LEFT or OwnedSide.RIGHT
	 */
	public OwnedSide getSwitchOwnedSide() {
		return MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
	}

	/**
	 * Game data method. Returns the owned side of the scale at beginning of match.
	 * 
	 * @return - OwnedSide.LEFT or OwnedSide.RIGHT
	 */
	public OwnedSide getScaleOwnedSide() {
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
	  System.out.printf("Cube Action: %s Scale Side: %s Switch Side: %s\n", c, mScaleSide, mSwitchSide);
		switch (c) {
		case EXCHANGE:
			return isExchangeOnMySide();
		case SCALE:
		  // Edge case not covered in isOnMySide - We obviously can't do scale in the middle
		  if(mStartingPos == EStartingPosition.MIDDLE) {
		    return false;
		  }
			return isOnMySide(mScaleSide);
		case SWITCH:
			return isOnMySide(mSwitchSide);
		case NONE:
		default:
			return true;
		}
	}

	/**
	 * Utilizes the previous two mySide methods.
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
		// Edge case not covered in isOnMySide - We obviously can't do scale in the middle
    if(mStartingPos == EStartingPosition.MIDDLE) {
      return false;
    }
		return isOnOtherSide(mScaleSide);
		case SWITCH:
		return isOnOtherSide(mSwitchSide);
		case NONE:
		default:
			return true;
		}
	}
	
	/**
	 * 
	 * @param c
	 * @return Whether we can perform a certain cube action
	 */
	public boolean isCubeActionAvailable(ECubeAction c) {
	  switch(c) {
	  case EXCHANGE:
	    // We can't do anything on the opposite side
	    return isCubeActionOnMySide(c);
    case SCALE:
      // We can cross in order to reach the opposite scale, so make it available as long as we are on left or right
      // We have to check cross type here - otherwise we won't maintain the correct order of priorities
      return isCubeActionOnMySide(c) || (isCubeActionOtherSide(c) && mCrossType != ECross.NONE);
    case SWITCH:
      System.out.println("Switch auto availability: " + isCubeActionOnMySide(c));
      // We can only do same-side switch
      return isCubeActionOnMySide(c);
    case NONE:
      // We can always do nothing
      return true;
    default:
	    return false;
	  }
	}
	
	/* package */ List<ECubeAction> getAvailableCubeActions() {
    return mReceivedCubeActionPrefs.stream().filter(cA -> isCubeActionAvailable(cA)).collect(Collectors.toList());
  }

	/* package */ List<ECubeAction> getCubeActionsOnMySide() {
		return mReceivedCubeActionPrefs.stream().filter(cA -> isCubeActionOnMySide(cA)).collect(Collectors.toList());
	}

	/* package */ List<ECubeAction> getCubeActionsOnOtherSide() {
		return mReceivedCubeActionPrefs.stream().filter(cA -> isCubeActionOtherSide(cA)).collect(Collectors.toList());
	}
	
	private void getSides() {
	   try {
	      nPosEntry = nAutonTable.getEntry(EStartingPosition.class.getSimpleName());
	      nCrossEntry = nAutonTable.getEntry(ECross.class.getSimpleName());
	      nCubeActionPrefsEntry = nAutonTable.getEntry(ECubeAction.class.getSimpleName());
	      mDelayEntry = nAutonTable.getEntry(SystemSettings.AUTO_DELAY_KEY);
	    } catch (Exception e) {
	       System.err.println("Error retrieving data entries from auton display");
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
		mReceivedCubeActionPrefs = pActions;
		mCrossType = pCross;
		mStartingPos = pPos;
		mSwitchSide = pSwitchSide;
		mScaleSide = pScaleSide;
	}
	
	public void testReceiveSideData(OwnedSide pSwitchSide, OwnedSide pScaleSide) {
	  mSwitchSide = pSwitchSide;
    mScaleSide = pScaleSide;
	}

}
