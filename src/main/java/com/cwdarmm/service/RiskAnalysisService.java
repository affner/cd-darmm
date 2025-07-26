// RiskAnalysisService.java
package com.cwdarmm.service;

import com.cwdarmm.model.dto.OptimalContractRow;
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
        int tradeNum = 1;

        // aquí sólo devolvemos la fila OPTIMAL “genérica”
        int tick = in.isHouse() ? in.getTicksSl1() : in.getTicksSl2();
        BigDecimal contracts = in.getAccountSize().multiply(BigDecimal.valueOf(tick)).divideToIntegralValue(
                BigDecimal.valueOf(in.getStopLossSize()));
        // aquí recogemos WIN o LOSS según el checkbox
        String wl = in.isWin() ? "WIN" : "LOSS";

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

    /**
     * Genera la tabla de Optimal Contracts para la ventana emergente.
     */
    public List<OptimalContractRow> generateOptimalContracts(RiskInputDTO in) {
        List<OptimalContractRow> out = new ArrayList<>();

        // Por cada sesión seleccionada, calculamos su fila
        if (in.isHouse()) {
            out.add(makeRow(in, in.getTicksSl1()));
        }
        if (in.isLunch()) {
            out.add(makeRow(in, in.getTicksSl2()));
        }

        return out;
    }

    private OptimalContractRow makeRow(RiskInputDTO in, int slTicks) {
        // 1) Cálculo de contratos (sólo parte entera)
        BigDecimal possible = in.getAccountSize()
                .multiply(BigDecimal.valueOf(slTicks))
                .divideToIntegralValue(BigDecimal.valueOf(in.getStopLossSize()));

        // 2) Cálculo del target en ticks = SL * RiskReward
        int target = (int) Math.round(in.getRiskReward() * slTicks);

        // 3) Ticker o mensaje
        String ticker = in.getMarketData().getName(); // o getTicker()

        if (possible.compareTo(BigDecimal.ONE) < 0) {
            // no alcanzas ni a 1 contrato → mensaje de “riesgo muy alto”
            return new OptimalContractRow(
                    slTicks,
                    "The risk is too high",
                    null,
                    target
            );
        } else {
            return new OptimalContractRow(
                    slTicks,
                    ticker,
                    possible,
                    target
            );
        }
    }
}
