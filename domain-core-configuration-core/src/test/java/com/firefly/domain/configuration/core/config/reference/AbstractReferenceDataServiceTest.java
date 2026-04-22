package com.firefly.domain.configuration.core.config.reference;

import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataConflictException;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataNotFoundException;
import com.firefly.domain.configuration.core.config.reference.mapper.ReferenceDataMapper;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractReferenceDataServiceTest {

    private FakeMapper mapper;
    private FakeService service;

    @BeforeEach
    void setUp() {
        mapper = new FakeMapper();
        service = new FakeService(new FakeApi(), mapper);
    }

    @Test
    void create_passesFreshIdempotencyKeyAndReturnsExtractedId() {
        UUID generatedId = UUID.randomUUID();
        service.nextCreateResponse = new SdkDto(generatedId, "created");

        StepVerifier.create(service.create(new DomainDto("create-payload")))
                .expectNext(generatedId)
                .verifyComplete();

        assertThat(service.lastIdempotencyKey).isNotNull();
        assertThat(UUID.fromString(service.lastIdempotencyKey)).isNotNull();
        assertThat(mapper.toSdkCalls).isEqualTo(1);
    }

    @Test
    void update_mapsResponseAndPassesFreshIdempotencyKey() {
        UUID id = UUID.randomUUID();
        service.nextUpdateResponse = new SdkDto(id, "updated");

        StepVerifier.create(service.update(id, new DomainDto("update-payload")))
                .assertNext(result -> assertThat(result.value()).isEqualTo("updated"))
                .verifyComplete();

        assertThat(service.lastIdempotencyKey).isNotNull();
        assertThat(mapper.toSdkCalls).isEqualTo(1);
        assertThat(mapper.toDomainCalls).isEqualTo(1);
    }

    @Test
    void remove_passesFreshIdempotencyKey() {
        UUID id = UUID.randomUUID();

        StepVerifier.create(service.remove(id)).verifyComplete();

        assertThat(service.lastIdempotencyKey).isNotNull();
    }

    @Test
    void getById_mapsResponseAndAppliesMapperOnce() {
        UUID id = UUID.randomUUID();
        service.nextGetResponse = new SdkDto(id, "loaded");

        StepVerifier.create(service.getById(id))
                .assertNext(dto -> assertThat(dto.value()).isEqualTo("loaded"))
                .verifyComplete();

        assertThat(mapper.toDomainCalls).isEqualTo(1);
    }

    @Test
    void sdk404_isMappedToReferenceDataNotFoundException() {
        UUID id = UUID.randomUUID();
        service.nextGetError = WebClientResponseException.create(
                HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null);

        StepVerifier.create(service.getById(id))
                .expectError(ReferenceDataNotFoundException.class)
                .verify();
    }

    @Test
    void sdk409_isMappedToReferenceDataConflictException() {
        UUID id = UUID.randomUUID();
        service.nextGetError = WebClientResponseException.create(
                HttpStatus.CONFLICT.value(), "Conflict", null, null, null);

        StepVerifier.create(service.getById(id))
                .expectError(ReferenceDataConflictException.class)
                .verify();
    }

    @Test
    void list_appliesMapperPerElement() {
        service.nextListResponse = Flux.just(
                new SdkDto(UUID.randomUUID(), "a"),
                new SdkDto(UUID.randomUUID(), "b"));

        StepVerifier.create(service.list(ReferenceDataFilter.builder().build()))
                .assertNext(d -> assertThat(d.value()).isEqualTo("a"))
                .assertNext(d -> assertThat(d.value()).isEqualTo("b"))
                .verifyComplete();

        assertThat(mapper.toDomainCalls).isEqualTo(2);
    }

    // --- test doubles -------------------------------------------------------

    private record DomainDto(String value) {
    }

    private record SdkDto(UUID id, String value) {
    }

    private static class FakeApi {
    }

    private static class FakeMapper implements ReferenceDataMapper<DomainDto, SdkDto> {
        int toSdkCalls = 0;
        int toDomainCalls = 0;

        @Override
        public SdkDto toSdk(DomainDto d) {
            toSdkCalls++;
            return new SdkDto(null, d.value());
        }

        @Override
        public DomainDto toDomain(SdkDto s) {
            toDomainCalls++;
            return new DomainDto(s.value());
        }
    }

    private static class FakeService extends AbstractReferenceDataService<FakeApi, SdkDto, DomainDto, UUID> {
        AtomicReference<String> lastKey = new AtomicReference<>();
        SdkDto nextCreateResponse;
        SdkDto nextUpdateResponse;
        SdkDto nextGetResponse;
        Throwable nextGetError;
        Flux<SdkDto> nextListResponse = Flux.empty();

        FakeService(FakeApi api, ReferenceDataMapper<DomainDto, SdkDto> mapper) {
            super(api, mapper);
        }

        String lastIdempotencyKey;

        @Override
        public ReferenceDataEntityType entityType() {
            return ReferenceDataEntityType.COUNTRY;
        }

        @Override
        protected Mono<SdkDto> sdkCreate(SdkDto payload, String idempotencyKey) {
            lastIdempotencyKey = idempotencyKey;
            return Mono.just(nextCreateResponse);
        }

        @Override
        protected Mono<SdkDto> sdkUpdate(UUID id, SdkDto payload, String idempotencyKey) {
            lastIdempotencyKey = idempotencyKey;
            return Mono.just(nextUpdateResponse);
        }

        @Override
        protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
            lastIdempotencyKey = idempotencyKey;
            return Mono.empty();
        }

        @Override
        protected Mono<SdkDto> sdkGetById(UUID id) {
            if (nextGetError != null) {
                return Mono.error(nextGetError);
            }
            return Mono.just(nextGetResponse);
        }

        @Override
        protected Flux<SdkDto> sdkList(ReferenceDataFilter filter) {
            return nextListResponse;
        }

        @Override
        protected UUID extractId(SdkDto response) {
            return response.id();
        }
    }
}
