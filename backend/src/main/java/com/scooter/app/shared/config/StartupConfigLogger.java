package com.scooter.app.shared.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StartupConfigLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupConfigLogger.class);

    private final Environment environment;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.flyway.locations}")
    private String flywayLocations;

    public StartupConfigLogger(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void logResolvedConfig() {
        String activeProfiles = Arrays.toString(environment.getActiveProfiles());
        String configName = environment.getProperty("spring.config.name", "application");
        String configLocation = environment.getProperty("spring.config.location", "classpath:/");

        log.info("Resolved datasource configuration: url='{}', username='{}'", datasourceUrl, datasourceUsername);
        log.info("Resolved Flyway locations: {}", flywayLocations);
        log.info("Spring active profiles: {}", activeProfiles);
        log.info("Spring config sources: spring.config.name='{}', spring.config.location='{}'", configName, configLocation);
    }
}
