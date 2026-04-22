package com.firefly.domain.configuration.core.config.reference.relationship;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.RelationshipTypeMasterApi;
import com.firefly.common.reference.master.data.sdk.model.RelationshipTypeMasterDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import com.firefly.domain.configuration.interfaces.rest.reference.RelationshipTypeDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class RelationshipTypeConfigService
        extends AbstractReferenceDataService<RelationshipTypeMasterApi, RelationshipTypeMasterDTO, RelationshipTypeDto, UUID> {

    private final ObjectMapper objectMapper;

    public RelationshipTypeConfigService(RelationshipTypeMasterApi api,
                                         RelationshipTypeMapper mapper,
                                         ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.RELATIONSHIP_TYPE;
    }

    @Override
    protected Mono<RelationshipTypeMasterDTO> sdkCreate(RelationshipTypeMasterDTO payload, String idempotencyKey) {
        return api.createRelationshipType(payload, idempotencyKey);
    }

    @Override
    protected Mono<RelationshipTypeMasterDTO> sdkUpdate(UUID id, RelationshipTypeMasterDTO payload, String idempotencyKey) {
        return api.updateRelationshipType(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteRelationshipType(id, idempotencyKey);
    }

    @Override
    protected Mono<RelationshipTypeMasterDTO> sdkGetById(UUID id) {
        return api.getRelationshipType(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<RelationshipTypeMasterDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listRelationshipTypes(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, RelationshipTypeMasterDTO.class));
    }

    @Override
    protected UUID extractId(RelationshipTypeMasterDTO response) {
        return response.getRelationshipTypeId();
    }
}
