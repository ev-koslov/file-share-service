package com.cloud.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Application database configuration
 */
@Configuration
@ComponentScan("com.cloud.database")
@EnableJpaRepositories(basePackages = "com.cloud.database.repositories")
@EnableTransactionManagement
@EntityScan(basePackages = "com.cloud.database.dto")
public class DatabaseConfig {
}
