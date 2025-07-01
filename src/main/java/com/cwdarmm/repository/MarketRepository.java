package com.cwdarmm.repository;

import com.cwdarmm.model.Market;
import java.util.List;

public interface MarketRepository {
    List<Market> findAll();
    Market findBySymbol(String symbol);
}
