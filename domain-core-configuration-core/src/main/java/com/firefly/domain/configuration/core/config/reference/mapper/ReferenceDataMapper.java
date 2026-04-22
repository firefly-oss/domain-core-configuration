package com.firefly.domain.configuration.core.config.reference.mapper;

/**
 * Translates between the public domain DTO and the SDK DTO.
 * Concrete implementations are declared as {@code @Mapper(componentModel = "spring")} interfaces.
 *
 * @param <D> public domain DTO type
 * @param <S> SDK DTO type
 */
public interface ReferenceDataMapper<D, S> {

    S toSdk(D domainDto);

    D toDomain(S sdkDto);
}
