package com.cwdarmm.service;

import com.cwdarmm.model.dto.ResultRowDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class OptimizationService {

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
}
