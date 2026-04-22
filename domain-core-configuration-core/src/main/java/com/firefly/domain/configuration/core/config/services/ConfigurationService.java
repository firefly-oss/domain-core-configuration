package com.firefly.domain.configuration.core.config.services;

import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataService;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateReferenceDataSagaCommand;
import com.firefly.domain.configuration.interfaces.rest.InitConfigResponse;
import com.firefly.domain.configuration.interfaces.rest.TenantSnapshotResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConfigurationService {

    Mono<InitConfigResponse> getInitConfiguration();

    Mono<UUID> updateReferenceData(UpdateReferenceDataSagaCommand cmd);

    Mono<UUID> activateReferenceEntity(ActivateReferenceEntitySagaCommand cmd);

    ReferenceDataService<?, ?> getReferenceDataService(ReferenceDataEntityType type);

    Mono<TenantSnapshotResponse> getTenantSnapshot(UUID tenantId);
}
