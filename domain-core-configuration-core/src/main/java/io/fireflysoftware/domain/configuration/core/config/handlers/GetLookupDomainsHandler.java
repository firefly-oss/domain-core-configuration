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
