package org.ilite.frc.robot.modules;

import org.ilite.frc.common.config.SystemSettings;
import org.ilite.frc.common.types.EDriveTrain;
import org.ilite.frc.common.types.ELogitech310;
import org.ilite.frc.robot.Data;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

/**
 * Class for running all drive train control operations from both autonomous and
 * driver-control
 */
public class DriveTrain implements IModule {
  private final ILog mLog = Logger.createLog(DriveTrain.class);

	private Solenoid gearShifter;
	private final TalonSRX mLeftMaster;
	private final TalonSRX mLeftFollow;
  private final TalonSRX mRightMaster;
  private final TalonSRX mRightFollow;
  private final Data data;
  

	public DriveTrain(Data pData) {
	  data = pData;
		gearShifter = new Solenoid(SystemSettings.DRIVETRAIN_SHIFT_SOLENOID_ID);
		
		mLeftMaster = createTalon(SystemSettings.TALON_ADDR_DT_LEFT_MASTER, FeedbackDevice.QuadEncoder);
//		mLeftMaster.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 5);
		mLeftFollow = createTalon(SystemSettings.TALON_ADDR_DT_LEFT_FOLLOW_1, SystemSettings.TALON_ADDR_DT_LEFT_MASTER);
//		mLeftFollow.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 5);
    
    mRightMaster = createTalon(SystemSettings.TALON_ADDR_DT_RIGHT_MASTER, FeedbackDevice.QuadEncoder);
//    mRightMaster.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 5);
    mRightFollow = createTalon(SystemSettings.TALON_ADDR_DT_RIGHT_FOLLOW_1, SystemSettings.TALON_ADDR_DT_RIGHT_MASTER);
//    mRightFollow.setStatusFrameRateMs(StatusFrameRate.QuadEncoder, 5);
		
	}

	public void setShift(boolean shift) {
//		set(IS_SHIFT, shift ? 1d : null);
		set(EDriveTrain.VOLTAGE_RAMP_RATE, shift ? 
		    SystemSettings.DRIVETRAIN_HIGH_GEAR_RAMP_RATE : 
		    SystemSettings.DRIVETRAIN_DEFAULT_RAMP_RATE);
    
		// Set these outside the control loop so we don't waste clock cycles
//		mLeftMaster.setVoltageRampRate(get(VOLTAGE_RAMP_RATE));
//    mRightMaster.setVoltageRampRate(get(VOLTAGE_RAMP_RATE));
	}

	public void setPower(double left, double right) {
    set(EDriveTrain.DESIRED_LEFT_POWER, left);
    set(EDriveTrain.DESIRED_RIGHT_POWER, right);
	}

	public double getCurrentFeedback(){
	  return -1d;
//		return (mRightMaster.getOutputCurrent() + mLeftMaster.getOutputCurrent())/2;
	}

  @Override
  public void initialize(double pNow) {
//    mLeftMaster.setControlMode(TalonControlMode.PercentVbus.value);
//    mRightMaster.setControlMode(TalonControlMode.PercentVbus.value);
    set(EDriveTrain.VOLTAGE_RAMP_RATE, SystemSettings.DRIVETRAIN_DEFAULT_RAMP_RATE);
    setShift(false);
    resetEncoders();
  }
  
  public void resetEncoders() {
//    mLeftMaster.setEncPosition(0);
//    mLeftMaster.setPosition(0);
//    mRightMaster.setEncPosition(0);
//    mRightMaster.setPosition(0);
//    mLeftFollow.setEncPosition(0);
//    mLeftFollow.setPosition(0);
//    mRightFollow.setEncPosition(0);
//    mRightFollow.setPosition(0);
  }
  
  @Override
  public void update(double pNow) {
    data.drivetrain.meta().setTimestamp(pNow);
    set(EDriveTrain.TIME_SECONDS, Timer.getFPGATimestamp());
//    set(LEFT_POSITION_ROT, (double)mLeftMaster.getEncPosition());
//    set(RIGHT_POSITION_ROT,(double)mRightMaster.getEncPosition());
//    set(LEFT_VELOCITY_RPM, (double)mLeftMaster.getEncVelocity());
//    set(RIGHT_VELOCITY_RPM, (double)mRightMaster.getEncVelocity());
//    
//    set(LEFT_TALON_MASTER_CURRENT, mLeftMaster.getOutputCurrent());
//    set(LEFT_TALON_MASTER_VOLTAGE, mLeftMaster.getOutputVoltage());
//    set(LEFT_TALON_FOLLOW_CURRENT, mLeftFollow.getOutputCurrent());
//    set(LEFT_TALON_FOLLOW_VOLTAGE, mLeftFollow.getOutputVoltage());
//
//    set(RIGHT_TALON_MASTER_CURRENT, mRightMaster.getOutputCurrent());
//    set(RIGHT_TALON_MASTER_VOLTAGE, mRightMaster.getOutputVoltage());
//    set(RIGHT_TALON_FOLLOW_CURRENT, mRightFollow.getOutputCurrent());
//    set(RIGHT_TALON_FOLLOW_VOLTAGE, mRightFollow.getOutputVoltage());
    
    
//    set(TALON_VBUS, (mLeftMaster.getBusVoltage() + mRightMaster.getBusVoltage())/2);

//    mLeftMaster.set(get(DESIRED_LEFT_POWER) * -1);
//    mRightMaster.set(get(DESIRED_RIGHT_POWER));
//    gearShifter.set(data.drivetrain. isSet(IS_SHIFT));
    if(data.driverinput.isSet(ELogitech310.BACK)) {
      resetEncoders();
    }
  }

  @Override
  public void shutdown(double pNow) {
    // TODO Auto-generated method stub
    
  }
  
  private void set(EDriveTrain pEnum, double pValue) {
    data.drivetrain.set(pEnum, pValue);
  }
  
  private double get(EDriveTrain pEnum) {
    return data.drivetrain.get(pEnum);
  }

  
  private TalonSRX createTalon(int pAddress) {
    return createTalon(pAddress, null, null);
  }
  
  private TalonSRX createTalon(int pAddress, Integer pMasterAddr) {
    return createTalon(pAddress, pMasterAddr, null);
  }
  
  private TalonSRX createTalon(int pAddress, FeedbackDevice pFeedback) {
    return createTalon(pAddress, null, pFeedback);
  }
  
  private TalonSRX createTalon(int pAddress, Integer pMasterAddr, FeedbackDevice pFeedback) {
    TalonSRX result = new TalonSRX(pAddress);
    if(pMasterAddr != null) {
      mLog.warn("Setting " + pAddress + " to follow " + pMasterAddr);
//      result.setControlMode(TalonControlMode.Follower.value);
//      result.set(pMasterAddr);
    }
    if(pFeedback != null) {
//      result.setFeedbackDevice(pFeedback);
    }
    return result;
  }

}