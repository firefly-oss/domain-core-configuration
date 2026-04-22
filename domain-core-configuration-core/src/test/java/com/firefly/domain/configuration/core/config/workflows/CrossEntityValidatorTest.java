package com.firefly.domain.configuration.core.config.workflows;

import com.firefly.core.product.sdk.api.ProductApi;
import com.firefly.core.product.sdk.api.ProductConfigurationApi;
import com.firefly.core.product.sdk.model.PaginationResponse;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CrossEntityValidatorTest {

    private ProductApi productApi;
    private ProductConfigurationApi productConfigurationApi;
    private CrossEntityValidator validator;

    @BeforeEach
    void setUp() {
        productApi = mock(ProductApi.class);
        productConfigurationApi = mock(ProductConfigurationApi.class);
        validator = new CrossEntityValidator(productConfigurationApi, productApi);
    }

    @Test
    void hasCrossCheck_reportsCurrencyLegalFormAndCountry() {
        assertThat(validator.hasCrossCheck(ReferenceDataEntityType.CURRENCY)).isTrue();
        assertThat(validator.hasCrossCheck(ReferenceDataEntityType.LEGAL_FORM)).isTrue();
        assertThat(validator.hasCrossCheck(ReferenceDataEntityType.COUNTRY)).isTrue();
        assertThat(validator.hasCrossCheck(ReferenceDataEntityType.BANK)).isFalse();
        assertThat(validator.hasCrossCheck(ReferenceDataEntityType.TITLE)).isFalse();
    }

    @Test
    void canDeactivate_returnsTrue_forEntityTypeWithoutCrossCheck() {
        StepVerifier.create(validator.canDeactivate(ReferenceDataEntityType.TITLE, UUID.randomUUID()))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void canDeactivate_returnsTrue_whenNoProductsExist() {
        PaginationResponse empty = new PaginationResponse();
        empty.setContent(List.of());
        when(productApi.filterProducts(any(), any())).thenReturn(Mono.just(empty));

        StepVerifier.create(validator.canDeactivate(ReferenceDataEntityType.CURRENCY, UUID.randomUUID()))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void canDeactivate_returnsFalse_whenProductConfigReferencesTheEntityId() {
        UUID currencyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        PaginationResponse productsPage = new PaginationResponse();
        productsPage.setContent(List.<Object>of(new FakeProduct(productId)));
        when(productApi.filterProducts(any(), any())).thenReturn(Mono.just(productsPage));

        PaginationResponse configsPage = new PaginationResponse();
        configsPage.setContent(List.<Object>of("config-referencing=" + currencyId));
        when(productConfigurationApi.filterConfigurations(eq(productId), any(), any()))
                .thenReturn(Mono.just(configsPage));

        StepVerifier.create(validator.canDeactivate(ReferenceDataEntityType.CURRENCY, currencyId))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void canDeactivate_returnsTrue_whenConfigsDoNotReferenceTheEntityId() {
        UUID productId = UUID.randomUUID();
        PaginationResponse productsPage = new PaginationResponse();
        productsPage.setContent(List.<Object>of(new FakeProduct(productId)));
        when(productApi.filterProducts(any(), any())).thenReturn(Mono.just(productsPage));

        PaginationResponse configsPage = new PaginationResponse();
        configsPage.setContent(List.<Object>of("config-referencing=other-id"));
        when(productConfigurationApi.filterConfigurations(eq(productId), any(), any()))
                .thenReturn(Mono.just(configsPage));

        StepVerifier.create(validator.canDeactivate(ReferenceDataEntityType.CURRENCY, UUID.randomUUID()))
                .expectNext(true)
                .verifyComplete();
    }

    public static class FakeProduct {
        private final UUID productId;

        public FakeProduct(UUID productId) {
            this.productId = productId;
        }

        public UUID getProductId() {
            return productId;
        }
    }
}
