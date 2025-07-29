package com.cwdarmm.service;

/**
 * Acceso de solo lectura a la tabla <code>bd_markets</code>
 * para poblar vistas con información completa de contratos.
 */

import com.cwdarmm.model.domain.BdMarket;
import com.cwdarmm.model.dto.MarketDbRowDTO;
import com.cwdarmm.repository.BdMarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)    // abrimos tx para todo este service
public class MarketDbService {

    private final BdMarketRepository bdMarketRepo;

    public List<MarketDbRowDTO> listAll() {
        return bdMarketRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private MarketDbRowDTO toDto(BdMarket bd) {
        // ——————————————————————————————————————————————————
        // F O R Z A M O S   L A   C A R G A   D E   L O S   A S O C I A D O S
        // para que, una vez que esta tx termine, los proxies ya tengan
        // los campos en memoria y no lancen LazyInitializationException
        bd.getMarket().getDescription();
        bd.getAccount().getDescription();
        bd.getMarketData().getDescription();
        bd.getContract().getDescription();
        bd.getSymbol().getSymbol();
        // ——————————————————————————————————————————————————

        // construimos el DTO que contiene los ObjectProperty intactos
        // valores null → 0.0
        double multiplier = bd.getMultiplier() != null ? bd.getMultiplier() : 0.0;
        double tickSize   = bd.getTickSize()   != null ? bd.getTickSize()   : 0.0;
        double tickValue  = bd.getTickValue()  != null ? bd.getTickValue()  : 0.0;
        double margin     = bd.getMargin()     != null ? bd.getMargin()     : 0.0;
        double commission = bd.getCommission() != null ? bd.getCommission() : 0.0;

        return MarketDbRowDTO.builder()
                .future     (bd.getMarket())
                .account    (bd.getAccount())
                .marketData (bd.getMarketData())
                .name       (bd.getContract())
                .symbol     (bd.getSymbol())
                .multiplier (multiplier)
                .tickSize   (tickSize)
                .tickValue  (tickValue)
                .margin     (margin)
                .commission (commission)
                .build();
    }
}
