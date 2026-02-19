package com.scooter.app.shared.config;

import org.flywaydb.core.api.exception.FlywayValidateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(
            @Value("${app.flyway.auto-repair-on-validation-error:false}") boolean autoRepairOnValidationError
    ) {
        return flyway -> {
            try {
                flyway.migrate();
            } catch (FlywayValidateException ex) {
                if (!autoRepairOnValidationError) {
                    throw ex;
                }

                log.warn("Flyway validation failed. Running flyway repair because app.flyway.auto-repair-on-validation-error=true.", ex);
                flyway.repair();
                flyway.migrate();
            }
        };
    }
}
