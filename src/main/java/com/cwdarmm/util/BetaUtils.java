package com.cwdarmm.util;

import org.apache.commons.math3.distribution.BetaDistribution;

public final class BetaUtils {

    private BetaUtils(){}

    public static double ciLower(int wins, int losses, double alpha) {
        return new BetaDistribution(wins + 1, losses + 1)
                .inverseCumulativeProbability(alpha / 2);
    }

    public static double ciUpper(int wins, int losses, double alpha) {
        return new BetaDistribution(wins + 1, losses + 1)
                .inverseCumulativeProbability(1 - alpha / 2);
    }
}
