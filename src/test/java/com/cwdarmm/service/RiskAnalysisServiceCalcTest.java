package com.cwdarmm.service;

import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.model.domain.CatMarketData;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.RiskResultDTO;
import com.cwdarmm.repository.BdMarketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Esta clase prueba la lógica de cálculo de riesgo que usa el motor CW-DARMM.
 * Se testea cómo responde el sistema a diferentes escenarios: primer trade, ganar o perder.
 */
public class RiskAnalysisServiceCalcTest {

    // Creamos una instancia del servicio de riesgo usando un repositorio simulado (mock).
    // Esto es para poder probar sin depender de la base de datos.
    private RiskAnalysisService service = new RiskAnalysisService(Mockito.mock(BdMarketRepository.class));

    // Método auxiliar que construye un objeto base con datos de entrada mínimos.
    private RiskInputDTO baseInput() {
        return RiskInputDTO.builder()
                .account(CatAccount.builder().id(1L).description("ACC").build()) // Cuenta dummy
                .marketData(CatMarketData.builder().id(2L).description("DATA").build()) // Mercado dummy
                .accountSize(new BigDecimal("1000")) // Saldo de la cuenta: $1000
                .build();
    }

    /**
     * 🔹 Caso 1: Primer trade del sistema.
     * Se espera que devuelva una sola fila con etiqueta "INITIAL".
     * No importa si ganaste o perdiste, aún no hay historial. Se usa el riesgo que tú le indicas (sin ajuste).
     */
    @Test
    void firstTradeReturnsInitialRow() {
        RiskInputDTO in = baseInput().toBuilder()
                .firstTrade(true) // Indica que es la primera operación
                .riskPctA(new BigDecimal("2.0")) // Riesgo definido por el usuario para estrategia A (2%)
                .riskPctB(new BigDecimal("1.0")) // Riesgo para estrategia B (1%)
                .build();

        List<RiskResultDTO> rows = service.calculate(in);
        assertEquals(1, rows.size());

        RiskResultDTO r = rows.get(0);
        assertEquals(0, r.getTradeNumber()); // Primer trade = número 0
        assertEquals("INITIAL", r.getWl());  // Se etiqueta como "INITIAL"
        assertEquals(new BigDecimal("1000"), r.getAccountSize()); // Se mantiene el saldo original
        assertEquals(2.0, r.getRiskKellyA()); // No hay ajuste en el riesgo
        assertEquals(1.0, r.getRiskKellyB());
    }

    /**
     * 🔹 Caso 2: Ganaste la operación anterior.
     * El sistema aplica la lógica de compounding (DARMM): aumenta el riesgo un 5%.
     * Ejemplo: si arriesgabas 2.0%, ahora arriesgas 2.1% en la siguiente.
     */
    @Test
    void winAdjustsRiskPercentages() {
        RiskInputDTO in = baseInput().toBuilder()
                .firstTrade(false) // Ya hubo al menos una operación antes
                .house(true)       // Flags internos que activan el cálculo, según la lógica del sistema
                .lunch(true)
                .win(true)         // Este trade fue ganador
                .riskPctA(new BigDecimal("2.0")) // Riesgo anterior (A)
                .riskPctB(new BigDecimal("1.5")) // Riesgo anterior (B)
                .build();

        List<RiskResultDTO> rows = service.calculate(in);
        assertEquals(1, rows.size());

        RiskResultDTO r = rows.get(0);
        assertEquals(1, r.getTradeNumber()); // Este sería el segundo trade (número 1)
        assertEquals("WIN", r.getWl());      // Se etiqueta como "WIN"
        // Se espera que el riesgo suba un 5% → lógica DARMM compounding
        assertEquals(2.0 * 1.05, r.getRiskKellyA(), 1e-9);
        assertEquals(1.5 * 1.05, r.getRiskKellyB(), 1e-9);
    }

    /**
     * 🔹 Caso 3: Perdiste la operación anterior.
     * El sistema aplica la lógica de contracción: baja el riesgo un 2%.
     * Ejemplo: si arriesgabas 3.0%, ahora arriesgas 2.94%.
     */
    @Test
    void lossAdjustsRiskPercentagesDown() {
        RiskInputDTO in = baseInput().toBuilder()
                .firstTrade(false) // Ya hubo operaciones antes
                .house(true)
                .lunch(true)
                .win(false)        // Este trade fue perdedor
                .riskPctA(new BigDecimal("3.0")) // Riesgo anterior (A)
                .riskPctB(new BigDecimal("2.0")) // Riesgo anterior (B)
                .build();

        List<RiskResultDTO> rows = service.calculate(in);
        assertEquals(1, rows.size());

        RiskResultDTO r = rows.get(0);
        assertEquals(1, r.getTradeNumber()); // Segundo trade
        assertEquals("LOSS", r.getWl());     // Se etiqueta como "LOSS"
        // Se espera que el riesgo baje un 2% → lógica DARMM en modo defensivo
        assertEquals(3.0 * 0.98, r.getRiskKellyA(), 1e-9);
        assertEquals(2.0 * 0.98, r.getRiskKellyB(), 1e-9);
    }

    /**
     * Verifica la secuencia WIN → LOSS → WIN para Kelly B con redondeo a 3 decimales.
     */
    @Test
    void kellyBSequenceUsesLastRisk() {
        RiskInputDTO first = baseInput().toBuilder()
                .firstTrade(false)
                .lunch(true)
                .win(true)
                .riskPctB(new BigDecimal("1.500"))
                .build();

        double step1 = service.calculate(first).get(0).getRiskKellyB();
        assertEquals(1.575, step1, 1e-9);

        RiskInputDTO second = baseInput().toBuilder()
                .firstTrade(false)
                .lunch(true)
                .win(false)
                .riskPctB(BigDecimal.valueOf(step1))
                .build();

        double step2 = service.calculate(second).get(0).getRiskKellyB();
        assertEquals(1.544, step2, 1e-9);

        RiskInputDTO third = baseInput().toBuilder()
                .firstTrade(false)
                .lunch(true)
                .win(true)
                .riskPctB(BigDecimal.valueOf(step2))
                .build();

        double step3 = service.calculate(third).get(0).getRiskKellyB();
        assertEquals(1.621, step3, 1e-9);
    }
}
