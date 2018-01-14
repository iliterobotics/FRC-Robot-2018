package org.ilite.frc.common.types;

public enum ECubeAction {
	SCALE,
	SWITCH,
	EXCHANGE,
	NONE;
	
	public static ECubeAction intToEnum(int num) {
		switch(num) {
		case 0:
			return SCALE;
		case 1:
			return SWITCH;
		case 2:
			return EXCHANGE;
		case 3:
			return NONE;
		}
		return null;
	}
	
}
