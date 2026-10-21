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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Port for business parameters. Banking and insurance journeys read limits, switches,
 * account identifiers, and transfer routes through this port.
 *
 * <p>The default adapter is {@link SandboxBusinessParameterAdapter}. An installer replaces
 * that adapter with {@link ConfigMgmtBusinessParameterAdapter} by setting
 * {@code firefly.configuration.parameters.adapter=core}. Identity and rules stay in Jafly.
 */
public interface BusinessParameterPort {

    Mono<BusinessParameter> findByKey(String key);

    Flux<BusinessParameter> list();

    Mono<BusinessParameter> findById(UUID id);

    Mono<BusinessParameter> create(BusinessParameter draft);

    Mono<BusinessParameter> update(UUID id, BusinessParameter draft);

    Mono<BusinessParameter> changeStatus(UUID id, ParameterStatus status);

    Mono<Void> remove(UUID id);

    Flux<ParameterHistoryEntry> history(UUID id);
}
