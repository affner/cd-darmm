package com.cwdarmm.repository;

import com.cwdarmm.model.domain.OpenMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketMasterRepository extends JpaRepository<OpenMarket, Long> {
}
