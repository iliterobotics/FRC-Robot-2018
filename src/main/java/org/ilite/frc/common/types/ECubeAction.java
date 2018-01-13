package org.ilite.frc.common.types;

public enum ECubeAction {
	SCALE,
	SWITCH,
	EXCHANGE,
	NOTHING;
  
  public String toString() {
    switch(this) {
    case SCALE: return "Place on Scale";
    case SWITCH: return "Place on Switch";
    case EXCHANGE: return "Place into Exchange";
    case NOTHING:
    default: return "Do Nothing";
    }
  }
}
