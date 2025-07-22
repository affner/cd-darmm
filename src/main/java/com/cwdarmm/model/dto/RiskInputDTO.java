package com.cwdarmm.model.dto;

import com.cwdarmm.model.domain.TradingAccount;
import com.cwdarmm.model.domain.PriceFeed;
import com.cwdarmm.model.domain.OpenMarket;
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
    private TradingAccount account;
    private OpenMarket market;
    private PriceFeed marketData;
    private BigDecimal accountSize;
    private double riskReward;
    private int ticksSl1;
    private int ticksSl2;
    private int stopLossSize;
    private boolean house;
    private boolean lunch;
    private boolean win;
    private boolean loss;
}
