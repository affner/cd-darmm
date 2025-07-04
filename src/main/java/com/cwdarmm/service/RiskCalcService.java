package com.cwdarmm.service;

import com.cwdarmm.model.ConfidenceTier;
import com.cwdarmm.model.dto.RiskRequestDTO;
import com.cwdarmm.model.domain.RiskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiskCalcService {

    // parámetros DARMM (podrían ir a properties)
    private static final double DARMM_UP   = 1.05;  // +5 % si win
    private static final double DARMM_DOWN = 0.98;  // −2 % si loss
    private static final double CONF_HIGH  = 0.13;  // +0.13 % bonus
    private static final double CONF_MED   = 0.065; // +0.065 %

    private double currentRiskPct = 0.0;   // muta semana a semana

    /**
     * Calcula contratos óptimos para un trade dado.
     */
    public RiskResult calc(RiskRequestDTO req, int tradeNum, ConfidenceTier tier) {

        // 1) actualiza % riesgo base con DARMM compounding
        if (tradeNum == 1) currentRiskPct = req.riskPercentBase();   // primer trade
        else
            currentRiskPct *= req.lastTradeWin() ? DARMM_UP : DARMM_DOWN;

        // 2) añade capa de confianza si aplica
        double confBonus = switch (tier) {
            case HIGH   -> CONF_HIGH;
            case MEDIUM -> CONF_MED;
            default     -> 0.0;
        };
        double adjustedRiskPct = currentRiskPct + confBonus;

        // 3) contratos = (Balance × %R) / (stop × tickValue)
        double riskUsd     = req.accountBalance() * adjustedRiskPct / 100.0;
        double contractVal = req.stopTicks() * req.tickValue();
        double contracts   = Math.max(1, Math.floor(riskUsd / contractVal));

        // 4) construye resultado para la tabla azul
        return RiskResult.builder()
                .tradeNumber(tradeNum)
                .contracts(contracts)
                .riskUsd(riskUsd)
                .capitalUsed(contracts * contractVal)
                .rrRatio((req.tickValue() * req.stopTicks()) / riskUsd)
                .darmmRiskPercent(adjustedRiskPct)
                .build();
    }

    /** reinicia % riesgo los lunes */
    public void resetWeekly(double base) {
        currentRiskPct = base;
    }

}
