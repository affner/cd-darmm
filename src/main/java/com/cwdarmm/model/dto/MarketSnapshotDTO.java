package com.cwdarmm.model.dto;

import lombok.Builder;

@Builder
public record MarketSnapshotDTO(
        String marketId,
        int    weekNumber,
        double equity,
        double totalRiskUsd,
        double totalContracts
) { }
