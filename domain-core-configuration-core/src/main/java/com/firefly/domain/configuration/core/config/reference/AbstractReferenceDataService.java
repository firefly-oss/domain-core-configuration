package com.firefly.domain.configuration.core.config.reference;

import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataConflictException;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataNotFoundException;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Generic base service that carries the common CRUD orchestration (idempotency keys,
 * logging, error mapping) for every reference-data entity. Concrete subclasses supply
 * the narrow SDK-binding hooks.
 *
 * @param <A> SDK API class
 * @param <S> SDK DTO type
 * @param <D> public domain DTO type
 * @param <I> identifier type
 */
@Slf4j
public abstract class AbstractReferenceDataService<A, S, D, I> implements ReferenceDataService<D, I> {

    protected final A api;
    protected final ReferenceDataMapper<D, S> mapper;

    protected AbstractReferenceDataService(A api, ReferenceDataMapper<D, S> mapper) {
        this.api = api;
        this.mapper = mapper;
    }

    // SDK-binding hooks — one-line overrides in concrete subclasses

    protected abstract Mono<S> sdkCreate(S payload, String idempotencyKey);

    protected abstract Mono<S> sdkUpdate(I id, S payload, String idempotencyKey);

    protected abstract Mono<Void> sdkDelete(I id, String idempotencyKey);

    protected abstract Mono<S> sdkGetById(I id);

    protected abstract Flux<S> sdkList(ReferenceDataFilter filter);

    protected abstract I extractId(S response);

    // Public contract — final orchestration flows

    @Override
    public final Mono<I> create(D dto) {
        String idempotencyKey = UUID.randomUUID().toString();
        S sdkPayload = mapper.toSdk(dto);
        return sdkCreate(sdkPayload, idempotencyKey)
                .map(this::extractId)
                .doOnSuccess(id -> log.info("Created {} id={}", entityType(), id))
                .onErrorMap(this::mapSdkError);
    }

    @Override
    public final Mono<D> update(I id, D dto) {
        String idempotencyKey = UUID.randomUUID().toString();
        S sdkPayload = mapper.toSdk(dto);
        return sdkUpdate(id, sdkPayload, idempotencyKey)
                .map(mapper::toDomain)
                .doOnSuccess(result -> log.info("Updated {} id={}", entityType(), id))
                .onErrorMap(this::mapSdkError);
    }

    @Override
    public final Mono<Void> remove(I id) {
        String idempotencyKey = UUID.randomUUID().toString();
        return sdkDelete(id, idempotencyKey)
                .doOnSuccess(v -> log.info("Removed {} id={}", entityType(), id))
                .onErrorMap(this::mapSdkError);
    }

    @Override
    public final Mono<D> getById(I id) {
        return sdkGetById(id)
                .map(mapper::toDomain)
                .onErrorMap(this::mapSdkError);
    }

    @Override
    public final Flux<D> list(ReferenceDataFilter filter) {
        return sdkList(filter)
                .map(mapper::toDomain)
                .onErrorMap(this::mapSdkError);
    }

    private Throwable mapSdkError(Throwable ex) {
        if (ex instanceof WebClientResponseException wcre) {
            int status = wcre.getStatusCode().value();
            if (status == 404) {
                return new ReferenceDataNotFoundException(
                        entityType() + " not found: " + wcre.getMessage(), wcre);
            }
            if (status == 409) {
                return new ReferenceDataConflictException(
                        entityType() + " conflict: " + wcre.getMessage(), wcre);
            }
        }
        return ex;
    }
}
