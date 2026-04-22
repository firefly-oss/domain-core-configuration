package com.firefly.domain.configuration.core.config.workflows.commands;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Compensation command that reapplies the previously-captured DTO of an entity.
 * The payload is opaque because it was captured as an {@code Object} snapshot by
 * the preceding {@code CaptureEntitySnapshotSagaCommand} step.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReapplyPreviousStateSagaCommand implements Command<Void> {

    private ReferenceDataEntityType entityType;

    private UUID entityId;

    private Object previousState;
}
