package com.cwdarmm.repository;

import com.cwdarmm.model.domain.PriceFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedDefinitionRepository extends JpaRepository<PriceFeed, Long> {
}
