package org.ilite.frc.common.types;

import com.flybotix.hfr.codex.Codex;
import com.flybotix.hfr.codex.CodexOf;

public enum EGlobalValues implements CodexOf<Double> {

    GAME_MODE;

    public static void map(Codex<Double, EGlobalValues> pCodex, EGameMode pCurrentGameMode) {
        pCodex.set(GAME_MODE, (double)pCurrentGameMode.ordinal());
    }

}
