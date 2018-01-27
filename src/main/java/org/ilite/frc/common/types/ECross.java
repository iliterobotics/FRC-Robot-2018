package org.ilite.frc.common.types;

public enum ECross {
	CARPET,
	PLATFORM,
	NONE;
	
	public static ECross intToEnum(int num) {
		return values()[num];
	}
}
