package com.firefly.domain.configuration.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the product management SDK.
 * Maps the properties defined in application.yaml under api-configuration.core-common.product-mgmt.
 */
@Configuration
@ConfigurationProperties(prefix = "api-configuration.core-common.product-mgmt")
@Data
public class ProductMgmtProperties {

    private String basePath;

}
