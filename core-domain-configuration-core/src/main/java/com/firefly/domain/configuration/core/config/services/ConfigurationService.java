package com.firefly.domain.configuration.core.config.services;

import com.firefly.domain.configuration.core.config.queries.InitConfigResponse;
import reactor.core.publisher.Mono;

public interface ConfigurationService {

    Mono<InitConfigResponse> getInitConfiguration();

}
