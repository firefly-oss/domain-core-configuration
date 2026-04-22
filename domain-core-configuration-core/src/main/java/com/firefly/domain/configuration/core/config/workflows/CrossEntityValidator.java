package com.firefly.domain.configuration.core.config.workflows;

import com.firefly.core.product.sdk.api.ProductConfigurationApi;
import com.firefly.core.product.sdk.model.FilterOptions;
import com.firefly.core.product.sdk.model.FilterRequestProductDTO;
import com.firefly.core.product.sdk.model.PaginationRequest;
import com.firefly.core.product.sdk.api.ProductApi;
import com.firefly.core.product.sdk.model.PaginationResponse;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Cross-catalog precondition checks invoked by {@code ActivateReferenceEntitySaga}
 * before a master-data entity is deactivated. Implemented as a best-effort scan
 * of the product catalog — admin operations accept the latency cost.
 *
 * The check returns {@code true} when deactivation is safe (no active references
 * found) and {@code false} when at least one active product still depends on the
 * entity. Entity types without a cross-check defined return {@code true} so the
 * saga treats them as unconditionally deactivatable.
 */
@Slf4j
@Component
public class CrossEntityValidator {

    private static final Set<ReferenceDataEntityType> CROSS_CHECKED =
            Set.of(ReferenceDataEntityType.CURRENCY,
                    ReferenceDataEntityType.LEGAL_FORM,
                    ReferenceDataEntityType.COUNTRY);

    private static final int SCAN_PAGE_SIZE = 200;

    private final ProductConfigurationApi productConfigurationApi;
    private final ProductApi productApi;

    public CrossEntityValidator(ProductConfigurationApi productConfigurationApi,
                                ProductApi productApi) {
        this.productConfigurationApi = productConfigurationApi;
        this.productApi = productApi;
    }

    public boolean hasCrossCheck(ReferenceDataEntityType type) {
        return CROSS_CHECKED.contains(type);
    }

    /**
     * @return {@code true} when no active product references the given entity.
     */
    public Mono<Boolean> canDeactivate(ReferenceDataEntityType type, UUID entityId) {
        if (!hasCrossCheck(type)) {
            return Mono.just(true);
        }
        String needle = entityId != null ? entityId.toString() : null;
        if (needle == null) {
            return Mono.just(true);
        }
        FilterRequestProductDTO request = new FilterRequestProductDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(0);
        pagination.setPageSize(SCAN_PAGE_SIZE);
        request.setPagination(pagination);
        FilterOptions options = new FilterOptions();
        request.setOptions(options);
        return productApi.filterProducts(request, UUID.randomUUID().toString())
                .flatMap(response -> scanProducts(response, needle))
                .defaultIfEmpty(true);
    }

    private Mono<Boolean> scanProducts(PaginationResponse response, String needle) {
        List<Object> content = response.getContent();
        if (content == null || content.isEmpty()) {
            return Mono.just(true);
        }
        return reactor.core.publisher.Flux.fromIterable(content)
                .concatMap(raw -> extractProductId(raw)
                        .flatMap(productId -> productHasReference(productId, needle)))
                .any(hasRef -> hasRef)
                .map(hasRef -> !hasRef);
    }

    private Mono<UUID> extractProductId(Object raw) {
        if (raw == null) {
            return Mono.empty();
        }
        try {
            java.lang.reflect.Method getter = raw.getClass().getMethod("getProductId");
            Object value = getter.invoke(raw);
            if (value instanceof UUID uuid) {
                return Mono.just(uuid);
            }
            if (value instanceof String str) {
                return Mono.just(UUID.fromString(str));
            }
        } catch (ReflectiveOperationException | IllegalArgumentException ex) {
            log.debug("Unable to extract productId from {}: {}", raw.getClass().getSimpleName(), ex.getMessage());
        }
        return Mono.empty();
    }

    private Mono<Boolean> productHasReference(UUID productId, String needle) {
        com.firefly.core.product.sdk.model.FilterRequestProductConfigurationDTO configFilter =
                new com.firefly.core.product.sdk.model.FilterRequestProductConfigurationDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(0);
        pagination.setPageSize(SCAN_PAGE_SIZE);
        configFilter.setPagination(pagination);
        return productConfigurationApi.filterConfigurations(productId, configFilter, UUID.randomUUID().toString())
                .map(response -> configsContainNeedle(response, needle))
                .defaultIfEmpty(false)
                .onErrorResume(ex -> {
                    log.error("cross-check.scan.failed productId={} needle={} cause={} — assuming reference exists to avoid unsafe deactivation",
                            productId, needle, ex.getMessage());
                    return Mono.just(true);
                });
    }

    private boolean configsContainNeedle(PaginationResponse response, String needle) {
        List<Object> content = response.getContent();
        if (content == null || content.isEmpty()) {
            return false;
        }
        for (Object raw : content) {
            if (raw != null && raw.toString().contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
