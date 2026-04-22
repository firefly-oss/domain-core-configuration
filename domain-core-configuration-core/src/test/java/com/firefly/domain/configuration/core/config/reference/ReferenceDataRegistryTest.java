package com.firefly.domain.configuration.core.config.reference;

import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataValidationException;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReferenceDataRegistryTest {

    @Test
    void resolve_returnsMatchingService() {
        FakeService<Object, UUID> countryService = new FakeService<>(ReferenceDataEntityType.COUNTRY);
        FakeService<Object, UUID> currencyService = new FakeService<>(ReferenceDataEntityType.CURRENCY);

        ReferenceDataRegistry registry = new ReferenceDataRegistry(List.of(countryService, currencyService));

        assertThat(registry.size()).isEqualTo(2);
        assertThat(registry.resolve(ReferenceDataEntityType.CURRENCY)).isSameAs(currencyService);
    }

    @Test
    void resolve_unknownType_throwsValidationException() {
        ReferenceDataRegistry registry = new ReferenceDataRegistry(List.of());

        assertThatThrownBy(() -> registry.resolve(ReferenceDataEntityType.BANK))
                .isInstanceOf(ReferenceDataValidationException.class);
    }

    @Test
    void duplicateRegistration_failsFast() {
        FakeService<Object, UUID> first = new FakeService<>(ReferenceDataEntityType.COUNTRY);
        FakeService<Object, UUID> duplicate = new FakeService<>(ReferenceDataEntityType.COUNTRY);

        assertThatThrownBy(() -> new ReferenceDataRegistry(List.of(first, duplicate)))
                .isInstanceOf(IllegalStateException.class);
    }

    private static class FakeService<D, I> implements ReferenceDataService<D, I> {
        private final ReferenceDataEntityType type;

        FakeService(ReferenceDataEntityType type) {
            this.type = type;
        }

        @Override public ReferenceDataEntityType entityType() { return type; }
        @Override public Mono<I> create(D dto) { return Mono.empty(); }
        @Override public Mono<D> update(I id, D dto) { return Mono.empty(); }
        @Override public Mono<Void> remove(I id) { return Mono.empty(); }
        @Override public Mono<D> getById(I id) { return Mono.empty(); }
        @Override public Flux<D> list(ReferenceDataFilter filter) { return Flux.empty(); }

        // Suppress unused warning on generic mapper helper
        @SuppressWarnings("unused")
        static <A, B> ReferenceDataMapper<A, B> ignored() { return null; }
    }
}
