package com.cwdarmm.service;

import com.cwdarmm.model.domain.AccountDefinition;
import com.cwdarmm.model.domain.FeedDefinition;
import com.cwdarmm.model.domain.MarketMaster;
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
public class CatalogService {

    private final AccountDefinitionRepository accountRepo;
    private final MarketMasterRepository marketRepo;

    /**
     * Devuelve todas las cuentas (para el combo Account)
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AccountDefinition> listAccounts() {
        return accountRepo.findAll();
    }

    /**
     * Devuelve todos los mercados (para el combo Market)
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MarketMaster> listMarkets() {
        return marketRepo.findAll();
    }

    /**
     * Devuelve los feeds (Market Data) disponibles para una cuenta dada.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<FeedDefinition> listFeedsByAccount(Long accountId) {
        return accountRepo.findById(accountId)
                .map(AccountDefinition::getFeeds)         // Set<FeedDefinition>
                .map(this::toList)
                .orElse(Collections.emptyList());
    }

    private List<FeedDefinition> toList(Set<FeedDefinition> set) {
        return set.stream().collect(Collectors.toList());
    }
}
