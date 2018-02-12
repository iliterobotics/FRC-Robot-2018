package org.ilite.frc.common.types;

public enum ECross {
	NONE,
	CARPET,
	PLATFORM;
	
	public static ECross intToEnum(int num) {
		if(num == -1)return NONE;
		return values()[num];
	}
}
