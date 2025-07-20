package com.cwdarmm.repository;

import com.cwdarmm.model.domain.AccountDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDefinitionRepository extends JpaRepository<AccountDefinition, Long> {
}
