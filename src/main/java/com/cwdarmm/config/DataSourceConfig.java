package com.cwdarmm.config;

/**
 * Configuración básica del datasource (SQLite embebido).
 */

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:sqlite:cwdarmm.db");
        ds.setDriverClassName("org.sqlite.JDBC");
        return ds;
    }
}

