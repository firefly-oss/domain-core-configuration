package com.firefly.domain.configuration.core.config.reference.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.TransactionCategoryCatalogApi;
import com.firefly.common.reference.master.data.sdk.model.TransactionCategoryCatalogDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import com.firefly.domain.configuration.interfaces.rest.reference.TransactionCategoryDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionCategoryConfigService
        extends AbstractReferenceDataService<TransactionCategoryCatalogApi, TransactionCategoryCatalogDTO, TransactionCategoryDto, UUID> {

    private final ObjectMapper objectMapper;

    public TransactionCategoryConfigService(TransactionCategoryCatalogApi api,
                                            TransactionCategoryMapper mapper,
                                            ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override public ReferenceDataEntityType entityType() { return ReferenceDataEntityType.TRANSACTION_CATEGORY; }
    @Override protected Mono<TransactionCategoryCatalogDTO> sdkCreate(TransactionCategoryCatalogDTO p, String k) { return api.createTransactionCategory(p, k); }
    @Override protected Mono<TransactionCategoryCatalogDTO> sdkUpdate(UUID id, TransactionCategoryCatalogDTO p, String k) { return api.updateTransactionCategory(id, p, k); }
    @Override protected Mono<Void> sdkDelete(UUID id, String k) { return api.deleteTransactionCategory(id, k); }
    @Override protected Mono<TransactionCategoryCatalogDTO> sdkGetById(UUID id) { return api.getTransactionCategory(id, UUID.randomUUID().toString()); }

    @Override
    protected Flux<TransactionCategoryCatalogDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listTransactionCategories(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, TransactionCategoryCatalogDTO.class));
    }

    @Override protected UUID extractId(TransactionCategoryCatalogDTO r) { return r.getCategoryId(); }
}
