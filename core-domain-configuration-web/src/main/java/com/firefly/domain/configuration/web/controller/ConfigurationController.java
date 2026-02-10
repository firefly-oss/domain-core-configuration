package com.firefly.domain.configuration.web.controller;

import com.firefly.domain.configuration.core.config.queries.SelectCompanyQuery;
import com.firefly.domain.configuration.core.config.services.CompanyService;
import com.firefly.domain.configuration.core.config.services.ConfigurationService;
import com.firefly.domain.configuration.interfaces.rest.InitConfigResponse;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configuration")
@RequiredArgsConstructor
@Tag(name = "Configuration", description = "CQ queries for configuration")
public class ConfigurationController {

    private final ConfigurationService configurationService;
    private final CompanyService companyService;

    @Operation(summary = "Get init configuration", description = "Retrieve init configuration")
    @GetMapping("/init")
    public Mono<ResponseEntity<InitConfigResponse>> getInitConfiguration() {
        return configurationService.getInitConfiguration()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Select company", description = "Retrieve companies matching the selection criteria")
    @PostMapping("/select-company")
    public Mono<ResponseEntity<List<SelectCompanyResponse>>> selectCompany(@RequestBody SelectCompanyQuery request) {
        return companyService.selectCompany(request)
                .map(ResponseEntity::ok);
    }
}
