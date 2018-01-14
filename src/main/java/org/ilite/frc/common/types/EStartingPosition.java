package org.ilite.frc.common.types;

public enum EStartingPosition {
	LEFT,
	RIGHT,
	MIDDLE;
	
	public static EStartingPosition intToEnum(int num) {
		switch(num) {
		case 0:
			return LEFT;
		case 1:
			return RIGHT;
		case 2:
			return MIDDLE;
		}
		return null;
	}
}
