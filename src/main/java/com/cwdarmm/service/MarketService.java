package com.cwdarmm.service;

import com.cwdarmm.event.MarketCreatedEvent;
import com.cwdarmm.model.domain.Market;
import com.cwdarmm.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository repo;
    private final ApplicationEventPublisher pub;
    private final FxWizardService wizard;      // pequeño helper para mostrar wizard

    /* ---------- catálogo ---------- */

    public List<Market> findAllReferenceMarkets() {
        return repo.findReferenceCatalog();
    }

    /* ---------- abrir mercado ---------- */

    public void openWizardAndCreateMarket(Market base) throws Exception {
        Market filled = wizard.runOpenMarketWizard(base);
        if (filled == null) return;                   // usuario canceló

        repo.save(filled);                            // persiste en CSV/JSON
        pub.publishEvent(new MarketCreatedEvent(filled));
    }

    /* ---------- ciclo semanal ---------- */

    public void archiveAndResetAll() {
        repo.archiveWeekAndReset();
    }
}
