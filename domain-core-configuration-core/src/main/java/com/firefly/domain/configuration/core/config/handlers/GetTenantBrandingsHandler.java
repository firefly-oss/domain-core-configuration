package com.firefly.domain.configuration.core.config.handlers;

import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import com.firefly.common.config.sdk.api.TenantBrandingsApi;
import com.firefly.common.config.sdk.model.PaginationResponseTenantBrandingDTO;
import com.firefly.common.config.sdk.model.TenantBrandingDTO;
import com.firefly.domain.configuration.core.config.queries.TenantBrandingQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetTenantBrandingsHandler extends QueryHandler<TenantBrandingQuery, List<TenantBrandingDTO>> {

    private final TenantBrandingsApi tenantBrandingsApi;

    @Override
    protected Mono<List<TenantBrandingDTO>> doHandle(TenantBrandingQuery query) {
        return tenantBrandingsApi.filterTenantBrandings(0, 100, null, null, null, null, UUID.randomUUID().toString())
                .mapNotNull(PaginationResponseTenantBrandingDTO::getContent);
    }

}
