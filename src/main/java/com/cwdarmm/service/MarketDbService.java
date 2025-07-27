package com.cwdarmm.service;

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
        // F O R Z A M O   L A   C A R G A   D E   L O S   A S O C I A D O S
        // para que, una vez que esta tx termine, los proxies ya tengan
        // los campos en memoria y no lancen LazyInitializationException
        bd.getMarket().getDescription();
        bd.getAccount().getDescription();
        bd.getMarketData().getDescription();
        bd.getContract().getDescription();
        bd.getSymbol().getSymbol();
        // ——————————————————————————————————————————————————

        // construimos el DTO que contiene los ObjectProperty intactos
        return MarketDbRowDTO.builder()
                .future     (bd.getMarket())
                .account    (bd.getAccount())
                .marketData (bd.getMarketData())
                .name       (bd.getContract())
                .symbol     (bd.getSymbol())
                .multiplier (bd.getMultiplier())
                .tickSize   (bd.getTickSize())
                .tickValue  (bd.getTickValue())
                .margin     (bd.getMargin())
                .commission (bd.getCommission())
                .build();
    }
}
