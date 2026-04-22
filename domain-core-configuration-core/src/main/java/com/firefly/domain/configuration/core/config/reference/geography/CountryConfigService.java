package com.firefly.domain.configuration.core.config.reference.geography;

import com.firefly.common.reference.master.data.sdk.api.CountriesApi;
import com.firefly.common.reference.master.data.sdk.model.CountryDTO;
import com.firefly.common.reference.master.data.sdk.model.FilterRequestCountryDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationRequest;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.CountryConfigDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Service
public class CountryConfigService
        extends AbstractReferenceDataService<CountriesApi, CountryDTO, CountryConfigDto, UUID> {

    public CountryConfigService(CountriesApi api, CountryMapper mapper) {
        super(api, mapper);
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.COUNTRY;
    }

    @Override
    protected Mono<CountryDTO> sdkCreate(CountryDTO payload, String idempotencyKey) {
        return api.createCountry(payload, idempotencyKey);
    }

    @Override
    protected Mono<CountryDTO> sdkUpdate(UUID id, CountryDTO payload, String idempotencyKey) {
        return api.updateCountry(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteCountry(id, idempotencyKey);
    }

    @Override
    protected Mono<CountryDTO> sdkGetById(UUID id) {
        return api.getCountry(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<CountryDTO> sdkList(ReferenceDataFilter filter) {
        FilterRequestCountryDTO request = new FilterRequestCountryDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(filter.getPage() != null ? filter.getPage() : 0);
        pagination.setPageSize(filter.getSize() != null ? filter.getSize() : 100);
        request.setPagination(pagination);
        return api.filterCountries(request, UUID.randomUUID().toString())
                .flatMapIterable(resp -> resp.getContent() != null ? resp.getContent() : Collections.emptyList());
    }

    @Override
    protected UUID extractId(CountryDTO response) {
        return response.getCountryId();
    }
}
