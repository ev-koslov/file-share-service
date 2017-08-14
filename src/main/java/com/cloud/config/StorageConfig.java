package com.cloud.config;


import com.cloud.common.components.AspectWatcherBean;
import com.cloud.storage.services.SimpleStorageService;
import com.cloud.storage.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Storage system configuration
 */
@Configuration
@ComponentScan(basePackages = "com.cloud.storage")
public class StorageConfig {
    @Autowired
    private Environment environment;

    @Bean
    public StorageService configureStorageService(AspectWatcherBean watcherBean) throws IOException {
        Path path = Paths.get(environment.getProperty("file.cloud.storage.rootDir"));

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        return new SimpleStorageService(path.toString(), watcherBean);
    }
}
