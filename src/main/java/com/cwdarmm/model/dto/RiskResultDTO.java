package com.cwdarmm.model.dto;

import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.model.domain.CatMarketData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RiskResultDTO {
    private int tradeNumber;
    private String wl;
    private CatAccount account;
    private CatMarketData marketData;
    private BigDecimal accountSize;
    private Double riskKellyA;
    private Double riskKellyB;
}
