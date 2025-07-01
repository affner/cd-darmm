package com.cwdarmm.service;

import com.cwdarmm.model.*;
import org.springframework.stereotype.Service;

@Service
public class KellySizer implements RiskEngine {

    @Override
    public OptimalContract calculate(Market m,
                                     TradingAccount acc,
                                     RiskProfile profile,
                                     int stopTicks) {

        double riskFraction = profile.getKellyA() / 100.0; // iniciar con A
        double riskUsd      = acc.getCurrentSize() * riskFraction;
        double usdPerTick   = m.getTickValue();
        int    contractQty  = (int) Math.floor(riskUsd / (stopTicks * usdPerTick));

        return new OptimalContract(stopTicks, contractQty, riskUsd);
    }
}
