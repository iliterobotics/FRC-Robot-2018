package org.ilite.frc.common.input;

import org.ilite.frc.common.types.ECross;

public enum EDriverControlMode {
	ARCADE,
	SPLIT_ARCADE;
	public static EDriverControlMode intToEnum(int num) {
		if(num <= -1)return ARCADE;
		return values()[num];
	}
}
