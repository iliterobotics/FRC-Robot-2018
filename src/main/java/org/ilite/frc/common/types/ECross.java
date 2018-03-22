package org.ilite.frc.common.types;

public enum ECross {
	CARPET,
	PLATFORM,
	NONE;
	
	public static ECross intToEnum(int num) {
		if(num == -1)return NONE;
		if(num >= values().length || num < 0) return NONE;
		return values()[num];
	}
}
