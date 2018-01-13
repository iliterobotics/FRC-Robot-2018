package org.ilite.frc.common.types;

public enum EStartingPosition {
	LEFT,
	RIGHT,
	MIDDLE;
  
  public String toString() {
    switch(this) {
    case LEFT: return "Left";
    case RIGHT: return "Right";
    case MIDDLE:
    default: return "Middle";
    }
  }
	
}
