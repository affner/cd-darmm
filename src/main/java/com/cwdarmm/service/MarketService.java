package com.cwdarmm.service;

import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.domain.AccountDefinition;
import com.cwdarmm.model.domain.MarketEntity;
import com.cwdarmm.model.domain.MarketMaster;
import com.cwdarmm.model.domain.FeedDefinition;
import com.cwdarmm.repository.AccountDefinitionRepository;
import com.cwdarmm.repository.MarketMasterRepository;
import com.cwdarmm.repository.FeedDefinitionRepository;
import com.cwdarmm.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketService {
    private final AccountDefinitionRepository accountRepo;
    private final MarketMasterRepository marketMasterRepo;
    private final FeedDefinitionRepository feedRepo;
    private final MarketRepository marketRepo;

    /**
     * Persistir nueva configuración de mercado.
     */
    public MarketDTO save(MarketDTO dto) {
        // Resolver entidades
        AccountDefinition acc = accountRepo.findById(dto.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no existe: " + dto.getAccount()));
        MarketMaster mkt = marketMasterRepo.findById(dto.getMarket().getId())
                .orElseThrow(() -> new IllegalArgumentException("Mercado no existe: " + dto.getMarket()));
        FeedDefinition fd = feedRepo.findById(dto.getMarketData().getId())
                .orElseThrow(() -> new IllegalArgumentException("Feed no existe: " + dto.getMarketData()));

        MarketEntity entity = MarketEntity.builder()
                .account(acc)
                .market(mkt)
                .marketData(fd)
                .accountSize(dto.getAccountSize())
                .riskA(dto.getRiskA())
                .riskB(dto.getRiskB())
                .riskFinalHouse(dto.getRiskFinalHouse())
                .riskFinalLunch(dto.getRiskFinalLunch())
                .build();

        entity = marketRepo.save(entity);
        return toDTO(entity);
    }

    /**
     * Listar todas las configuraciones guardadas.
     */
    public List<MarketDTO> findAll() {
        return marketRepo.findAllWithFetch().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Catálogos para UI
     */
    public List<AccountDefinition> listAccounts() {
        return accountRepo.findAll();
    }

    public List<MarketMaster> listMarkets() {
        return marketMasterRepo.findAll();
    }

    public List<FeedDefinition> listMarketData() {
        return feedRepo.findAll();
    }


    private MarketDTO toDTO(MarketEntity e) {
        return MarketDTO.builder()
                .account(e.getAccount())
                .market(e.getMarket())
                .marketData(e.getMarketData())
                .accountSize(e.getAccountSize())
                .riskA(e.getRiskA())
                .riskB(e.getRiskB())
                .riskFinalHouse(e.getRiskFinalHouse())
                .riskFinalLunch(e.getRiskFinalLunch())
                .build();
    }
}
