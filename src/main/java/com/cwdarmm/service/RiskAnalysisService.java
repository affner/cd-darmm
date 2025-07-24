// RiskAnalysisService.java
package com.cwdarmm.service;

import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskAnalysisService {


    /**
     * Ejecuta la simulación de trades y calcula métricas básicas.
     */
    public List<RiskResultDTO> calculate(RiskInputDTO in) {
        List<RiskResultDTO> rows = new ArrayList<>();

        // a) INITIAL (solo si el usuario dijo que sí es el primer trade)
        if (in.isFirstTrade()) {
            rows.add(RiskResultDTO.builder()
                    .tradeNumber(0)
                    .wl("INITIAL")
                    .account(in.getAccount())
                    .marketData(in.getMarketData())
                    .accountSize(in.getAccountSize())
                    .riskKellyA(in.getTicksSl1().doubleValue())
                    .riskKellyB(in.getTicksSl2().doubleValue())
                    .build()
            );
        }

        // b) Cálculo de “Optimal Contracts”
        int tradeNum = in.isFirstTrade() ? 1 : 1;
        // Si quieres recuperar el último número de la tabla anterior,
        // hazlo en el controller antes de llamar al servicio.

        // Fórmula genérica (ajusta según tu Excel)
        Integer tick = in.isHouse()
                ? in.getTicksSl1()
                : in.getTicksSl2();
        BigDecimal contracts = in.getAccountSize()
                .multiply(new BigDecimal(tick))
                .divideToIntegralValue(
                        BigDecimal.valueOf(in.getStopLossSize())
                );
        // aquí recogemos WIN o LOSS según el checkbox
        String wl = in.isWin()
                ? "WIN"
                : "LOSS";
        rows.add(RiskResultDTO.builder()
                .tradeNumber(tradeNum)
                .wl(wl)
                .account(in.getAccount())
                .marketData(in.getMarketData())
                // Aquí mostramos #contratos en la columna AccountSize
                .accountSize(contracts)
                .riskKellyA(in.isHouse() ? in.getTicksSl1().doubleValue() : null)
                .riskKellyB(in.isLunch() ? in.getTicksSl2().doubleValue() : null)
                .build()
        );

        return rows;
    }
}
