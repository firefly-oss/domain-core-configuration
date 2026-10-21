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

import java.util.UUID;

/**
 * Updates a business parameter and compensates by writing the previous value back.
 * Status stays on the status saga.
 */
@Saga(name = "UpdateBusinessParameterSaga")
@Service
public class UpdateBusinessParameterSaga {

    static final String STEP_READ = "readBusinessParameterForUpdate";
    static final String STEP_UPDATE = "updateBusinessParameter";
    static final String COMPENSATE_RESTORE = "restoreBusinessParameter";
    static final String PREVIOUS = "previousBusinessParameter";

    private final BusinessParameterPort parameters;

    public UpdateBusinessParameterSaga(BusinessParameterPort parameters) {
        this.parameters = parameters;
    }

    @SagaStep(id = STEP_READ)
    @StepEvent(type = "configuration.parameter.read")
    public Mono<BusinessParameter> readBusinessParameterForUpdate(UUID id, ExecutionContext ctx) {
        return parameters.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Parameter was not found")))
                .doOnNext(current -> ctx.putVariable(PREVIOUS, current));
    }

    @SagaStep(id = STEP_UPDATE, dependsOn = STEP_READ, compensate = COMPENSATE_RESTORE)
    @StepEvent(type = "configuration.parameter.updated")
    public Mono<BusinessParameter> updateBusinessParameter(BusinessParameter draft, ExecutionContext ctx) {
        BusinessParameter previous = ctx.getVariableAs(PREVIOUS, BusinessParameter.class);
        if (previous == null || previous.id() == null) {
            return Mono.error(new IllegalStateException("Parameter was not read before update"));
        }
        BusinessParameter request = new BusinessParameter(
                previous.id(),
                draft.key() == null ? previous.key() : draft.key(),
                draft.value(),
                draft.kind() == null ? previous.kind() : draft.kind(),
                previous.status(),
                draft.description() == null ? previous.description() : draft.description());
        return parameters.update(previous.id(), request);
    }

    public Mono<Void> restoreBusinessParameter(BusinessParameter updated, ExecutionContext ctx) {
        BusinessParameter previous = ctx.getVariableAs(PREVIOUS, BusinessParameter.class);
        if (previous == null || previous.id() == null) {
            return Mono.empty();
        }
        return parameters.update(previous.id(), previous).then();
    }
}
