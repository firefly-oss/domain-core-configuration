package com.firefly.domain.configuration.core.config.handlers;

import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import com.firefly.common.reference.master.data.sdk.api.LanguageLocaleApi;
import com.firefly.common.reference.master.data.sdk.model.PaginationResponse;
import com.firefly.domain.configuration.core.config.queries.LanguageLocaleQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent
public class GetLanguagesHandler extends QueryHandler<LanguageLocaleQuery, PaginationResponse> {

    private final LanguageLocaleApi languageLocaleApi;

    @Override
    protected Mono<PaginationResponse> doHandle(LanguageLocaleQuery query) {
        return languageLocaleApi.listLanguageLocales(0, 100, null, null, UUID.randomUUID().toString());
    }

}
