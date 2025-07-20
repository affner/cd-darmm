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
public class RiskInputDTO {
    private AccountDefinition account;
    private MarketMaster market;
    private FeedDefinition marketData;
    private double accountSize;
    private double riskReward;
    private int ticksSl1;
    private int ticksSl2;
    private int stopLossSize;
    private boolean house;
    private boolean lunch;
    private boolean win;
    private boolean loss;
}
