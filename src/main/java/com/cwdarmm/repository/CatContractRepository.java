package com.cwdarmm.repository;

import com.cwdarmm.model.domain.CatContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatContractRepository extends JpaRepository<CatContract, Long> {
}
