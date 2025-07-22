package com.cwdarmm.service;

import com.cwdarmm.model.domain.PriceFeed;
import com.cwdarmm.model.domain.TradingAccount;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.domain.MarketEntity;
import com.cwdarmm.model.domain.OpenMarket;
import com.cwdarmm.repository.AccountDefinitionRepository;
import com.cwdarmm.repository.MarketMasterRepository;
import com.cwdarmm.repository.FeedDefinitionRepository;
import com.cwdarmm.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketDataService {
    private final AccountDefinitionRepository accountRepo;
    private final MarketMasterRepository marketMasterRepo;
    private final FeedDefinitionRepository feedRepo;
    private final MarketRepository marketRepo;

    /**
     * Persistir nueva configuración de mercado (o actualizar existente).
     */
    @Transactional
    public MarketDTO save(MarketDTO dto) {
        // Resolver entidades
        TradingAccount acc = accountRepo.findById(dto.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no existe: " + dto.getAccount()));
        OpenMarket mkt = marketMasterRepo.findById(dto.getMarket().getId())
                .orElseThrow(() -> new IllegalArgumentException("Mercado no existe: " + dto.getMarket()));
        PriceFeed fd = feedRepo.findById(dto.getMarketData().getId())
                .orElseThrow(() -> new IllegalArgumentException("Feed no existe: " + dto.getMarketData()));

        MarketEntity entity = MarketEntity.builder()
                .id(dto.getId())
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
        entity.getAccount().getName();
        entity.getMarket().getName();
        entity.getMarketData().getName();
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
    public List<TradingAccount> listAccounts() {
        return accountRepo.findAll();
    }

    public List<OpenMarket> listMarkets() {
        return marketMasterRepo.findAll();
    }

    public List<PriceFeed> listMarketData() {
        return feedRepo.findAll();
    }


    public void delete(Long id) {
        marketRepo.deleteById(id);
    }
    private MarketDTO toDTO(MarketEntity e) {
        return MarketDTO.builder()
                .id(e.getId())
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
