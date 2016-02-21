package com.fauge.robotics.towertracker;

public enum ECameraAlignment {
	LEFT("Turn Left", -1),
	RIGHT("Turn Right", 1),
	CENTER("Centered", 0);
	
	public String alignment;
	public int multiplier;
	
	private ECameraAlignment(String align, int multiply){
		alignment = align;
		multiplier = multiply;
	}
	public String getAlignment(){
		return alignment;
	}
	
	public int getMultiplier(){
	    return multiplier;
	}
}
