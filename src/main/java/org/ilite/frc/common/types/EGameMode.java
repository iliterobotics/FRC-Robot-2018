package org.ilite.frc.common.types;

public enum EGameMode {

    DISABLED_INIT, TEST_INIT, AUTONOMOUS_INIT, TELEOP_INIT,
    DISABLED_PERIODIC, TEST_PERIODIC, AUTONOMOUS_PERIODIC, TELEOP_PERIODIC;

    public static EGameMode intToEnum(int num) {
        if(num == -1) return DISABLED_PERIODIC;
        if(num >= values().length || num < 0) return DISABLED_PERIODIC;
        return values()[num];
    }

}
