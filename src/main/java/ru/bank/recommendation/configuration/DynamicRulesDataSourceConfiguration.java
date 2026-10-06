package ru.bank.recommendation.configuration;


import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Конфигурация DataSource для базы динамических правил (PostgreSQL).
 *
 * Использует {@link org.springframework.boot.jdbc.autoconfigure.DataSourceProperties},
 * зарегистрированный как {@code @Primary} DataSource приложения.
 */
@Configuration
public class DynamicRulesDataSourceConfiguration {

    @Primary
    @Bean(name = "defaultDataSource")
    public DataSource defaultDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}
