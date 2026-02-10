package com.firefly.domain.configuration.core.config.services;

import com.firefly.domain.configuration.interfaces.rest.InitConfigResponse;
import reactor.core.publisher.Mono;

public interface ConfigurationService {

    Mono<InitConfigResponse> getInitConfiguration();

}
