package org.ilite.frc.robot.modules;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.MotorSafety;

public class TalonFactory {
	
	
	public static class Configuration {
        public double MAX_OUTPUT_VOLTAGE = 12;
        public double NOMINAL_VOLTAGE = 0;
        public double PEAK_VOLTAGE = 12;
        public double NOMINAL_CLOSED_LOOP_VOLTAGE = 12;
        public boolean SAFETY_ENABLED = false;
        public int CONTROL_FRAME_PERIOD_MS = 5;
        public int MOTION_CONTROL_FRAME_PERIOD_MS = 100;
        public int GENERAL_STATUS_FRAME_RATE_MS = 5;
        public int FEEDBACK_STATUS_FRAME_RATE_MS = 100;
        public int QUAD_ENCODER_STATUS_FRAME_RATE_MS = 100;
        public int ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS = 100;
        public int PULSE_WIDTH_STATUS_FRAME_RATE_MS = 100;
        public double VOLTAGE_COMPENSATION_RAMP_RATE = 0;
        public double VOLTAGE_RAMP_RATE = 0;
        public double EXPIRATION_TIMEOUT_SECONDS = MotorSafety.DEFAULT_SAFETY_EXPIRATION;
        
    }
	
	private static final Configuration kDefaultConfig = new Configuration();
	
	public static TalonSRX createDefault(int id) {
        return createTalon(id, kDefaultConfig);
    }
	
	public static TalonSRX createTalon(int id, Configuration config)
	{
		TalonSRX talon = new TalonSRX(id);
		talon.set(ControlMode.PercentOutput, 0);
		return talon;
		
	}
	
	

}
