package org.ilite.frc.common.types;

public enum EStartingPosition {
	LEFT,
	RIGHT,
	MIDDLE,
	UNKNOWN;
	
	public static EStartingPosition intToEnum(int num) {
		return values()[num];
	}
}
