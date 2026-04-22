package com.firefly.domain.configuration.core.config.workflows.handlers;

import com.firefly.domain.configuration.core.config.workflows.commands.UpdateLocalizationSagaCommand;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Dispatches a single locale update for a reference-data entity.
 *
 * The master-data reference services do not currently expose per-locale endpoints
 * in the core SDK, so this handler acts as a best-effort stub: it logs the
 * intent and returns a deterministic placeholder identifier derived from the
 * payload locale. Once the core layer exposes dedicated localization APIs, this
 * handler becomes the integration point without touching the surrounding saga.
 */
@Slf4j
@CommandHandlerComponent
public class UpdateLocalizationHandler
        extends CommandHandler<UpdateLocalizationSagaCommand, UUID> {

    @Override
    protected Mono<UUID> doHandle(UpdateLocalizationSagaCommand cmd) {
        log.info("Recording localization update entityType={} entityId={} locale={}",
                cmd.getEntityType(),
                cmd.getEntityId(),
                cmd.getPayload() != null ? cmd.getPayload().getLocaleTag() : null);
        UUID id = cmd.getPayload() != null && cmd.getPayload().getLocalizationId() != null
                ? cmd.getPayload().getLocalizationId()
                : UUID.randomUUID();
        return Mono.just(id);
    }
}
