package com.firefly.domain.configuration.core.config.reference.geography;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.AdministrativeDivisionsApi;
import com.firefly.common.reference.master.data.sdk.model.AdministrativeDivisionDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.AdministrativeDivisionDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AdministrativeDivisionConfigService
        extends AbstractReferenceDataService<AdministrativeDivisionsApi, AdministrativeDivisionDTO, AdministrativeDivisionDto, UUID> {

    private final ObjectMapper objectMapper;

    public AdministrativeDivisionConfigService(AdministrativeDivisionsApi api,
                                               AdministrativeDivisionMapper mapper,
                                               ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.ADMINISTRATIVE_DIVISION;
    }

    @Override
    protected Mono<AdministrativeDivisionDTO> sdkCreate(AdministrativeDivisionDTO payload, String idempotencyKey) {
        return api.createDivision(payload, idempotencyKey);
    }

    @Override
    protected Mono<AdministrativeDivisionDTO> sdkUpdate(UUID id, AdministrativeDivisionDTO payload, String idempotencyKey) {
        return api.updateDivision(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteDivision(id, idempotencyKey);
    }

    @Override
    protected Mono<AdministrativeDivisionDTO> sdkGetById(UUID id) {
        return api.getDivision(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<AdministrativeDivisionDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listDivisions(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, AdministrativeDivisionDTO.class));
    }

    @Override
    protected UUID extractId(AdministrativeDivisionDTO response) {
        return response.getDivisionId();
    }
}
