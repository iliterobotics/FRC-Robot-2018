package org.ilite.frc.robot.modules;


import org.ilite.frc.common.config.SystemSettings;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
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
        public int PEAK_CURRENT_LIMTI = 35;
        public int CONFIG_TIMEOUT_MS = SystemSettings.TALON_CONFIG_TIMEOUT_MS;
        
    }
	
	private static final Configuration kDefaultConfig = new Configuration();
	
	public static TalonSRX createDefault(int id) {
        return createTalon(id, kDefaultConfig);
    }
	
	public static TalonSRX createTalon(int id, Configuration config)
	{
		TalonSRX talon = new TalonSRX(id);
		talon.set(ControlMode.PercentOutput, 0);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, config.QUAD_ENCODER_STATUS_FRAME_RATE_MS, config.CONFIG_TIMEOUT_MS);
		talon.configOpenloopRamp(config.VOLTAGE_RAMP_RATE, config.CONFIG_TIMEOUT_MS);
		talon.configPeakCurrentLimit(config.PEAK_CURRENT_LIMTI, config.CONFIG_TIMEOUT_MS);
		return talon;
		
	}
	
	

}
