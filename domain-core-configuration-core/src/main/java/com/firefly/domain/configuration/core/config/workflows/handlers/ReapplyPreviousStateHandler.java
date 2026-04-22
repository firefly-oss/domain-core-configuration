package com.firefly.domain.configuration.core.config.workflows.handlers;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataService;
import com.firefly.domain.configuration.core.config.workflows.commands.ReapplyPreviousStateSagaCommand;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

/**
 * Compensation handler that reapplies a previously-captured snapshot DTO to the
 * target entity. A null {@code previousState} is logged and treated as a no-op
 * because there is nothing to roll back.
 */
@Slf4j
@CommandHandlerComponent
public class ReapplyPreviousStateHandler
        extends CommandHandler<ReapplyPreviousStateSagaCommand, Void> {

    private final ReferenceDataRegistry registry;

    public ReapplyPreviousStateHandler(ReferenceDataRegistry registry) {
        this.registry = registry;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Mono<Void> doHandle(ReapplyPreviousStateSagaCommand cmd) {
        if (cmd.getPreviousState() == null) {
            log.warn("ReapplyPreviousStateHandler invoked with null snapshot entityType={} entityId={}",
                    cmd.getEntityType(), cmd.getEntityId());
            return Mono.empty();
        }
        ReferenceDataService service = registry.resolve(cmd.getEntityType());
        log.info("Reapplying previous state entityType={} entityId={}", cmd.getEntityType(), cmd.getEntityId());
        return service.update(cmd.getEntityId(), cmd.getPreviousState())
                .then();
    }
}
