package com.cwdarmm.repository;

import com.cwdarmm.model.TradingAccount;
import java.util.List;

public interface AccountRepository {
    List<TradingAccount> findAll();
    TradingAccount findByName(String name);
}
