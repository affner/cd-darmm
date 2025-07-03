package com.cwdarmm.model.domain;

import lombok.Builder;

@Builder
public record RiskResult(
        int tradeNumber,
        double contracts,
        double riskUsd,
        double capitalUsed,
        double rrRatio,
        double darmmRiskPercent   // % real tras compounding
) { }
