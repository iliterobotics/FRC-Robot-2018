package org.ilite.frc.common.types;

public enum ECross {
	CARPET,
	PLATFORM,
	NONE;
  
  public String toString() {
    switch(this) {
      case CARPET: return "Carpet";
      case PLATFORM: return "Platform";
      case NONE:
      default: return "Not Allowed";
    }
  }
}
