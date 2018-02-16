package org.ilite.frc.robot.modules;

public class SuperStructure implements IModule {
	private Intake intake;
	private ElevatorModule elevator;
	private Carriage carriage;
	
	private boolean CARRIAGE_IS_OPEN;
	private boolean INTAKE_HAS_CUBE;
	private boolean INTAKE_EXTENDED;
	private boolean IS_CLIMBING;
	private boolean BOTTOM_POSITION;
	private boolean SWITCH_POSITION;
	private boolean SCALE_POSITION;
	private boolean CLIMB_READY;
	private boolean GEAR_SHIFTED;
	private boolean IS_INTAKE_JAMMED;
	
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
		INTAKE_HAS_CUBE = carriage.beamBreakBroken();
		CARRIAGE_IS_OPEN = carriage.carriageOpens();
		INTAKE_EXTENDED = intake.isPneumaticsOut();
		IS_CLIMBING = elevator.getDirection();
		BOTTOM_POSITION = elevator.isDown();
		SWITCH_POSITION = elevator.getPosition() == elevator.ElevatorPosition.SWITCH;
		SCALE_POSITION = elevator.getPosition() == elevator.ElevatorPosition.SCALE;
		GEAR_SHIFTED = elevator.getGearState();
		IS_INTAKE_JAMMED = intake.getJammed();
		return false;
	}
	
	public boolean readyToIntake(){
		return(CARRIAGE_IS_OPEN && BOTTOM_POSITION && !INTAKE_HAS_CUBE && INTAKE_EXTENDED);
	}
	
	public boolean carriageReady(){
		return INTAKE_HAS_CUBE;
	}
	
	public boolean readyToClimb(){
		return (GEAR_SHIFTED && SCALE_POSITION);
	}
	
	public boolean haveCube()
	{
		return INTAKE_HAS_CUBE;
	}
	
	public boolean elevatorDown()
	{
		return
	}
}
