package org.ilite.frc.common.types;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;
import org.ilite.frc.robot.modules.Carriage;

public enum ECarriage implements CodexOf<Double> {

    CURRENT_STATE, HEIGHT, CLOSED;

    public static void map( Codex<Double, ECarriage> pCodex, Carriage mCarriage ) {
        pCodex.reset();
    }

}


