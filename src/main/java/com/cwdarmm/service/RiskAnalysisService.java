package com.cwdarmm.service;

import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio que reproduce la lógica de cálculo de riesgo y
 * contratos óptimos descrita en el documento
 * "CW-DARMM: A Probabilistic, Confidence-Weighted Framework for
 * Adaptive Capital Growth".
 *
 * <p>La implementación toma como referencia el módulo VBA
 * <code>risk_market.frm</code> del libro Excel, donde se calculan
 * los tamaños de contrato y se actualizan los porcentajes de
 * riesgo según el resultado del trade. Todas las validaciones y
 * fórmulas se han trasladado a Java para integrarse con la
 * aplicación de escritorio.</p>
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(RiskAnalysisService.class);


    private final OptimalContractsCalculator optimalContractsCalculator;

    /**
     * Ejecuta la simulación de trades y calcula métricas básicas.
     */
    public List<RiskResultDTO> calculate(RiskInputDTO in) {
        // 1) Preparamos la lista donde se guardarán los resultados
        //    (ver secciones iniciales de docs/GOOD ARTICLE.pdf para una
        //     explicación del flujo de riesgo)
        List<RiskResultDTO> rows = new ArrayList<>();

        log.info("CALC IN  firstTrade={} win={} riskA={} riskB={}",
                in.isFirstTrade(), in.isWin(), in.getRiskPctA(), in.getRiskPctB());

        // 2) Si es el primer trade, sólo devolvemos INITIAL y salimos
        if (in.isFirstTrade()) {
            rows.add(RiskResultDTO.builder()
                    .tradeNumber(0)
                    .wl("INITIAL")
                    .account(in.getAccount())
                    .marketData(in.getMarketData())
                    .accountSize(in.getAccountSize())
                    .riskKellyA(in.getRiskPctA() == null ? null : in.getRiskPctA().doubleValue())
                    .riskKellyB(in.getRiskPctB() == null ? null : in.getRiskPctB().doubleValue())
                    .build());
            return rows;  // <<< aquí devolvemos sólo INITIAL
        }

        // 3) Según el resultado del trade ajustamos los porcentajes de riesgo
        BigDecimal multiplier = in.isWin() ? new BigDecimal("1.05") : new BigDecimal("0.98");
        BigDecimal newRiskA = in.isHouse() && in.getRiskPctA() != null ? in.getRiskPctA().multiply(multiplier) : null;
        if (newRiskA != null) newRiskA = newRiskA.setScale(3, java.math.RoundingMode.HALF_UP);
        BigDecimal newRiskB = in.isLunch() && in.getRiskPctB() != null ? in.getRiskPctB().multiply(multiplier) : null;
        if (newRiskB != null) newRiskB = newRiskB.setScale(3, java.math.RoundingMode.HALF_UP);

        // 4) Registramos el trade actual, WIN o LOSS
        String wl = in.isWin() ? "WIN" : "LOSS";
        rows.add(RiskResultDTO.builder()
                .tradeNumber(1)
                .wl(wl)
                .account(in.getAccount())
                .marketData(in.getMarketData())
                .accountSize(in.getAccountSize())
                .riskKellyA(newRiskA == null ? null : newRiskA.doubleValue())
                .riskKellyB(newRiskB == null ? null : newRiskB.doubleValue())
                .build());// <<< sólo la fila WIN/LOSS

        // 5) Devolvemos la lista para su visualización o exportación
        return rows;
    }


    public List<OptimalContractRow> generateOptimalContracts(RiskInputDTO in) {
        return optimalContractsCalculator.calculate(in);
    }
}
