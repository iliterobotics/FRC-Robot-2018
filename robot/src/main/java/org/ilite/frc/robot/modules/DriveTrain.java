package org.ilite.frc.robot.modules;

import static org.ilite.frc.robot.types.EDriveTrain.DESIRED_LEFT_POWER;
import static org.ilite.frc.robot.types.EDriveTrain.DESIRED_RIGHT_POWER;
import static org.ilite.frc.robot.types.EDriveTrain.IS_SHIFT;
import static org.ilite.frc.robot.types.EDriveTrain.LEFT_POSITION;
import static org.ilite.frc.robot.types.EDriveTrain.LEFT_VELOCITY;
import static org.ilite.frc.robot.types.EDriveTrain.RIGHT_POSITION;
import static org.ilite.frc.robot.types.EDriveTrain.RIGHT_VELOCITY;
import static org.ilite.frc.robot.types.EDriveTrain.VOLTAGE_RAMP_RATE;

import java.util.ArrayList;
import java.util.List;

import org.ilite.frc.robot.Data;
import org.ilite.frc.robot.config.SystemSettings;
import org.ilite.frc.robot.types.EDriveTrain;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;
import com.flybotix.hfr.util.log.ILog;
import com.flybotix.hfr.util.log.Logger;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Class for running all drive train control operations from both autonomous and
 * driver-control
 */
public class DriveTrain implements IModule {
  private final ILog mLog = Logger.createLog(DriveTrain.class);

	private Solenoid gearShifter;
	private final CANTalon mLeftMaster;
  private final CANTalon mRightMaster;
  private final Data data;
  
  private final List<CANTalon> otherTalons = new ArrayList<>();

	public DriveTrain(Data pData) {
	  data = pData;
		gearShifter = new Solenoid(SystemSettings.DRIVETRAIN_SHIFT_SOLENOID_ID);
		
		mLeftMaster = createTalon(SystemSettings.TALON_ADDR_DT_LEFT_MASTER, FeedbackDevice.QuadEncoder);
		otherTalons.add(createTalon(SystemSettings.TALON_ADDR_DT_LEFT_FOLLOW_1, SystemSettings.TALON_ADDR_DT_LEFT_MASTER));
    
    mRightMaster = createTalon(SystemSettings.TALON_ADDR_DT_RIGHT_MASTER, FeedbackDevice.QuadEncoder);
    otherTalons.add(createTalon(SystemSettings.TALON_ADDR_DT_RIGHT_FOLLOW_1, SystemSettings.TALON_ADDR_DT_RIGHT_MASTER));
		
	}

	public void setShift(boolean shift) {
		set(IS_SHIFT, shift ? 1d : null);
		set(VOLTAGE_RAMP_RATE, shift ? 
		    SystemSettings.DRIVETRAIN_HIGH_GEAR_RAMP_RATE : 
		    SystemSettings.DRIVETRAIN_DEFAULT_RAMP_RATE);
    
		// Set these outside the control loop so we don't waste clock cycles
		mLeftMaster.setVoltageRampRate(get(VOLTAGE_RAMP_RATE));
    mRightMaster.setVoltageRampRate(get(VOLTAGE_RAMP_RATE));
	}

	public void setPower(double left, double right) {
    set(DESIRED_LEFT_POWER, left);
    set(DESIRED_RIGHT_POWER, right);
	}

	public double getCurrentFeedback(){
		return (mRightMaster.getOutputCurrent() + mLeftMaster.getOutputCurrent())/2;
	}

  @Override
  public void initialize(double pNow) {
    mLeftMaster.setControlMode(TalonControlMode.PercentVbus.value);
    mRightMaster.setControlMode(TalonControlMode.PercentVbus.value);
    set(VOLTAGE_RAMP_RATE, SystemSettings.DRIVETRAIN_DEFAULT_RAMP_RATE);
    setShift(false);
  }

  @Override
  public void update(double pNow) {
    set(LEFT_POSITION, (double)mLeftMaster.getEncPosition());
    set(RIGHT_POSITION,(double)mRightMaster.getEncPosition());
    set(LEFT_VELOCITY, (double)mLeftMaster.getEncVelocity());
    set(RIGHT_VELOCITY, (double)mRightMaster.getEncVelocity());

    mLeftMaster.set(get(DESIRED_LEFT_POWER) * -1);
    mRightMaster.set(get(DESIRED_RIGHT_POWER));
    gearShifter.set(data.drivetrain. isSet(IS_SHIFT));
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

  
  private CANTalon createTalon(int pAddress) {
    return createTalon(pAddress, null, null);
  }
  
  private CANTalon createTalon(int pAddress, Integer pMasterAddr) {
    return createTalon(pAddress, pMasterAddr, null);
  }
  
  private CANTalon createTalon(int pAddress, FeedbackDevice pFeedback) {
    return createTalon(pAddress, null, pFeedback);
  }
  
  private CANTalon createTalon(int pAddress, Integer pMasterAddr, FeedbackDevice pFeedback) {
    CANTalon result = new CANTalon(pAddress);
    if(pMasterAddr != null) {
      mLog.warn("Setting " + pAddress + " to follow " + pMasterAddr);
      result.setControlMode(TalonControlMode.Follower.value);
      result.set(pMasterAddr);
    }
    if(pFeedback != null) {
      result.setFeedbackDevice(pFeedback);
    }
    return result;
  }

}