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


    /**
     * Redondea a un número arbitrario de decimales
     */
    private double round(double v, int scale) {
        double factor = Math.pow(10, scale);
        return Math.round(v * factor) / factor;
    }

    /**
     * Redondea a 2 decimales
     */
    private double round(double v) {
        return round(v, 2);
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

        // Excel usa SOLO el primer SL (ticksSl1)
        final int sl = in.getTicksSl1();

        // Offset idéntico a Optimal Contracts
        final int offset = "NASDAQ".equalsIgnoreCase(in.getMarket().getDescription()) ? 2 : 1;

        // Blindaje por si riskReward llega 0/null → usa 2
        double rr = 2.0;
        try {
            // si getRiskReward() es Double/BigDecimal, conviértelo con cuidado
            rr = in.getRiskReward() > 0 ? in.getRiskReward() : 2.0;
        } catch (Exception ignore) {
            rr = 2.0;
        }

        // Target consistente con Optimal Contracts (ej.: rr=2, sl=1, offset=1 → 3)
        final int targetTicks = (int) Math.round(rr * sl + offset);

        // En el Excel original se calculan escenarios para Kelly A/B (+bono x)
        // Aquí replicamos: base y base+x (x depende del % base)
        List<java.math.BigDecimal> baseRiskPcts = new ArrayList<>();
        if (in.isHouse() && in.getRiskPctA() != null) baseRiskPcts.add(in.getRiskPctA());
        if (in.isLunch() && in.getRiskPctB() != null) baseRiskPcts.add(in.getRiskPctB());
        if (baseRiskPcts.isEmpty()) baseRiskPcts.add(java.math.BigDecimal.ZERO);

        for (java.math.BigDecimal basePctOrig : baseRiskPcts) {
            java.math.BigDecimal basePct = basePctOrig;

            // Ajuste compounding según resultado (igual que en tu RiskAnalysisService)
            if (!in.isFirstTrade()) {
                java.math.BigDecimal mult = in.isWin()
                        ? new java.math.BigDecimal("1.05")
                        : new java.math.BigDecimal("0.98");
                basePct = basePct.multiply(mult);
            }
            double pctBase = basePct.doubleValue();
            double x = pctBase < 1.0 ? 0.03 : 0.1; // mismo criterio que ya usabas

            for (BdMarket bd : rows) {
                double tickValue = bd.getTickValue();
                double commission = bd.getCommission();
                String symbol = bd.getSymbol().getSymbol();

                // k=0 → base, k=1 → base + x
                for (int k = 0; k < 2; k++) {
                    double riskPctApplied = pctBase + (x * k);

                    // Risk disponible en $
                    double currentRisk = in.getAccountSize().doubleValue() * (riskPctApplied / 100.0);

                    // Riesgo por contrato = sl*tickValue + comisión (como en VBA)
                    double riskPerContract = sl * tickValue + commission;
                    if (riskPerContract <= 0) continue;

                    // Contratos óptimos = floor(currentRisk / rpc)
                    int optimal = (int) Math.floor(currentRisk / riskPerContract);

                    // Regla del primer trade (si la usas): arranque con 5
                    if (optimal < 1 && in.isFirstTrade()) {
                        optimal = 5;
                    }

                    double capitalUsed = optimal * riskPerContract;
                    double realRiskPct = (in.getAccountSize().doubleValue() == 0) ? 0
                            : (capitalUsed * 100.0 / in.getAccountSize().doubleValue());

                    //Usa SIEMPRE el targetTicks ya calculado (coincide con Optimal Contracts)
                    double potentialProfit = (optimal * tickValue * targetTicks) - (commission * optimal);
                    double potentialLoss = optimal * riskPerContract;

                    ResultRowDTO row = new ResultRowDTO();
                    row.getAsset().set(in.getMarket().getDescription());
                    row.getBroker().set(in.getAccount().getDescription());
                    row.getSymbol().set(symbol);

                    // Aquí es donde antes te salía 2: ahora queda 3
                    row.getTarget().set(targetTicks);

                    row.getSlSize().set(sl);
                    row.getRiskPerContract().set(round(riskPerContract));
                    row.getOptimalContract().set(optimal);
                    row.getCapitalUsed().set(round(capitalUsed));
                    row.getRealRisk().set(round(realRiskPct));
                    row.getPotentialProfit().set(round(potentialProfit));
                    row.getPotentialLoss().set(round(potentialLoss));
                    // % de riesgo mostrado con 3 decimales (como en tu UI)
                    row.getRiskPercentage().set(round(riskPctApplied, 3));

                    // Colores por símbolo (micro vs grande)
                    String c1 = in.getMarket().getColor1();
                    String c2 = in.getMarket().getColor2();
                    String chosen = (symbol != null && symbol.startsWith("M")) ? c1 : c2;
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
