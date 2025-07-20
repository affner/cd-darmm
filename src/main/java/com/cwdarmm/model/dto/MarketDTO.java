package com.cwdarmm.model.dto;

import com.cwdarmm.model.domain.AccountDefinition;
import com.cwdarmm.model.domain.FeedDefinition;
import com.cwdarmm.model.domain.MarketMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketDTO {
    private AccountDefinition account;
    private MarketMaster market;
    private FeedDefinition marketData;
    private double accountSize;
    private double riskA;
    private double riskB;
    private double riskFinalHouse;
    private double riskFinalLunch;

}
