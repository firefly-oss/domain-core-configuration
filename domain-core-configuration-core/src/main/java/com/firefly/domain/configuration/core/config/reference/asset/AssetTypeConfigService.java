package com.firefly.domain.configuration.core.config.reference.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.AssetTypeApi;
import com.firefly.common.reference.master.data.sdk.model.AssetTypeDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.AssetTypeDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AssetTypeConfigService
        extends AbstractReferenceDataService<AssetTypeApi, AssetTypeDTO, AssetTypeDto, UUID> {

    private final ObjectMapper objectMapper;

    public AssetTypeConfigService(AssetTypeApi api, AssetTypeMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.ASSET_TYPE; }
    @Override protected Mono<AssetTypeDTO> sdkCreate(AssetTypeDTO p, String k) { return api.createAssetType(p, k); }
    @Override protected Mono<AssetTypeDTO> sdkUpdate(UUID id, AssetTypeDTO p, String k) { return api.updateAssetType(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteAssetType(id, k); }
    @Override protected Mono<AssetTypeDTO> sdkGetById(UUID id) { return api.getAssetType(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<AssetTypeDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listAssetTypes(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, AssetTypeDTO.class));
    }

    @Override protected UUID extractId(AssetTypeDTO r) { return r.getAssetId(); }
}
