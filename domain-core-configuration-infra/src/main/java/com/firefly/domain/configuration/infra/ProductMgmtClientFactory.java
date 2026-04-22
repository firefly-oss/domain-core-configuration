package com.firefly.domain.configuration.infra;

import com.firefly.core.product.sdk.api.ProductApi;
import com.firefly.core.product.sdk.api.ProductConfigurationApi;
import com.firefly.core.product.sdk.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Client factory for the product management SDK.
 * Exposes only the APIs that this domain needs for cross-entity activation checks
 * (e.g., preventing deactivation of a currency referenced by active products).
 */
@Component
public class ProductMgmtClientFactory {

    private final ApiClient apiClient;

    @Autowired
    public ProductMgmtClientFactory(ProductMgmtProperties productMgmtProperties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(productMgmtProperties.getBasePath());
    }

    @Bean
    public ProductConfigurationApi productConfigurationApi() {
        return new ProductConfigurationApi(apiClient);
    }

    @Bean
    public ProductApi productApi() {
        return new ProductApi(apiClient);
    }

}
