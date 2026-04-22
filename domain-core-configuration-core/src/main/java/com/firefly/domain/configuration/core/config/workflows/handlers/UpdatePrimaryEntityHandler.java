package com.firefly.domain.configuration.core.config.workflows.handlers;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataValidationException;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateReferenceDataSagaCommand;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Invokes the registry-resolved {@link ReferenceDataService} to mutate the target
 * entity. The {@code payload} carried in the command is cast to the concrete domain
 * DTO type; a type mismatch surfaces as {@link ReferenceDataValidationException}
 * so the saga framework fails fast with a 400 semantic.
 */
@Slf4j
@CommandHandlerComponent
public class UpdatePrimaryEntityHandler
        extends CommandHandler<UpdateReferenceDataSagaCommand, UUID> {

    private final ReferenceDataRegistry registry;

    public UpdatePrimaryEntityHandler(ReferenceDataRegistry registry) {
        this.registry = registry;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Mono<UUID> doHandle(UpdateReferenceDataSagaCommand cmd) {
        ReferenceDataService service = registry.resolve(cmd.getEntityType());
        Object payload = cmd.getPayload();
        if (payload == null) {
            return Mono.error(new ReferenceDataValidationException(
                    "Missing payload for " + cmd.getEntityType() + " update"));
        }
        log.info("Updating primary entity entityType={} entityId={}", cmd.getEntityType(), cmd.getEntityId());
        Mono<Object> updated = service.update(cmd.getEntityId(), payload);
        return updated
                .thenReturn(cmd.getEntityId())
                .onErrorMap(ClassCastException.class, ex -> new ReferenceDataValidationException(
                        "Payload type mismatch for " + cmd.getEntityType() + ": " + ex.getMessage(), ex));
    }
}
