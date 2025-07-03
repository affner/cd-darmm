package com.cwdarmm.event;

import com.cwdarmm.model.domain.RiskResult;

public record RiskCalculatedEvent(String marketId, RiskResult result) { }
