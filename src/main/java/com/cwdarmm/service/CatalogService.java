package com.cwdarmm.service;

import com.cwdarmm.model.domain.AccountDefinition;
import com.cwdarmm.model.domain.FeedDefinition;
import com.cwdarmm.model.domain.AccountFeed;
import com.cwdarmm.model.domain.MarketMaster;
import com.cwdarmm.repository.AccountDefinitionRepository;
import com.cwdarmm.repository.FeedDefinitionRepository;
import com.cwdarmm.repository.AccountFeedRepository;
import com.cwdarmm.repository.MarketMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final AccountDefinitionRepository accountRepo;
    private final MarketMasterRepository marketRepo;
    private final FeedDefinitionRepository feedRepo;
    private final AccountFeedRepository accountFeedRepo;

    /**
     * Devuelve todas las cuentas (para el combo Account)
     */
    public List<AccountDefinition> listAccounts() {
        return accountRepo.findAll();
    }

    /**
     * Devuelve todos los mercados (para el combo Market)
     */
    public List<MarketMaster> listMarkets() {
        return marketRepo.findAll();
    }

    /**
     * Devuelve los feeds (Market Data) disponibles para una cuenta dada.
     */
    public List<FeedDefinition> listFeedsByAccount(Long accountId) {
        return accountFeedRepo.findByAccountId(accountId)
                .stream()
                .map(AccountFeed::getFeed)
                .collect(Collectors.toList());
    }
}
