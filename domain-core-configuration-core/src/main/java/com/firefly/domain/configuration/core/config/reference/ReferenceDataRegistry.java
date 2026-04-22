package com.firefly.domain.configuration.core.config.reference;

import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Runtime lookup from {@link ReferenceDataEntityType} to its registered
 * {@link ReferenceDataService} instance. Spring injects the list of every
 * concrete service by component scan; the map is built eagerly at startup.
 */
@Slf4j
@Component
public class ReferenceDataRegistry {

    private final Map<ReferenceDataEntityType, ReferenceDataService<?, ?>> byType;

    public ReferenceDataRegistry(List<ReferenceDataService<?, ?>> services) {
        this.byType = new EnumMap<>(ReferenceDataEntityType.class);
        for (ReferenceDataService<?, ?> service : services) {
            ReferenceDataEntityType type = service.entityType();
            ReferenceDataService<?, ?> previous = byType.put(type, service);
            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate ReferenceDataService for entity type " + type
                                + ": " + previous.getClass() + " vs " + service.getClass());
            }
        }
        log.info("ReferenceDataRegistry initialized with {} services", byType.size());
    }

    /**
     * Returns the concrete service registered for the given entity type.
     *
     * @throws ReferenceDataValidationException when no service is registered
     */
    public ReferenceDataService<?, ?> resolve(ReferenceDataEntityType type) {
        ReferenceDataService<?, ?> service = byType.get(type);
        if (service == null) {
            throw new ReferenceDataValidationException(
                    "No ReferenceDataService registered for entity type " + type);
        }
        return service;
    }

    public int size() {
        return byType.size();
    }

    public boolean contains(ReferenceDataEntityType type) {
        return byType.containsKey(type);
    }
}
