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

package io.fireflysoftware.domain.configuration.parameters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Selects the sandbox adapter unless an installer sets the property to {@code core}.
 */
@Configuration
public class BusinessParameterAdapterConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "firefly.configuration.parameters",
            name = "adapter",
            havingValue = "sandbox",
            matchIfMissing = true)
    public BusinessParameterPort sandboxBusinessParameterPort() {
        return new SandboxBusinessParameterAdapter();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "firefly.configuration.parameters",
            name = "adapter",
            havingValue = "core")
    public BusinessParameterPort configMgmtBusinessParameterPort(
            @Value("${firefly.configuration.parameters.core-base-url:${ENDPOINT_CORE_CONFIG_MGMT:http://localhost:8080}}")
            String baseUrl) {
        WebClient client = WebClient.builder().baseUrl(baseUrl).build();
        return new ConfigMgmtBusinessParameterAdapter(client);
    }
}
