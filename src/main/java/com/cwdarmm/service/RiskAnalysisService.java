// RiskAnalysisService.java
package com.cwdarmm.service;

import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.model.dto.MarketDbRowDTO;
import com.cwdarmm.service.MarketDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    private final MarketDbService marketDbService;


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

        // b) Cálculo de “Optimal Contracts” simplificado
        int tradeNum = 1;

        // Para este ejemplo tomamos el SL de la sesión seleccionada
        int sl = in.isHouse() ? in.getTicksSl1() : in.getTicksSl2();

        MarketDbRowDTO row = marketDbService.find(
                in.getMarket().getName(),
                in.getAccount().getName(),
                in.getMarketData().getName());

        double tickValue = row != null ? row.getTickValue() : 1.0;
        double commission = row != null ? row.getCommission() : 0.0;

        double riskPct = sl; // interpretamos sl como porcentaje de riesgo
        double riskPerContract = tickValue * in.getStopLossSize() + commission;
        BigDecimal currentRisk = in.getAccountSize()
                .multiply(BigDecimal.valueOf(riskPct))
                .divide(BigDecimal.valueOf(100));

        BigDecimal contracts = currentRisk
                .divide(BigDecimal.valueOf(riskPerContract), 0, RoundingMode.DOWN);
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

    private OptimalContractRow makeRow(RiskInputDTO in, int riskPct) {
        int slTicks = in.getStopLossSize();

        MarketDbRowDTO row = marketDbService.find(
                in.getMarket().getName(),
                in.getAccount().getName(),
                in.getMarketData().getName());

        double tickValue = row != null ? row.getTickValue() : 1.0;
        double commission = row != null ? row.getCommission() : 0.0;

        double riskPerContract = tickValue * slTicks + commission;
        BigDecimal currentRisk = in.getAccountSize()
                .multiply(BigDecimal.valueOf(riskPct))
                .divide(BigDecimal.valueOf(100));

        BigDecimal possible = currentRisk
                .divide(BigDecimal.valueOf(riskPerContract), 0, RoundingMode.DOWN);

        int r = "NASDAQ".equalsIgnoreCase(in.getMarket().getName()) ? 2 : 1;
        int target = (int) Math.round(in.getRiskReward() * slTicks + r);

        String ticker = row != null ? row.getSymbol() : in.getMarketData().getName();

        if (possible.compareTo(BigDecimal.ONE) < 0) {
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
