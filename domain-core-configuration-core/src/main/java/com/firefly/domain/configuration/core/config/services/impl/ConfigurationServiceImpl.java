package com.firefly.domain.configuration.core.config.services.impl;

import com.firefly.common.reference.master.data.sdk.model.CountryDTO;
import com.firefly.common.reference.master.data.sdk.model.CurrencyDTO;
import com.firefly.common.reference.master.data.sdk.model.LegalFormDTO;
import com.firefly.domain.configuration.core.config.queries.CountryQuery;
import com.firefly.domain.configuration.core.config.queries.CurrencyQuery;
import com.firefly.domain.configuration.core.config.queries.LegalFormQuery;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataRegistry;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.exception.ReferenceDataValidationException;
import com.firefly.domain.configuration.core.config.services.ConfigurationService;
import com.firefly.domain.configuration.core.config.workflows.commands.ActivateReferenceEntitySagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.CaptureEntitySnapshotSagaCommand;
import com.firefly.domain.configuration.core.config.workflows.commands.UpdateReferenceDataSagaCommand;
import com.firefly.domain.configuration.interfaces.rest.InitConfigResponse;
import com.firefly.domain.configuration.interfaces.rest.TenantSnapshotResponse;
import com.firefly.domain.configuration.interfaces.rest.reference.CountryConfigDto;
import com.firefly.domain.configuration.interfaces.rest.reference.CurrencyConfigDto;
import com.firefly.domain.configuration.interfaces.rest.reference.LanguageLocaleDto;
import com.firefly.domain.configuration.interfaces.rest.reference.LegalFormConfigDto;
import com.firefly.domain.configuration.interfaces.rest.reference.LookupItemDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.fireflyframework.cqrs.query.QueryBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static com.firefly.domain.configuration.core.utils.constants.ReferenceDataConstants.*;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    private final QueryBus queryBus;
    private final SagaEngine sagaEngine;
    private final ReferenceDataRegistry registry;

    @Autowired
    public ConfigurationServiceImpl(QueryBus queryBus,
                                    SagaEngine sagaEngine,
                                    ReferenceDataRegistry registry) {
        this.queryBus = queryBus;
        this.sagaEngine = sagaEngine;
        this.registry = registry;
    }

    @Override
    public Mono<InitConfigResponse> getInitConfiguration() {
        Mono<List<CountryDTO>> countries = queryBus.query(CountryQuery.builder().build());
        Mono<List<CurrencyDTO>> currencies = queryBus.query(CurrencyQuery.builder().build());
        Mono<List<LegalFormDTO>> legalForms = queryBus.query(LegalFormQuery.builder().build());

        return Mono.zip(countries, currencies, legalForms)
                .map(t -> InitConfigResponse.builder()
                        .countries(t.getT1())
                        .currencies(t.getT2())
                        .legalForms(t.getT3())
                        .build());
    }

    @Override
    public Mono<UUID> updateReferenceData(UpdateReferenceDataSagaCommand cmd) {
        return Mono.defer(() -> {
            if (cmd == null || cmd.getEntityType() == null || cmd.getEntityId() == null) {
                return Mono.error(new ReferenceDataValidationException(
                        "updateReferenceData requires entityType and entityId"));
            }
            if (!registry.contains(cmd.getEntityType())) {
                return Mono.error(new ReferenceDataValidationException(
                        "Unsupported entity type: " + cmd.getEntityType()));
            }
            StepInputs inputs = StepInputs.builder()
                    .forStepId(STEP_CAPTURE_SNAPSHOT, cmd)
                    .forStepId(STEP_UPDATE_PRIMARY_ENTITY, cmd)
                    .forStepId(STEP_UPDATE_LOCALIZATIONS, cmd)
                    .build();
            ExecutionContext ctx = ExecutionContext.forSaga(UUID.randomUUID().toString(), SAGA_UPDATE_REFERENCE_DATA);
            return sagaEngine.execute(SAGA_UPDATE_REFERENCE_DATA, inputs, ctx)
                    .map(result -> cmd.getEntityId());
        });
    }

    @Override
    public Mono<UUID> activateReferenceEntity(ActivateReferenceEntitySagaCommand cmd) {
        return Mono.defer(() -> {
            if (cmd == null || cmd.getEntityType() == null || cmd.getEntityId() == null || cmd.getTargetStatus() == null) {
                return Mono.error(new ReferenceDataValidationException(
                        "activateReferenceEntity requires entityType, entityId and targetStatus"));
            }
            if (!registry.contains(cmd.getEntityType())) {
                return Mono.error(new ReferenceDataValidationException(
                        "Unsupported entity type: " + cmd.getEntityType()));
            }
            StepInputs inputs = StepInputs.builder()
                    .forStepId(STEP_VALIDATE_CROSS_ENTITY, cmd)
                    .forStepId(STEP_CAPTURE_SNAPSHOT,
                            new CaptureEntitySnapshotSagaCommand(cmd.getEntityType(), cmd.getEntityId()))
                    .forStepId(STEP_APPLY_STATUS_CHANGE, cmd)
                    .build();
            ExecutionContext ctx = ExecutionContext.forSaga(UUID.randomUUID().toString(), SAGA_ACTIVATE_REFERENCE_ENTITY);
            return sagaEngine.execute(SAGA_ACTIVATE_REFERENCE_ENTITY, inputs, ctx)
                    .map(result -> cmd.getEntityId());
        });
    }

    @Override
    public ReferenceDataService<?, ?> getReferenceDataService(ReferenceDataEntityType type) {
        return registry.resolve(type);
    }

    @Override
    public Mono<TenantSnapshotResponse> getTenantSnapshot(UUID tenantId) {
        ReferenceDataFilter filter = ReferenceDataFilter.builder()
                .tenantId(tenantId)
                .status("ACTIVE")
                .build();
        Mono<List<CountryConfigDto>> countries = listIfAvailable(ReferenceDataEntityType.COUNTRY, filter);
        Mono<List<CurrencyConfigDto>> currencies = listIfAvailable(ReferenceDataEntityType.CURRENCY, filter);
        Mono<List<LegalFormConfigDto>> legalForms = listIfAvailable(ReferenceDataEntityType.LEGAL_FORM, filter);
        Mono<List<LanguageLocaleDto>> languageLocales = listIfAvailable(ReferenceDataEntityType.LANGUAGE_LOCALE, filter);
        Mono<List<LookupItemDto>> lookupItems = listIfAvailable(ReferenceDataEntityType.LOOKUP_ITEM, filter);

        return Mono.zip(countries, currencies, legalForms, languageLocales, lookupItems)
                .map(tuple -> TenantSnapshotResponse.builder()
                        .tenantId(tenantId)
                        .countries(tuple.getT1())
                        .currencies(tuple.getT2())
                        .legalForms(tuple.getT3())
                        .languageLocales(tuple.getT4())
                        .lookupItems(tuple.getT5())
                        .build());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> Mono<List<T>> listIfAvailable(ReferenceDataEntityType type, ReferenceDataFilter filter) {
        if (!registry.contains(type)) {
            return Mono.just(List.of());
        }
        ReferenceDataService service = registry.resolve(type);
        return ((Mono) service.list(filter).collectList()).map(list -> (List<T>) list);
    }
}
