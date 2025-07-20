package com.cwdarmm.repository;

import com.cwdarmm.model.domain.AccountFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountFeedRepository extends JpaRepository<AccountFeed, Long> {
    List<AccountFeed> findByAccountId(Long accountId);
}
