package com.cwdarmm.model.dto;

import com.cwdarmm.model.domain.AccountDefinition;
import com.cwdarmm.model.domain.FeedDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RiskResultDTO {
    private int tradeNumber;
    private String wl;
    private AccountDefinition account;
    private FeedDefinition marketData;
    private BigDecimal accountSize;
    private double riskKellyA;
    private double riskKellyB;
}
