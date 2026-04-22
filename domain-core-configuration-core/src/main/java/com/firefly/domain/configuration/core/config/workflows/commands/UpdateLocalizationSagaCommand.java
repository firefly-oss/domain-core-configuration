package com.firefly.domain.configuration.core.config.workflows.commands;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.LocalizationPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Saga-scoped command that updates a single locale variant of a reference-data
 * entity. Dispatched once per {@link LocalizationPayload} by the
 * {@code updateLocalizations} step of {@code UpdateReferenceDataSaga}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocalizationSagaCommand implements Command<UUID> {

    private ReferenceDataEntityType entityType;

    private UUID entityId;

    private LocalizationPayload payload;
}
