package com.firefly.domain.configuration.core.config.workflows.commands;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.LocalizationPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.List;
import java.util.UUID;

/**
 * Saga-scoped command used to atomically update a reference-data entity and its
 * dependent localizations. Carries an opaque {@code payload} that is cast to the
 * concrete domain DTO type at the handler layer via the registry-resolved service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReferenceDataSagaCommand implements Command<UUID> {

    private ReferenceDataEntityType entityType;

    private UUID entityId;

    private Object payload;

    private List<LocalizationPayload> localizations;
}
