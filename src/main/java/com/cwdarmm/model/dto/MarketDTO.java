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
public class MarketDTO {

    private Long id;
    private TradingAccount account;
    private OpenMarket market;
    private PriceFeed marketData;
    private BigDecimal accountSize;
    private double riskA;
    private double riskB;
    private double riskFinalHouse;
    private double riskFinalLunch;

}
