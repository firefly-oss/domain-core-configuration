package com.firefly.domain.configuration.core.config.reference;

import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CRUD contract shared by every reference-data entity service.
 * Implementations are registered in the Spring context and resolved at runtime
 * via {@link ReferenceDataRegistry}.
 *
 * @param <D> public domain DTO type exposed to the web layer
 * @param <I> identifier type (typically {@link java.util.UUID})
 */
public interface ReferenceDataService<D, I> {

    ReferenceDataEntityType entityType();

    Mono<I> create(D dto);

    Mono<D> update(I id, D dto);

    Mono<Void> remove(I id);

    Mono<D> getById(I id);

    Flux<D> list(ReferenceDataFilter filter);
}
