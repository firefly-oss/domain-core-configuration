package com.firefly.domain.configuration.core.config.handlers;

import com.firefly.common.reference.master.data.sdk.api.LanguageLocaleApi;
import com.firefly.common.reference.master.data.sdk.model.PaginationResponse;
import com.firefly.domain.configuration.core.config.queries.LanguageLocaleQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLanguagesHandlerTest {

    @Mock
    private LanguageLocaleApi languageLocaleApi;

    private GetLanguagesHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetLanguagesHandler(languageLocaleApi);
    }

    @Test
    void shouldReturnLanguageLocales() {
        PaginationResponse response = new PaginationResponse();
        response.setContent(List.of());

        when(languageLocaleApi.listLanguageLocales(eq(0), eq(100), isNull(), isNull(), anyString()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(LanguageLocaleQuery.builder().build()))
                .expectNext(response)
                .verifyComplete();

        verify(languageLocaleApi).listLanguageLocales(eq(0), eq(100), isNull(), isNull(), anyString());
    }
}
