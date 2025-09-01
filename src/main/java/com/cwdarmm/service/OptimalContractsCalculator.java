package com.cwdarmm.service;

import com.cwdarmm.model.domain.BdMarket;
import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.repository.BdMarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculadora de contratos óptimos independiente de la capa de presentación.
 * Cada invocación genera una tabla nueva con los valores para un target
 * específico, replicando las fórmulas del libro Excel.
 */
@Service
@RequiredArgsConstructor
public class OptimalContractsCalculator {

    private final BdMarketRepository bdMarketRepository;

    /**
     * Genera las filas de contratos óptimos para el target indicado.
     *
     * @param accountId    cuenta seleccionada
     * @param marketId     mercado
     * @param marketDataId feed de mercado
     * @param accountSize  saldo de la cuenta
     * @param riskPct      porcentaje de riesgo aplicado
     * @param slTicks      tamaño del stop loss en ticks
     * @param targetTicks  objetivo en ticks
     */
    public List<OptimalContractRow> calculate(Long accountId,
                                              Long marketId,
                                              Long marketDataId,
                                              double accountSize,
                                              double riskPct,
                                              int slTicks,
                                              int targetTicks) {
        List<BdMarket> rows = bdMarketRepository.findOneByMktAccMdata(marketId, accountId, marketDataId);
        List<OptimalContractRow> result = new ArrayList<>();
        if (rows.isEmpty()) {
            return result;
        }
        for (BdMarket bd : rows) {
            double riskUsd = accountSize * (riskPct / 100.0);
            double riskPerContract = slTicks * bd.getTickValue() + bd.getCommission();
            double contracts = riskPerContract <= 0 ? 0 : Math.floor(riskUsd / riskPerContract);
            if (contracts < 1) {
                result.add(new OptimalContractRow(slTicks, "The risk is too high", null, targetTicks));
            } else {
                BigDecimal opt = BigDecimal.valueOf(contracts);
                result.add(new OptimalContractRow(slTicks, bd.getSymbol().getSymbol(), opt, targetTicks));
            }
        }
        return result;
    }
}
