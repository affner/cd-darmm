package com.cwdarmm.service;

/**
 * Generador ficticio de resultados optimizados para la
 * pestaña RESULTS mientras se implementa la lógica real.
 */

import com.cwdarmm.model.domain.BdMarket;
import com.cwdarmm.model.dto.ResultRowDTO;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.repository.BdMarketRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptimizationService {

    private final BdMarketRepository bdMarketRepo;

    private final Random rnd = new Random();

    /**
     * Simula el CLICK de la hoja RESULTS:
     * Genera 10 filas con valores "aleatorios" coherentes
     * para cada columna.
     */
    public List<ResultRowDTO> dummyCalculate() {
        List<ResultRowDTO> rows = new ArrayList<>();

        // Simulamos 10 combinaciones distintas
        String[] assets  = {"S&P 500", "NASDAQ", "DOWJ"};
        String[] brokers = {"TRADEIFY", "BROKERX"};
        String[] symbols = {"ES", "MES", "NQ"};

        for (int i = 0; i < 10; i++) {
            ResultRowDTO row = new ResultRowDTO();

            // Ciclo entre unos pocos activos, brokers y símbolos
            row.getAsset().set( assets[i % assets.length] );
            row.getBroker().set( brokers[i % brokers.length] );
            row.getSymbol().set( symbols[i % symbols.length] );

            // Target entre 9 y 21
            int target = 9 + rnd.nextInt(13);
            row.getTarget().set(target);

            // SL Size entre 4 y 10
            int sl = 4 + rnd.nextInt(7);
            row.getSlSize().set(sl);

            // Risk per contract = sl * precio ficticio (p.ej. 13.92)
            double pricePerTick = 13 + rnd.nextDouble() * 2;
            double riskPerContract = sl * pricePerTick;
            row.getRiskPerContract().set( round(riskPerContract) );

            // Optimal contracts = floor(200 / riskPerContract)
            int optimal = Math.max(1, (int)(200 / riskPerContract));
            row.getOptimalContract().set(optimal);

            // Total capital used = optimal * riskPerContract
            double capital = optimal * riskPerContract;
            row.getCapitalUsed().set( round(capital) );

            // Real risk = capital / inicial (p.ej. 123123.0)
            double realRisk = capital / 123123.0;
            row.getRealRisk().set( round(realRisk) );

            // Potential profit = target * optimal * random precio
            double profit = target * optimal * (pricePerTick + rnd.nextDouble());
            row.getPotentialProfit().set( round(profit) );

            // Potential loss = sl * optimal * pricePerTick
            double loss = sl * optimal * pricePerTick;
            row.getPotentialLoss().set( round(loss) );

            // Risk % = (loss / 123123.0) * 100
            double pct = loss / 123123.0 * 100;
            row.getRiskPercentage().set( round(pct) );

            rows.add(row);
        }

        return rows;
    }

    /** Redondea a 2 decimales */
    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    /**
     * Calcula la tabla de resultados replicando la lógica del
     * formulario VBA risk_market.frm.
     */
    public List<ResultRowDTO> calculate(RiskInputDTO in) {
        List<BdMarket> rows = bdMarketRepo.findOneByMktAccMdata(
                in.getMarket().getId(),
                in.getAccount().getId(),
                in.getMarketData().getId());
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("No BD_MARKET rows for selection");
        }

        List<ResultRowDTO> results = new ArrayList<>();

        // Riesgo inicial ajustado por resultado WIN/LOSS
        java.math.BigDecimal riskPct = in.isHouse() ? in.getRiskPctA() : in.getRiskPctB();
        if (riskPct == null) riskPct = java.math.BigDecimal.ZERO;
        if (!in.isFirstTrade()) {
            java.math.BigDecimal mult = in.isWin() ? new java.math.BigDecimal("1.05") : new java.math.BigDecimal("0.98");
            riskPct = riskPct.multiply(mult);
        }
        double pctBase = riskPct.doubleValue();
        double x = pctBase < 1.0 ? 0.03 : 0.1;

        int start = in.getTicksSl1();
        int end   = in.getTicksSl2() >= start ? in.getTicksSl2() : start;

        int offset = "NASDAQ".equalsIgnoreCase(in.getMarket().getDescription()) ? 2 : 1;

        for (BdMarket bd : rows) {
            double tickValue = bd.getTickValue();
            double commission = bd.getCommission();
            String symbol = bd.getSymbol().getSymbol();

            for (int sl = start; sl <= end; sl++) {
                for (int k = 0; k < 2; k++) {
                    double riskPctApplied = pctBase + (x * k);
                    double currentRisk = in.getAccountSize().doubleValue() * riskPctApplied / 100.0;
                    double riskPerContract = tickValue * sl + commission;
                    if (riskPerContract <= 0) continue;

                    int optimal = (int) Math.floor(currentRisk / riskPerContract);
                    double capitalUsed = optimal * riskPerContract;
                    double realRisk = capitalUsed * 100.0 / in.getAccountSize().doubleValue();
                    int target = (int) Math.round(in.getRiskReward() * sl + offset);
                    double potentialProfit = (optimal * tickValue * target) - (commission * optimal);
                    double potentialLoss  = optimal * riskPerContract;

                    ResultRowDTO row = new ResultRowDTO();
                    row.getAsset().set(in.getMarket().getDescription());
                    row.getBroker().set(in.getAccount().getDescription());
                    row.getSymbol().set(symbol);
                    row.getTarget().set(target);
                    row.getSlSize().set(sl);
                    row.getRiskPerContract().set(round(riskPerContract));
                    row.getOptimalContract().set(optimal);
                    row.getCapitalUsed().set(round(capitalUsed));
                    row.getRealRisk().set(round(realRisk));
                    row.getPotentialProfit().set(round(potentialProfit));
                    row.getPotentialLoss().set(round(potentialLoss));
                    row.getRiskPercentage().set(round(riskPctApplied));

                    // Color a aplicar en columnas ticker/optimal contract
                    String c1 = in.getMarket().getColor1();
                    String c2 = in.getMarket().getColor2();
                    String chosen = (symbol!=null && symbol.startsWith("M")) ? c1 : c2;
                    row.getRowColor().set(chosen);

                    results.add(row);
                }
            }
        }

        // Marcar la fila con mayor beneficio potencial
        double maxProfit = results.stream()
                .mapToDouble(r -> r.getPotentialProfit().get())
                .max().orElse(Double.NaN);
        results.forEach(r -> r.getOptimalRow().set(
                Double.compare(r.getPotentialProfit().get(), maxProfit) == 0));

        return results;
    }
}
