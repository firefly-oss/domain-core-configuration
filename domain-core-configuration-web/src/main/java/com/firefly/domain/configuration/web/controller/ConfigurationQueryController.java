package com.firefly.domain.configuration.web.controller;

import org.fireflyframework.cqrs.query.QueryBus;
import com.firefly.common.config.sdk.model.TenantBrandingDTO;
import com.firefly.common.reference.master.data.sdk.model.PaginationResponse;
import com.firefly.domain.configuration.core.config.queries.LanguageLocaleQuery;
import com.firefly.domain.configuration.core.config.queries.LookupDomainQuery;
import com.firefly.domain.configuration.core.config.queries.TenantBrandingQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/configuration")
@RequiredArgsConstructor
@Tag(name = "Configuration Queries", description = "Query endpoints for configuration data")
public class ConfigurationQueryController {

    private final QueryBus queryBus;

    @Operation(summary = "Get language locales", description = "Retrieve all available language locales")
    @GetMapping("/languages")
    public Mono<ResponseEntity<PaginationResponse>> getLanguages() {
        log.info("Fetching language locales");
        return queryBus.<PaginationResponse>query(LanguageLocaleQuery.builder().build())
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get lookup domains", description = "Retrieve all available lookup domains")
    @GetMapping("/lookup-domains")
    public Mono<ResponseEntity<PaginationResponse>> getLookupDomains() {
        log.info("Fetching lookup domains");
        return queryBus.<PaginationResponse>query(LookupDomainQuery.builder().build())
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get tenant brandings", description = "Retrieve all tenant brandings")
    @GetMapping("/tenant-brandings")
    public Mono<ResponseEntity<List<TenantBrandingDTO>>> getTenantBrandings() {
        log.info("Fetching tenant brandings");
        return queryBus.<List<TenantBrandingDTO>>query(TenantBrandingQuery.builder().build())
                .map(ResponseEntity::ok);
    }
}
