package com.cwdarmm.service;

import com.cwdarmm.model.domain.CatAccount;
import com.cwdarmm.model.domain.CatMarketData;
import com.cwdarmm.model.domain.CatMarket;
import com.cwdarmm.repository.CatAccountRepository;
import com.cwdarmm.repository.CatMarketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferenceDataService {

    private final CatAccountRepository accountRepo;
    private final CatMarketRepository marketRepo;

    /**
     * Devuelve todas las cuentas (para el combo Account)
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CatAccount> listAccounts() {
        return accountRepo.findAll();
    }

    /**
     * Devuelve todos los mercados (para el combo Market)
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CatMarket> listMarkets() {
        return marketRepo.findAll();
    }

    /**
     * Devuelve los feeds (Market Data) disponibles para una cuenta dada.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CatMarketData> listFeedsByAccount(Long accountId) {
        return accountRepo.findById(accountId)
                .map(CatAccount::getMarketDataList)         // Set<CatMarketData>
                .map(this::toList)
                .orElse(Collections.emptyList());
    }

    private List<CatMarketData> toList(Set<CatMarketData> set) {
        return set.stream().collect(Collectors.toList());
    }
}
