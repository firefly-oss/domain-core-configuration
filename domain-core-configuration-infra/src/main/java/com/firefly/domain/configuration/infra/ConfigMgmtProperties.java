package com.firefly.domain.configuration.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the config management SDK.
 * Maps the properties defined in application.yaml under api-configuration.core-common.config-mgmt.
 */
@Configuration
@ConfigurationProperties(prefix = "api-configuration.core-common.config-mgmt")
@Data
public class ConfigMgmtProperties {

    private String basePath;

}
