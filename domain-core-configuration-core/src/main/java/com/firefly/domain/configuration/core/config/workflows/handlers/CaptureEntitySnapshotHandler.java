package com.firefly.domain.configuration.core.config.workflows.handlers;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataNotFoundException;
import com.firefly.domain.configuration.core.config.workflows.commands.CaptureEntitySnapshotSagaCommand;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

/**
 * Resolves the concrete {@link ReferenceDataService} via the registry and fetches
 * the current DTO of the target entity. Used by both sagas to capture a baseline
 * snapshot for compensation. An empty result from the SDK is mapped to a
 * {@link ReferenceDataNotFoundException} so the saga step fails explicitly —
 * an empty {@code Mono} would be treated as a step failure with no surfaceable
 * cause.
 */
@Slf4j
@CommandHandlerComponent
public class CaptureEntitySnapshotHandler
        extends CommandHandler<CaptureEntitySnapshotSagaCommand, Object> {

    private final ReferenceDataRegistry registry;

    public CaptureEntitySnapshotHandler(ReferenceDataRegistry registry) {
        this.registry = registry;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Mono<Object> doHandle(CaptureEntitySnapshotSagaCommand cmd) {
        ReferenceDataService service = registry.resolve(cmd.getEntityType());
        log.info("Capturing snapshot entityType={} entityId={}", cmd.getEntityType(), cmd.getEntityId());
        return service.getById(cmd.getEntityId())
                .map(dto -> (Object) dto)
                .switchIfEmpty(Mono.error(new ReferenceDataNotFoundException(
                        cmd.getEntityType() + " not found: " + cmd.getEntityId())));
    }
}
