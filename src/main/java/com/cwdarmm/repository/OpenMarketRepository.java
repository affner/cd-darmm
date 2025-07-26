package com.cwdarmm.repository;



import com.cwdarmm.model.domain.OpenMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenMarketRepository extends JpaRepository<OpenMarket, Long> {
    @Query("""
      select m from OpenMarket m 
       join fetch m.account 
       join fetch m.market 
       join fetch m.marketData
      """)
    List<OpenMarket> findAllWithFetch();
}
