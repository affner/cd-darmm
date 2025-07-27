package com.cwdarmm.repository;

import com.cwdarmm.model.domain.BdMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

public interface BdMarketRepository extends JpaRepository<BdMarket, Long> {
    /**
     * Trae un BdMarket (con join-fetch de todos los cat_*)
     * filtrando por los tres cat IDs.
     */
    @Query("""
            select b 
            from BdMarket b
            join fetch b.market      m
            join fetch b.account     a
            join fetch b.marketData  md
            join fetch b.contract    c
            join fetch b.symbol      s
            where m.id  = :marketId
              and a.id  = :accountId
              and md.id = :marketDataId
            """)
    List<BdMarket> findOneByMktAccMdata(@Param("marketId") Long marketId, @Param("accountId") Long accountId, @Param("marketDataId") Long marketDataId);
}
