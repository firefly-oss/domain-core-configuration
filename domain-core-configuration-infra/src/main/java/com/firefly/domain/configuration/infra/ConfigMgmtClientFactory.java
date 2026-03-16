package com.firefly.domain.configuration.infra;

import com.firefly.common.config.sdk.api.TenantBrandingsApi;
import com.firefly.common.config.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Client factory for the config management SDK.
 * Creates client service instances using the appropriate API clients and dependencies.
 */
@Component
public class ConfigMgmtClientFactory {

    private final ApiClient apiClient;

    @Autowired
    public ConfigMgmtClientFactory(ConfigMgmtProperties configMgmtProperties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(configMgmtProperties.getBasePath());
    }

    @Bean
    public TenantBrandingsApi tenantBrandingsApi() {
        return new TenantBrandingsApi(apiClient);
    }

}
