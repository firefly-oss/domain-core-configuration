package com.firefly.domain.configuration.core.config.workflows.commands;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Reads the current state of a reference-data entity so later saga steps can use
 * it as a compensation baseline. The handler returns an opaque {@code Object}
 * because the concrete DTO type depends on the resolved service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptureEntitySnapshotSagaCommand implements Command<Object> {

    private ReferenceDataEntityType entityType;

    private UUID entityId;
}
