package com.cwdarmm.repository;

/**
 * Repositorio JPA para los símbolos de contrato.
 */

import com.cwdarmm.model.domain.CatSymbol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatSymbolRepository extends JpaRepository<CatSymbol, Long> {
}
