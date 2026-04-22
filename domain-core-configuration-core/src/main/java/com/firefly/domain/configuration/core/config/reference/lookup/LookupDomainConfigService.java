package com.firefly.domain.configuration.core.config.reference.lookup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.LookupDomainsApi;
import com.firefly.common.reference.master.data.sdk.model.LookupDomainDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.LookupDomainDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class LookupDomainConfigService
        extends AbstractReferenceDataService<LookupDomainsApi, LookupDomainDTO, LookupDomainDto, UUID> {

    private final ObjectMapper objectMapper;

    public LookupDomainConfigService(LookupDomainsApi api, LookupDomainMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.LOOKUP_DOMAIN; }
    @Override protected Mono<LookupDomainDTO> sdkCreate(LookupDomainDTO p, String k) { return api.createDomain(p, k); }
    @Override protected Mono<LookupDomainDTO> sdkUpdate(UUID id, LookupDomainDTO p, String k) { return api.updateDomain(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteDomain(id, k); }
    @Override protected Mono<LookupDomainDTO> sdkGetById(UUID id) { return api.getDomain(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<LookupDomainDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listDomains(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, LookupDomainDTO.class));
    }

    @Override protected UUID extractId(LookupDomainDTO r) { return r.getDomainId(); }
}
