package com.cwdarmm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RiskResultDTO {
    private int tradeNumber;
    private String wl;
    private String account;
    private String marketData;
    private double accountSize;
    private double riskKellyA;
    private double riskKellyB;
}
