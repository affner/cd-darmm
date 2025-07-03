package com.cwdarmm.repository;

import com.cwdarmm.model.domain.Market;
import java.util.List;

public interface MarketRepository {
    List<Market> findReferenceCatalog();   // ES, NQ, GC … semilla
    void save(Market m);
    void archiveWeekAndReset();


}
