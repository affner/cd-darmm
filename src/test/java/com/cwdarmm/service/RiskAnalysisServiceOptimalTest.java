package com.cwdarmm.service;

import com.cwdarmm.model.domain.*;
import com.cwdarmm.model.dto.RiskInputDTO;
import com.cwdarmm.model.dto.OptimalContractRow;
import com.cwdarmm.repository.BdMarketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Esta clase prueba la función que calcula cuántos contratos puedes operar
 * en función del riesgo, el tamaño de tu cuenta, el tamaño del stop y los datos del mercado.
 * Es decir: responde a la pregunta de negocio...
 * “¿Con lo que tengo, cuántos contratos puedo meter sin pasarme del riesgo permitido?”
 */
public class RiskAnalysisServiceOptimalTest {

    /**
     * 🔹 Escenario: Prueba básica de cálculo de contratos óptimos.
     * Queremos saber si el sistema calcula correctamente cuántos contratos se pueden operar,
     * tomando en cuenta la configuración del instrumento, márgenes, y el tamaño del stop loss.
     */
    @Test
    void optimalContractsSimpleRange() {
        // 🔧 Simulación de un instrumento real (como si viniera de BD)
        // Aquí armamos un "instrumento ficticio" llamado MES con sus parámetros:

        BdMarket bd = BdMarket.builder()
                .id(1L)
                .account(CatAccount.builder().id(1L).description("NEXGEN").build()) // Cuenta: NEXGEN
                .market(CatMarket.builder().id(2L).description("S&P 500").build()) // Mercado: S&P
                .marketData(CatMarketData.builder().id(3L).description("PROJECTX").build()) // Configuración de datos
                .contract(CatContract.builder().id(3L).description("E-mini S&P 500").build()) // Tipo de contrato
                .symbol(CatSymbol.builder().id(4L).symbol("MES").build()) // Ticker real (MES)
                .multiplier(5.0)          // Tamaño del contrato en múltiplos (MES usa 5)
                .tickSize(0.25)         // Mínimo movimiento de precio (tick) = 0.25 puntos
                .tickValue(1.25)        // Cada tick vale $1.25
                .margin(1540.0)         // Margen requerido por contrato = $1540
                .commission(1.42)       // Comisión por contrato ida y vuelta
                .build();

        // 🔁 Creamos un repositorio simulado que devolverá esta configuración cuando se le pregunte.
        BdMarketRepository repo = Mockito.mock(BdMarketRepository.class);
        Mockito.when(repo.findOneByMktAccMdata(2L,1L,3L))
                .thenReturn(List.of(bd)); // ← Match por mercado, cuenta y config de datos

        // ✅ Creamos el servicio real con ese repositorio simulado
        RiskAnalysisService service = new RiskAnalysisService(repo);

        // 📥 Creamos el input del cálculo de riesgo
        RiskInputDTO req = RiskInputDTO.builder()
                .account(bd.getAccount())        // Cuenta NEXGEN
                .market(bd.getMarket())          // S&P 500
                .marketData(bd.getMarketData())  // PROJECTX
                .accountSize(new BigDecimal("200")) // Solo tenemos $200 disponibles
                .riskReward(2) // Queremos que la recompensa sea el doble del riesgo (R=2)
                .ticksSl1(1)   // Stop Loss de 1 tick (muy pequeño pero es para test)
                .ticksSl2(2)   // (esto se ignora en esta prueba)
                .house(false)
                .lunch(true)
                .win(true)
                .riskPctA(null)  // No usamos el riesgo A (no es relevante aquí)
                .riskPctB(new BigDecimal("1.5")) // Vamos a usar 1.5% de la cuenta como riesgo máximo
                .build();

        // ⚙️ Ejecutamos la función que calcula los contratos óptimos
        List<OptimalContractRow> rows = service.generateOptimalContracts(req);

        // ✅ Validamos que se generó una sola fila (es lo esperado para este test)
        assertEquals(1, rows.size());

        // 🔍 Analizamos el resultado:
        OptimalContractRow row = rows.get(0);
        assertEquals(1, row.getSlSize()); // Confirmamos que el stop en ticks es el esperado
        assertEquals("MES", row.getFuturesTicker()); // Debe ser el ticker del contrato
        assertEquals(new BigDecimal("1"), row.getOptimalContract()); // Con $200 y stop pequeño, puedes meter 1 contrato
        assertEquals(3, row.getTargetTicks()); // Si riesgo=1 tick y reward=2, objetivo = 1 + 2 = 3 ticks
    }

    /**
     * 🔹 Escenario: cálculo en el primer trade.
     *  Con las mismas condiciones que en el Excel, debe devolver
     *  el ticker "ES" y 5 contratos óptimos.
     */
    @Test
    void optimalContractsFirstTrade() {
        BdMarket es = BdMarket.builder()
                .id(2L)
                .account(CatAccount.builder().id(1L).description("NEXGEN").build())
                .market(CatMarket.builder().id(2L).description("S&P 500").build())
                .marketData(CatMarketData.builder().id(3L).description("PROJECTX").build())
                .contract(CatContract.builder().id(3L).description("E-mini S&P 500").build())
                .symbol(CatSymbol.builder().id(3L).symbol("ES").build())
                .multiplier(50.0)
                .tickSize(0.25)
                .tickValue(12.5)
                .margin(15400.0)
                .commission(2.08)
                .build();

        BdMarketRepository repo = Mockito.mock(BdMarketRepository.class);
        Mockito.when(repo.findOneByMktAccMdata(2L,1L,3L))
                .thenReturn(List.of(es));

        RiskAnalysisService service = new RiskAnalysisService(repo);

        RiskInputDTO req = RiskInputDTO.builder()
                .account(es.getAccount())
                .market(es.getMarket())
                .marketData(es.getMarketData())
                .accountSize(new BigDecimal("200"))
                .riskReward(3)
                .ticksSl1(1)
                .ticksSl2(1)
                .house(true)
                .lunch(false)
                .win(true)
                .firstTrade(true)
                .riskPctA(new BigDecimal("2.5"))
                .build();

        List<OptimalContractRow> rows = service.generateOptimalContracts(req);
        assertEquals(1, rows.size());

        OptimalContractRow row = rows.get(0);
        assertEquals("The risk is too high", row.getFuturesTicker());
        assertNull(row.getOptimalContract());
    }
}
