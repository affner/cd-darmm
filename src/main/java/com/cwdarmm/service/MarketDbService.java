package com.cwdarmm.service;

import com.cwdarmm.model.dto.MarketDbRowDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que simula la carga de la hoja BD_MARKET.
 * Más adelante conectará a tu repositorio o parseará el XLSM.
 */
@Service
public class MarketDbService {

    public List<MarketDbRowDTO> listAll() {
        // dummy: reproducimos exactamente los dos rows de tu captura
        List<MarketDbRowDTO> list = new ArrayList<>();

        MarketDbRowDTO r1 = new MarketDbRowDTO();
        r1.setFuture("S&P 500");
        r1.setAccount("TRADEIFY");
        r1.setMarketData("TRADOVATE");
        r1.setName("E-mini S&P 500");
        r1.setSymbol("ES");
        r1.setMultiplier(50);
        r1.setTickSize(0.25);
        r1.setTickValue(12.5);
        r1.setMargin(15400);
        r1.setCommission(5.68);
        list.add(r1);

        MarketDbRowDTO r2 = new MarketDbRowDTO();
        r2.setFuture("S&P 500");
        r2.setAccount("TRADEIFY");
        r2.setMarketData("TRADOVATE");
        r2.setName("Micro E-mini S&P");
        r2.setSymbol("MES");
        r2.setMultiplier(5);
        r2.setTickSize(0.25);
        r2.setTickValue(1.25);
        r2.setMargin(1540);
        r2.setCommission(1.74);
        list.add(r2);

        return list;
    }
}
