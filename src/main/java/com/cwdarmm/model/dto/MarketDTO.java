package com.cwdarmm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketDTO {
    private String account;
    private String market;
    private String marketData;
    private double accountSize;
    private double riskA;
    private double riskB;
    private double riskFinalHouse;
    private double riskFinalLunch;

}
