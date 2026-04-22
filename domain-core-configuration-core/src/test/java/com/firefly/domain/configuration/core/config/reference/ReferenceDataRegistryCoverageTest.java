package com.firefly.domain.configuration.core.config.reference;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the contract that every {@link ReferenceDataEntityType} constant has a
 * concrete {@link ReferenceDataService} registered in the Spring context.
 * Future additions to the enum must be accompanied by a matching service subclass.
 */
@SpringBootTest(classes = ReferenceDataRegistryCoverageTest.TestConfig.class)
class ReferenceDataRegistryCoverageTest {

    @Autowired
    private ReferenceDataRegistry registry;

    @Test
    void everyEnumValueHasRegisteredService() {
        for (ReferenceDataEntityType type : ReferenceDataEntityType.values()) {
            assertThat(registry.contains(type))
                    .as("Missing ReferenceDataService registration for %s", type)
                    .isTrue();
        }
        assertThat(registry.size()).isEqualTo(ReferenceDataEntityType.values().length);
    }

    @Configuration
    @ComponentScan(basePackages = {
            "com.firefly.domain.configuration.core.config.reference",
            "com.firefly.domain.configuration.infra"
    })
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = {
            org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.class,
            org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class
    })
    static class TestConfig {
    }
}
