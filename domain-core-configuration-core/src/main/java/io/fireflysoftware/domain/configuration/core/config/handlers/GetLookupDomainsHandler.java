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


package io.fireflysoftware.domain.configuration.core.config.handlers;

import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import io.fireflysoftware.common.reference.master.data.sdk.api.LookupDomainsApi;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.configuration.core.config.queries.LookupDomainQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetLookupDomainsHandler extends QueryHandler<LookupDomainQuery, PaginationResponse> {

    private final LookupDomainsApi lookupDomainsApi;

    @Override
    protected Mono<PaginationResponse> doHandle(LookupDomainQuery query) {
        return lookupDomainsApi.listDomains(0, 100, null, null, UUID.randomUUID().toString());
    }

}
