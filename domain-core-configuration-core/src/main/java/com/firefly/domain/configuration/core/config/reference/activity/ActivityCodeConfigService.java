package com.firefly.domain.configuration.core.config.reference.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.ActivityCodesApi;
import com.firefly.common.reference.master.data.sdk.model.ActivityCodeDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ActivityCodeDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityCodeConfigService
        extends AbstractReferenceDataService<ActivityCodesApi, ActivityCodeDTO, ActivityCodeDto, UUID> {

    private final ObjectMapper objectMapper;

    public ActivityCodeConfigService(ActivityCodesApi api, ActivityCodeMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.ACTIVITY_CODE; }
    @Override protected Mono<ActivityCodeDTO> sdkCreate(ActivityCodeDTO p, String k) { return api.createActivityCode(p, k); }
    @Override protected Mono<ActivityCodeDTO> sdkUpdate(UUID id, ActivityCodeDTO p, String k) { return api.updateActivityCode(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteActivityCode(id, k); }
    @Override protected Mono<ActivityCodeDTO> sdkGetById(UUID id) { return api.getActivityCode(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<ActivityCodeDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listActivityCodes(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, ActivityCodeDTO.class));
    }

    @Override protected UUID extractId(ActivityCodeDTO r) { return r.getActivityCodeId(); }
}
