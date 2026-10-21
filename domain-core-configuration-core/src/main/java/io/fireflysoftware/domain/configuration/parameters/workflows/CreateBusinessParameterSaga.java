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

package io.fireflysoftware.domain.configuration.parameters.workflows;

import io.fireflysoftware.domain.configuration.parameters.BusinessParameter;
import io.fireflysoftware.domain.configuration.parameters.BusinessParameterPort;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Creates a business parameter and compensates by deleting it.
 */
@Saga(name = "CreateBusinessParameterSaga")
@Service
public class CreateBusinessParameterSaga {

    static final String STEP_CREATE = "createBusinessParameter";
    static final String COMPENSATE_DELETE = "deleteCreatedBusinessParameter";

    private final BusinessParameterPort parameters;

    public CreateBusinessParameterSaga(BusinessParameterPort parameters) {
        this.parameters = parameters;
    }

    @SagaStep(id = STEP_CREATE, compensate = COMPENSATE_DELETE)
    @StepEvent(type = "configuration.parameter.created")
    public Mono<BusinessParameter> createBusinessParameter(BusinessParameter draft, ExecutionContext ctx) {
        return parameters.create(draft)
                .doOnNext(created -> ctx.putVariable("createdParameter", created));
    }

    public Mono<Void> deleteCreatedBusinessParameter(BusinessParameter created) {
        if (created == null || created.id() == null) {
            return Mono.empty();
        }
        return parameters.remove(created.id());
    }
}
