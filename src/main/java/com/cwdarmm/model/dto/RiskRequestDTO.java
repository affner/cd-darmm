package com.cwdarmm.model.dto;

import lombok.Builder;

@Builder
public record RiskRequestDTO(
        String marketId,
        double accountBalance,
        double stopTicks,
        double tickValue,
        double riskPercentBase,   // % base elegido en wizard
        boolean lastTradeWin      // true si fue win (para DARMM)
) { }
