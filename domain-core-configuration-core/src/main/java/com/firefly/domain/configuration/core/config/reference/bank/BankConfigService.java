package com.firefly.domain.configuration.core.config.reference.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.common.reference.master.data.sdk.api.BankInstitutionCodesApi;
import com.firefly.common.reference.master.data.sdk.model.BankInstitutionCodeDTO;
import com.firefly.domain.configuration.core.config.reference.AbstractReferenceDataService;
import com.firefly.domain.configuration.core.config.reference.ReferenceDataEntityType;
import com.firefly.domain.configuration.interfaces.rest.reference.BankDto;
import com.firefly.domain.configuration.interfaces.rest.reference.ReferenceDataFilter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class BankConfigService
        extends AbstractReferenceDataService<BankInstitutionCodesApi, BankInstitutionCodeDTO, BankDto, UUID> {

    private final ObjectMapper objectMapper;

    public BankConfigService(BankInstitutionCodesApi api, BankMapper mapper, ObjectMapper objectMapper) {
        super(api, mapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public ReferenceDataEntityType entityType() {
        return ReferenceDataEntityType.BANK;
    }

    @Override
    protected Mono<BankInstitutionCodeDTO> sdkCreate(BankInstitutionCodeDTO payload, String idempotencyKey) {
        return api.createBankInstitutionCode(payload, idempotencyKey);
    }

    @Override
    protected Mono<BankInstitutionCodeDTO> sdkUpdate(UUID id, BankInstitutionCodeDTO payload, String idempotencyKey) {
        return api.updateBankInstitutionCode(id, payload, idempotencyKey);
    }

    @Override
    protected Mono<Void> sdkDelete(UUID id, String idempotencyKey) {
        return api.deleteBankInstitutionCode(id, idempotencyKey);
    }

    @Override
    protected Mono<BankInstitutionCodeDTO> sdkGetById(UUID id) {
        return api.getBankInstitutionCode(id, UUID.randomUUID().toString());
    }

    @Override
    protected Flux<BankInstitutionCodeDTO> sdkList(ReferenceDataFilter filter) {
        Integer page = filter.getPage() != null ? filter.getPage() : 0;
        Integer size = filter.getSize() != null ? filter.getSize() : 100;
        return api.listBankInstitutionCodes(page, size, null, null, UUID.randomUUID().toString())
                .flatMapMany(resp -> {
                    List<?> content = resp.getContent();
                    return Flux.fromIterable(content != null ? content : Collections.emptyList());
                })
                .map(obj -> objectMapper.convertValue(obj, BankInstitutionCodeDTO.class));
    }

    @Override
    protected UUID extractId(BankInstitutionCodeDTO response) {
        return response.getInstitutionId();
    }
}
