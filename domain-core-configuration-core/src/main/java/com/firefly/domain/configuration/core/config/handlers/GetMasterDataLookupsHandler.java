package com.firefly.domain.configuration.core.config.handlers;

import com.firefly.common.reference.master.data.sdk.api.LookupDomainsApi;
import com.firefly.common.reference.master.data.sdk.api.LookupItemsApi;
import com.firefly.common.reference.master.data.sdk.model.LookupDomainDTO;
import com.firefly.common.reference.master.data.sdk.model.LookupItemDTO;
import com.firefly.domain.configuration.core.config.queries.MasterDataLookupsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Aggregates the {@code lookup_domains} + {@code lookup_items} catalogues
 * exposed by {@code core-common-reference-master-data} into a single map
 * keyed by domain code so the experience tier can project the items it
 * needs without orchestrating the join itself.
 *
 * <p>Domains without items are still represented (empty list) so callers
 * can distinguish "domain exists, no items" from "domain code unknown".
 */
@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetMasterDataLookupsHandler
        extends QueryHandler<MasterDataLookupsQuery, Map<String, List<LookupItemDTO>>> {

    /** Page size used when paginating through lookup_domains. */
    private static final int DOMAINS_PAGE_SIZE = 200;

    private final LookupDomainsApi lookupDomainsApi;
    private final LookupItemsApi lookupItemsApi;

    @Override
    protected Mono<Map<String, List<LookupItemDTO>>> doHandle(MasterDataLookupsQuery query) {
        log.debug("Aggregating lookup items by domain code");
        return lookupDomainsApi
                .listDomains(0, DOMAINS_PAGE_SIZE, null, null, UUID.randomUUID().toString())
                .flatMapMany(this::extractDomains)
                .flatMap(this::fetchItemsForDomain)
                .<Map<String, List<LookupItemDTO>>>collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.code(), entry.items()));
    }

    private Flux<LookupDomainDTO> extractDomains(Object pageResponse) {
        // The SDK returns PaginationResponse with content as List<Object>.
        // Convert via reflection-light cast to keep the handler decoupled
        // from the generated PaginationResponse type structure.
        if (pageResponse == null) {
            return Flux.empty();
        }
        try {
            Object content = pageResponse.getClass().getMethod("getContent").invoke(pageResponse);
            if (!(content instanceof List<?> list)) {
                return Flux.empty();
            }
            return Flux.fromIterable(list)
                    .map(LookupDomainDTO.class::cast);
        } catch (ReflectiveOperationException ex) {
            return Flux.error(new IllegalStateException(
                    "Unable to read lookup_domains page content", ex));
        }
    }

    private Mono<DomainItems> fetchItemsForDomain(LookupDomainDTO domain) {
        if (domain == null || domain.getDomainId() == null || domain.getDomainCode() == null) {
            return Mono.empty();
        }
        return lookupItemsApi
                .getItemsByDomainWithResponseSpec(domain.getDomainId(), UUID.randomUUID().toString())
                .bodyToFlux(LookupItemDTO.class)
                .collectList()
                .map(items -> new DomainItems(domain.getDomainCode(), items))
                .onErrorResume(err -> {
                    log.warn("Skipping domain {} ({}): {}",
                            domain.getDomainCode(), domain.getDomainId(), err.getMessage());
                    return Mono.just(new DomainItems(domain.getDomainCode(), List.of()));
                });
    }

    private record DomainItems(String code, List<LookupItemDTO> items) {
    }
}
