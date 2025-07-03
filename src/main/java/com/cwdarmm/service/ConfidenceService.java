package com.cwdarmm.service;

import org.springframework.stereotype.Service;

@Service
public class ConfidenceService {

    public double bonusRiskPct(int wins, int losses) {
        var beta = new org.apache.commons.math3.distribution.BetaDistribution(
                wins + 1, losses + 1);
        double lb = beta.inverseCumulativeProbability(0.025);
        double ub = beta.inverseCumulativeProbability(0.975);
        double ciIdx = 1 - (wins + losses) * (ub - lb) / 2;   // :contentReference[oaicite:2]{index=2}

        if (ciIdx > 0.90)   return 0.0013;   // +0.13 %
        if (ciIdx > 0.80)   return 0.00065;  // +0.065 %
        return 0.0;
    }
}
