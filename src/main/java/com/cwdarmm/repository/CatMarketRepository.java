package com.cwdarmm.repository;

import com.cwdarmm.model.domain.CatMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatMarketRepository extends JpaRepository<CatMarket, Long> {
}
