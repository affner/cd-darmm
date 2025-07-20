package com.cwdarmm.repository;

import com.cwdarmm.model.domain.AccountMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountMarketRepository extends JpaRepository<AccountMarket, Long> {
    List<AccountMarket> findByAccount(String account);
}
