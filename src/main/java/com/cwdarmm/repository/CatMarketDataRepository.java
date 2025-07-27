package com.cwdarmm.repository;

/**
 * Repositorio JPA para proveedores de datos de mercado.
 */

import com.cwdarmm.model.domain.CatMarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatMarketDataRepository extends JpaRepository<CatMarketData, Long> {
}
