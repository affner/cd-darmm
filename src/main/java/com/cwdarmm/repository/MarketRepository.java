package com.cwdarmm.repository;



import com.cwdarmm.model.domain.MarketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketRepository extends JpaRepository<MarketEntity, Long> {
    @Query("""
      select m from MarketEntity m 
       join fetch m.account 
       join fetch m.market 
       join fetch m.marketData
      """)
    List<MarketEntity> findAllWithFetch();
}
