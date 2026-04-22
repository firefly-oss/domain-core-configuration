package com.firefly.domain.configuration.web.controller.reference.base;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataService;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractReferenceDataAdminControllerTest {

    private ReferenceDataService<TestDto, UUID> service;
    private TestAdminController controller;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        service = mock(ReferenceDataService.class);
        when(service.entityType()).thenReturn(ReferenceDataEntityType.CURRENCY);
        controller = new TestAdminController(service);
    }

    @Test
    void create_returns201WithBodyFromService() {
        UUID id = UUID.randomUUID();
        TestDto dto = new TestDto("isoCode", "name");
        when(service.create(eq(dto))).thenReturn(Mono.just(id));

        StepVerifier.create(controller.create(dto))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(201);
                    assertThat(response.getBody()).isEqualTo(id);
                })
                .verifyComplete();
    }

    @Test
    void update_returns200WithDtoFromService() {
        UUID id = UUID.randomUUID();
        TestDto dto = new TestDto("EUR", "Euro");
        when(service.update(eq(id), eq(dto))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.update(id, dto))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(200);
                    assertThat(response.getBody()).isEqualTo(dto);
                })
                .verifyComplete();
    }

    @Test
    void remove_returns204() {
        UUID id = UUID.randomUUID();
        when(service.remove(eq(id))).thenReturn(Mono.empty());

        StepVerifier.create(controller.remove(id))
                .assertNext(response -> assertThat(response.getStatusCode().value()).isEqualTo(204))
                .verifyComplete();
        verify(service).remove(eq(id));
    }

    @Test
    void getById_returns200WithDto() {
        UUID id = UUID.randomUUID();
        TestDto dto = new TestDto("USD", "Dollar");
        when(service.getById(eq(id))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getById(id))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(200);
                    assertThat(response.getBody()).isEqualTo(dto);
                })
                .verifyComplete();
    }

    @Test
    void list_returnsFluxFromService() {
        TestDto a = new TestDto("EUR", "Euro");
        TestDto b = new TestDto("USD", "Dollar");
        when(service.list(any(ReferenceDataFilter.class))).thenReturn(Flux.just(a, b));

        StepVerifier.create(controller.list(ReferenceDataFilter.builder().build()))
                .expectNext(a)
                .expectNext(b)
                .verifyComplete();
    }

    // --- test scaffolding ----------------------------------------------------

    public static class TestDto {
        public String isoCode;
        public String name;

        public TestDto() {
        }

        public TestDto(String isoCode, String name) {
            this.isoCode = isoCode;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestDto other)) return false;
            return java.util.Objects.equals(isoCode, other.isoCode)
                    && java.util.Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(isoCode, name);
        }
    }

    private static class TestAdminController
            extends AbstractReferenceDataAdminController<TestDto, UUID> {
        TestAdminController(ReferenceDataService<TestDto, UUID> service) {
            super(service);
        }
    }

    @SuppressWarnings("unused")
    private static ResponseEntity<?> assertEmptyBody(ResponseEntity<?> response) {
        assertThat(response.getBody()).isNull();
        return response;
    }
}
