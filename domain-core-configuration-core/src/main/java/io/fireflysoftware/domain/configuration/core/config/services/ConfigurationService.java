package io.fireflysoftware.domain.configuration.core.config.services;

import io.fireflysoftware.domain.configuration.interfaces.rest.InitConfigResponse;
import reactor.core.publisher.Mono;

public interface ConfigurationService {

    Mono<InitConfigResponse> getInitConfiguration();

}
