// RiskAnalysisService.java
package com.cwdarmm.service;

import com.cwdarmm.model.dto.MarketDbRowDTO;
import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * Genera la tabla de Optimal Contracts para la ventana emergente,
     * ahora usando tickValue y commission desde MarketDbRowDTO.
     */
    public List<OptimalContractRow> generateOptimalContracts(RiskInputDTO in) {
        // 1) Traemos todos los mercados de BD y buscamos el que coincide
        List<MarketDbRowDTO> dbRows = marketDbService.listAll();
        // 2) Localizamos la fila que coincide con:
        //    FUTURE = in.market.name  (ej. "S&P 500")
        //    MARKETDATA = in.marketData.name (ej. "TRADOVATE")
        MarketDbRowDTO db = dbRows.stream()
                .filter(r ->
                        r.getFuture().equals(in.getMarket().getDescription()) &&
                                r.getMarketData().equals(in.getMarketData().getDescription())
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No encontré BD_MARKET para " +
                                in.getMarket().getDescription() + " / " +
                                in.getMarketData().getDescription()
                ));

        // 3) Creamos una fila por cada sesión (HOUSE / LUNCH)
        return List.of(
                        in.isHouse() ? makeRow(in, db, in.getTicksSl1(), in.getRiskPctA()) : null,
                        in.isLunch() ? makeRow(in, db, in.getTicksSl2(), in.getRiskPctB()) : null
                ).stream()
                .filter(row -> row != null)
                .collect(Collectors.toList());
    }

    private OptimalContractRow makeRow(RiskInputDTO in,
                                       MarketDbRowDTO db,
                                       int slTicks,
                                       BigDecimal riskPct) {
        // 1) currentRisk = accountSize * riskPct / 100
        BigDecimal pct = riskPct == null ? BigDecimal.ONE : riskPct;
        BigDecimal currentRisk = in.getAccountSize()
                .multiply(pct)
                .divide(BigDecimal.valueOf(100),
                        8,
                        BigDecimal.ROUND_HALF_UP);

        // 2) riskPerContract = tickValue * SL + commission
        BigDecimal tickValue  = BigDecimal.valueOf(db.getTickValue().doubleValue());
        BigDecimal commission = BigDecimal.valueOf(db.getCommission().doubleValue());
        BigDecimal riskPerContract = tickValue
                .multiply(BigDecimal.valueOf(slTicks))
                .add(commission);

        // 3) optimalContracts = floor(currentRisk / riskPerContract)
        BigDecimal optimalContracts = BigDecimal.ZERO;
        if (riskPerContract.compareTo(BigDecimal.ZERO) > 0) {
            optimalContracts = currentRisk
                    .divide(riskPerContract,
                            0,
                            BigDecimal.ROUND_DOWN);
        }

        // 4) targetTicks = SL * riskReward + offset
        int offset = 0; // si más adelante traes un campo getOffset() lo usas aquí
        int targetTicks = (int) Math.round(
                in.getRiskReward() * slTicks + offset
        );

        // 5) Ticker del contrato desde BD_MARKET
        String futuresTicker = db.getSymbol().get();

        // 6) Devolvemos la fila final
        if (optimalContracts.compareTo(BigDecimal.ONE) < 0) {
            return new OptimalContractRow(
                    slTicks,
                    "The risk is too high",
                    null,
                    targetTicks
            );
        }
        return new OptimalContractRow(
                slTicks,
                futuresTicker,
                optimalContracts,
                targetTicks
        );
    }
}
