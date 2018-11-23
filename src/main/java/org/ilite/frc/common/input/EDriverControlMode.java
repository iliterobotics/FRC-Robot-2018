package org.ilite.frc.common.input;


public enum EDriverControlMode {
	ARCADE,
	SPLIT_ARCADE;
	public static EDriverControlMode intToEnum(int num) {
		if(num <= -1)return ARCADE;
		return values()[num];
	}
}
