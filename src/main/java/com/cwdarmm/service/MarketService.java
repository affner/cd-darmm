package com.cwdarmm.service;

import com.cwdarmm.model.dto.MarketDTO;
import org.springframework.stereotype.Service;
import java.util.List;
import com.cwdarmm.model.domain.MarketEntity;
import com.cwdarmm.repository.MarketRepository;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final MarketRepository repo;

    public List<MarketDTO> findAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MarketDTO save(MarketDTO dto) {
        MarketEntity e = new MarketEntity(null,
                dto.getAccount(), dto.getMarket(), dto.getMarketData(),
                dto.getAccountSize(), dto.getRiskA(), dto.getRiskB(), dto.getRiskFinalHouse(), dto.getRiskFinalLunch());
        e = repo.save(e);
        return toDTO(e);
    }

    /**
     * Catálogos provisionales: extraer de BD o configuración si está disponible.
     */
    public List<String> listAccounts() {
        return List.of("ACC1", "ACC2", "ACC3");
    }

    public List<String> listMarkets() {
        return List.of("Dow Jones", "S&P 500", "NASDAQ");
    }

    public List<String> listMarketData() {
        return List.of("Data A", "Data B", "Data C");
    }

    private MarketDTO toDTO(MarketEntity e) {
        return MarketDTO.builder()
                .account(e.getAccount())
                .market(e.getMarket())
                .marketData(e.getMarketData())
                .accountSize(e.getAccountSize())
                .riskA(e.getRiskA())
                .riskB(e.getRiskB())
                .build();

    }

}
