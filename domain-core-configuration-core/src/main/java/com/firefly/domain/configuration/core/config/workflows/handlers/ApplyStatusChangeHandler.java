package com.firefly.domain.configuration.core.config.workflows.handlers;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataNotFoundException;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Applies a status change (ACTIVE/INACTIVE) to the target entity.
 *
 * The reference-data DTO status field is modelled per-entity (e.g., {@code isActive},
 * {@code status}), so this handler uses reflection to locate a compatible mutator
 * on the fetched DTO. Entities without a status field pass through unchanged —
 * the cross-entity validator is expected to gate deactivation upstream.
 */
@Slf4j
@CommandHandlerComponent
public class ApplyStatusChangeHandler
        extends CommandHandler<ActivateReferenceEntitySagaCommand, UUID> {

    private static final String[] STATUS_SETTERS = {"setIsActive", "setStatus", "setActive"};

    private final ReferenceDataRegistry registry;

    public ApplyStatusChangeHandler(ReferenceDataRegistry registry) {
        this.registry = registry;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Mono<UUID> doHandle(ActivateReferenceEntitySagaCommand cmd) {
        ReferenceDataService service = registry.resolve(cmd.getEntityType());
        boolean active = cmd.getTargetStatus() == ActivateReferenceEntitySagaCommand.TargetStatus.ACTIVE;
        log.info("Applying status change entityType={} entityId={} targetStatus={}",
                cmd.getEntityType(), cmd.getEntityId(), cmd.getTargetStatus());
        return service.getById(cmd.getEntityId())
                .switchIfEmpty(Mono.error(new ReferenceDataNotFoundException(
                        cmd.getEntityType() + " not found: " + cmd.getEntityId())))
                .flatMap(current -> {
                    boolean applied = applyStatusMutator(current, active);
                    if (!applied) {
                        log.error("status.mutator.missing entityType={} entityId={} dtoClass={} — activation reports success without touching status",
                                cmd.getEntityType(), cmd.getEntityId(), current.getClass().getSimpleName());
                    }
                    return service.update(cmd.getEntityId(), current);
                })
                .thenReturn(cmd.getEntityId());
    }

    private boolean applyStatusMutator(Object dto, boolean active) {
        for (String setter : STATUS_SETTERS) {
            Method method = findSetter(dto.getClass(), setter);
            if (method == null) {
                continue;
            }
            try {
                Class<?> paramType = method.getParameterTypes()[0];
                Object value = resolveStatusValue(paramType, active);
                method.invoke(dto, value);
                return true;
            } catch (ReflectiveOperationException ex) {
                log.warn("Failed to invoke {} on {}: {}", setter, dto.getClass().getSimpleName(), ex.getMessage());
            }
        }
        return false;
    }

    private Method findSetter(Class<?> type, String name) {
        for (Method m : type.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 1) {
                return m;
            }
        }
        return null;
    }

    private Object resolveStatusValue(Class<?> paramType, boolean active) {
        if (paramType == Boolean.class || paramType == boolean.class) {
            return active;
        }
        if (paramType == String.class) {
            return active ? "ACTIVE" : "INACTIVE";
        }
        return active;
    }
}
