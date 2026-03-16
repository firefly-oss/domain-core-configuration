package com.firefly.domain.configuration.core.config.handlers;

import com.firefly.common.reference.master.data.sdk.api.LookupDomainsApi;
import com.firefly.common.reference.master.data.sdk.model.PaginationResponse;
import com.firefly.domain.configuration.core.config.queries.LookupDomainQuery;
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
class GetLookupDomainsHandlerTest {

    @Mock
    private LookupDomainsApi lookupDomainsApi;

    private GetLookupDomainsHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetLookupDomainsHandler(lookupDomainsApi);
    }

    @Test
    void shouldReturnLookupDomains() {
        PaginationResponse response = new PaginationResponse();
        response.setContent(List.of());

        when(lookupDomainsApi.listDomains(eq(0), eq(100), isNull(), isNull(), anyString()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(LookupDomainQuery.builder().build()))
                .expectNext(response)
                .verifyComplete();

        verify(lookupDomainsApi).listDomains(eq(0), eq(100), isNull(), isNull(), anyString());
    }
}
