package com.firefly.domain.configuration.core.config.reference.currency;

import com.firefly.common.reference.master.data.sdk.api.CurrenciesApi;
import com.firefly.common.reference.master.data.sdk.model.CurrencyDTO;
import com.firefly.common.reference.master.data.sdk.model.FilterRequestCurrencyDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationRequest;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.CurrencyConfigDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Service
public class CurrencyConfigService
        extends AbstractReferenceDataService<CurrenciesApi, CurrencyDTO, CurrencyConfigDto, UUID> {

    public CurrencyConfigService(CurrenciesApi api, CurrencyMapper mapper) {
        super(api, mapper);
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.CURRENCY;
    }

    @Override
    protected Mono<CurrencyDTO> sdkCreate(CurrencyDTO payload, String idempotencyKey) {
        return api.createCurrency(payload, idempotencyKey);
    }

    @Override
    protected Mono<CurrencyDTO> sdkUpdate(UUID id, CurrencyDTO payload, String idempotencyKey) {
        return api.updateCurrency(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteCurrency(id, idempotencyKey);
    }

    @Override
    protected Mono<CurrencyDTO> sdkGetById(UUID id) {
        return api.getCurrency(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<CurrencyDTO> sdkList(ReferenceDataFilter filter) {
        FilterRequestCurrencyDTO request = new FilterRequestCurrencyDTO();
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPageNumber(filter.getPage() != null ? filter.getPage() : 0);
        pagination.setPageSize(filter.getSize() != null ? filter.getSize() : 100);
        request.setPagination(pagination);
        return api.filterCurrencies(request, UUID.randomUUID().toString())
                .flatMapIterable(resp -> resp.getContent() != null ? resp.getContent() : Collections.emptyList());
    }

    @Override
    protected UUID extractId(CurrencyDTO response) {
        return response.getCurrencyId();
    }
}
