package com.cwdarmm.model.dto;

import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.domain.CatMarketData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskInputDTO {
    private CatAccount account;
    private CatMarket market;
    private CatMarketData marketData;
    private BigDecimal accountSize;
    private double riskReward;
    private Integer ticksSl1;
    private Integer ticksSl2;
    private int stopLossSize;
    private boolean house;
    private boolean lunch;
    private boolean win;
    private boolean loss;
    private boolean firstTrade;
    private BigDecimal riskPctA;
    private BigDecimal riskPctB;
}
