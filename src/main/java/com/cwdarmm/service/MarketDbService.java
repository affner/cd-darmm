package com.cwdarmm.service;

import com.cwdarmm.model.domain.BdMarket;
import com.cwdarmm.model.dto.MarketDbRowDTO;
import com.cwdarmm.repository.BdMarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que simula la carga de la hoja BD_MARKET.
 * Más adelante conectará a tu repositorio o parseará el XLSM.
 */
@Service
@RequiredArgsConstructor
public class MarketDbService {

    private final BdMarketRepository repository;

    /**
     * Devuelve todas las filas de la tabla BD_MARKETS mapeadas a DTO para la UI.
     */
    @Transactional(readOnly = true)
    public List<MarketDbRowDTO> listAll() {
        return repository.findAllWithFetch().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private MarketDbRowDTO toDTO(BdMarket e) {
        MarketDbRowDTO dto = new MarketDbRowDTO();
        dto.setFuture(e.getMarket().getDescription());
        dto.setAccount(e.getAccount().getDescription());
        dto.setMarketData(e.getMarketData().getDescription());
        dto.setName(e.getContract().getDescription());
        dto.setSymbol(e.getSymbol().getSymbol());
        dto.setMultiplier(e.getMultiplier());
        dto.setTickSize(e.getTickSize());
        dto.setTickValue(e.getTickValue());
        dto.setMargin(e.getMargin());
        dto.setCommission(e.getCommission());
        return dto;
    }
}
