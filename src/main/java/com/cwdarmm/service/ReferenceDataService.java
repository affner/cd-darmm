package com.cwdarmm.service;

import com.cwdarmm.model.domain.TradingAccount;
import com.cwdarmm.model.domain.PriceFeed;
import com.cwdarmm.model.domain.OpenMarket;
import com.cwdarmm.repository.AccountDefinitionRepository;
import com.cwdarmm.repository.MarketMasterRepository;
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

    private final AccountDefinitionRepository accountRepo;
    private final MarketMasterRepository marketRepo;

    /**
     * Devuelve todas las cuentas (para el combo Account)
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TradingAccount> listAccounts() {
        return accountRepo.findAll();
    }

    /**
     * Devuelve todos los mercados (para el combo Market)
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<OpenMarket> listMarkets() {
        return marketRepo.findAll();
    }

    /**
     * Devuelve los feeds (Market Data) disponibles para una cuenta dada.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PriceFeed> listFeedsByAccount(Long accountId) {
        return accountRepo.findById(accountId)
                .map(TradingAccount::getFeeds)         // Set<PriceFeed>
                .map(this::toList)
                .orElse(Collections.emptyList());
    }

    private List<PriceFeed> toList(Set<PriceFeed> set) {
        return set.stream().collect(Collectors.toList());
    }
}
