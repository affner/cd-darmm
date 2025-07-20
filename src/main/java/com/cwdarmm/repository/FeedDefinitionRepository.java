package com.cwdarmm.repository;

import com.cwdarmm.model.domain.FeedDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedDefinitionRepository extends JpaRepository<FeedDefinition, Long> {
}
