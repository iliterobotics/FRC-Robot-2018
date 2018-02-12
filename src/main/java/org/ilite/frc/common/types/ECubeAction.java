package org.ilite.frc.common.types;

public enum ECubeAction {
	SCALE,
	SWITCH,
	EXCHANGE,
	NONE;
	
	public static ECubeAction intToEnum(int num) {
		if(num == -1) return NONE;
		return values()[num];
	}
	
}
