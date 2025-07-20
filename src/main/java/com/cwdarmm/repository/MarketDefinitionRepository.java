package com.cwdarmm.repository;

import com.cwdarmm.model.domain.MarketDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketDefinitionRepository extends JpaRepository<MarketDefinition, String> {
}

