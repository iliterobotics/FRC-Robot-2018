package org.ilite.frc.common.types;

public enum EStartingPosition {
    LEFT,
    RIGHT,
    MIDDLE,
	UNKNOWN;
  public static EStartingPosition intToEnum(int num) {
    if(num == -1)return UNKNOWN;
    if(num >= values().length || num < 0) return UNKNOWN;
    return values()[num];
  }
}
