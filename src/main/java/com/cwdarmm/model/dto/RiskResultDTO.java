package com.cwdarmm.model.dto;

import com.cwdarmm.model.domain.TradingAccount;
import com.cwdarmm.model.domain.PriceFeed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RiskResultDTO {
    private int tradeNumber;
    private String wl;
    private TradingAccount account;
    private PriceFeed marketData;
    private BigDecimal accountSize;
    private Double riskKellyA;
    private Double riskKellyB;
}
