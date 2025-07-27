package com.cwdarmm.repository;

/**
 * Repositorio JPA para el catálogo de cuentas.
 */

import com.cwdarmm.model.domain.CatAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatAccountRepository extends JpaRepository<CatAccount, Long> {
}
