package com.cwdarmm.service;

import com.cwdarmm.model.*;

public interface RiskEngine {
    OptimalContract calculate(Market m, TradingAccount acc,
                              RiskProfile profile, int stopTicks);
}
