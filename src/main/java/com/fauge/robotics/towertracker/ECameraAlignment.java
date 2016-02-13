package com.fauge.robotics.towertracker;

public enum ECameraAlignment {
	LEFT("Turn Left"),
	RIGHT("Turn Right"),
	CENTER("Centered");
	
	public String alignment;
	
	private ECameraAlignment(String align){
		alignment = align;
	}
	public String getAlignment(){
		return alignment;
	}
}
