package org.ilite.frc.common.types;

public enum ECross {
	CARPET,
	PLATFORM,
	NONE;
	
	public static ECross intToEnum(int num) {
		switch(num) {
		case 0:
			return CARPET;
		case 1:
			return PLATFORM;
		case 2:
			return NONE;
		}
		return null;
	}
}
