package com.cwdarmm.util;

import org.apache.commons.math3.distribution.BetaDistribution;

/** Helpers estáticos para Beta distrib / intervalos. */
public final class BetaUtils {

    private BetaUtils() {}

    /** devuelve ancho del intervalo de confianza al 95 % */
    public static double intervalWidth(int wins, int losses) {
        BetaDistribution beta = new BetaDistribution(wins + 1, losses + 1);
        return beta.inverseCumulativeProbability(0.975)
                - beta.inverseCumulativeProbability(0.025);
    }
}
