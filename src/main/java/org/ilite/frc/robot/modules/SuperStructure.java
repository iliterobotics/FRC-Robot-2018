package org.ilite.frc.robot.modules;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

public class SuperStructure implements IModule {
	private Intake intake;
	private ElevatorModule elevator;
	private Carriage carriage;
	

	public enum Flags implements CodexOf<Boolean>{
		CARRIAGE_IS_OPEN,
		INTAKE_HAS_CUBE,
		BOTTOM_POSITION,
		SWITCH_POSITION,
		INTAKE_EXTENDED,
		IS_CLIMBING,
		SCALE_POSITION,
		CLIMB_READY,
		GEAR_SHIFTED,
		IS_INTAKE_JAMMED;
	}
	
	private final Codex<Boolean, Flags> mFlags = new Codex<>(Flags.class);
	
	public SuperStructure(ElevatorModule e, Carriage c, Intake i){
		elevator = e;
		carriage = c;
		intake = i;
		
	}

	@Override
	public void shutdown(double pNow) {
		
		
	}

	@Override
	public void initialize(double pNow) {
		
		
	}

	
	//all elevator methods are implemented in most current ElevatorModule
	@Override
	public boolean update(double pNow) {
		/*
		Flags.INTAKE_HAS_CUBE = true;//carriage.beamBreakBroken();
		Flags.CARRIAGE_IS_OPEN = true;//carriage.carriageOpens();
		Flags.INTAKE_EXTENDED = true;//intake.isPneumaticsOut();
		Flags.IS_CLIMBING = true;//elevator.getDirection();
		Flags.BOTTOM_POSITION = true;//elevator.isDown();
		Flags.SWITCH_POSITION = true;//elevator.getPosition() == elevator.ElevatorPosition.SWITCH;
		Flags.SCALE_POSITION = true;//elevator.getPosition() == elevator.ElevatorPosition.SCALE;
		Flags.GEAR_SHIFTED = true;//elevator.getGearState();
		Flags.IS_INTAKE_JAMMED = intake.getJammed();
		*/
		return false;
	}
	
	public boolean readyToIntake(){
		return(carriage.carriageOpen() && elevator.isDown() &&
				!carriage.beamBreakBroken() && intake.isPneumaticsOut());
	}
	
	public boolean carriageReady(){
		return (carriage.beamBreakBroken());
	}
	
	public boolean readyToClimb(){
		return (elevator.getGearState() && elevator.getPosition() == elevator.ElevatorPosition.SCALE);
	}
	
	public boolean haveCube()
	{
		return carriage.beamBreakBroken();
	}
	
	public boolean elevatorDown()
	{
		return elevator.isDown();
	}
}
