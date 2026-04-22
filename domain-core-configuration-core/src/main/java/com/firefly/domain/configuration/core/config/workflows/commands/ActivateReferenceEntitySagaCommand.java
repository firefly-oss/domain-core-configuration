package com.firefly.domain.configuration.core.config.workflows.commands;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Saga-scoped command for activating or deactivating a reference-data entity.
 * Cross-entity preconditions are evaluated before the status change is applied.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivateReferenceEntitySagaCommand implements Command<UUID> {

    public enum TargetStatus {
        ACTIVE,
        INACTIVE
    }

    private ReferenceDataEntityType entityType;

    private UUID entityId;

    private TargetStatus targetStatus;
}
