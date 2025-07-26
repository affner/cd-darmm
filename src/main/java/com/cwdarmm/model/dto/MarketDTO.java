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
public class MarketDTO {

    private Long id;
    private CatAccount account;
    private CatMarket market;
    private CatMarketData marketData;
    private BigDecimal accountSize;
    private double riskA;
    private double riskB;
    private double riskFinalHouse;
    private double riskFinalLunch;

}
