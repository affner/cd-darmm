package com.cwdarmm.repository;

import com.cwdarmm.model.domain.BdMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BdMarketRepository extends JpaRepository<BdMarket, Long> {

    /**
     * Obtiene todas las filas de BD_MARKETS con sus relaciones cargadas.
     */
    @Query("""
            select b from BdMarket b
             join fetch b.account
             join fetch b.market
             join fetch b.marketData
             join fetch b.contract
             join fetch b.symbol
            """)
    List<BdMarket> findAllWithFetch();
}
