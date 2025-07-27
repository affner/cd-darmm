package com.cwdarmm.service;

import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.model.domain.CatMarketData;
import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.model.dto.MarketDTO;
import com.cwdarmm.model.domain.OpenMarket;
import com.cwdarmm.repository.CatAccountRepository;
import com.cwdarmm.repository.CatMarketRepository;
import com.cwdarmm.repository.CatMarketDataRepository;
import com.cwdarmm.repository.OpenMarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de acceso y manipulación de los catálogos de mercado.
 *
 * <p>Persiste la información de la sesión abierta y se alimenta
 * de los catálogos equivalentes a las hojas <code>BD_MARKET</code>
 * y <code>RESULTS</code> en el libro de Excel. Desde aquí se
 * realizan las mismas validaciones básicas que estaban en VBA
 * antes de almacenar la información.</p>
 */

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketDataService {
    private final CatAccountRepository accountRepo;
    private final CatMarketRepository marketMasterRepo;
    private final CatMarketDataRepository feedRepo;
    private final OpenMarketRepository marketRepo;

    /**
     * Persistir nueva configuración de mercado (o actualizar existente).
     */
    @Transactional
    public MarketDTO save(MarketDTO dto) {
        // Resolver entidades
        CatAccount acc = accountRepo.findById(dto.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no existe: " + dto.getAccount()));
        CatMarket mkt = marketMasterRepo.findById(dto.getMarket().getId())
                .orElseThrow(() -> new IllegalArgumentException("Mercado no existe: " + dto.getMarket()));
        CatMarketData fd = feedRepo.findById(dto.getMarketData().getId())
                .orElseThrow(() -> new IllegalArgumentException("Feed no existe: " + dto.getMarketData()));

        OpenMarket entity = OpenMarket.builder()
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
        entity.getAccount().getDescription();
        entity.getMarket().getDescription();
        entity.getMarketData().getDescription();
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
    public List<CatAccount> listAccounts() {
        return accountRepo.findAll();
    }

    public List<CatMarket> listMarkets() {
        return marketMasterRepo.findAll();
    }

    public List<CatMarketData> listMarketData() {
        return feedRepo.findAll();
    }


    public void delete(Long id) {
        marketRepo.deleteById(id);
    }
    private MarketDTO toDTO(OpenMarket e) {
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
