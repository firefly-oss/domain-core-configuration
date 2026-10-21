/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.configuration.infra;

import io.fireflysoftware.common.config.sdk.api.TenantBrandingsApi;
import io.fireflysoftware.common.config.sdk.invoker.ApiClient;
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
