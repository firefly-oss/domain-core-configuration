/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.configuration.core.config.handlers;

import io.fireflysoftware.common.reference.master.data.sdk.api.LanguageLocaleApi;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationResponse;
import io.fireflysoftware.domain.configuration.core.config.queries.LanguageLocaleQuery;
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
