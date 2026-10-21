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
 * Removes a parameter. The configuration microservice allows delete.
 * Compensation recreates the previous row. Audit history stays, because audit rows are immutable.
 */
@Saga(name = "RemoveBusinessParameterSaga")
@Service
public class RemoveBusinessParameterSaga {

    static final String STEP_READ = "readBusinessParameterForRemove";
    static final String STEP_REMOVE = "removeBusinessParameter";
    static final String COMPENSATE_RECREATE = "recreateBusinessParameter";
    static final String PREVIOUS = "removedBusinessParameter";

    private final BusinessParameterPort parameters;

    public RemoveBusinessParameterSaga(BusinessParameterPort parameters) {
        this.parameters = parameters;
    }

    @SagaStep(id = STEP_READ)
    @StepEvent(type = "configuration.parameter.remove.read")
    public Mono<BusinessParameter> readBusinessParameterForRemove(UUID id, ExecutionContext ctx) {
        return parameters.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Parameter was not found")))
                .doOnNext(current -> ctx.putVariable(PREVIOUS, current));
    }

    @SagaStep(id = STEP_REMOVE, dependsOn = STEP_READ, compensate = COMPENSATE_RECREATE)
    @StepEvent(type = "configuration.parameter.removed")
    public Mono<BusinessParameter> removeBusinessParameter(UUID id, ExecutionContext ctx) {
        BusinessParameter previous = ctx.getVariableAs(PREVIOUS, BusinessParameter.class);
        if (previous == null || previous.id() == null) {
            return Mono.error(new IllegalStateException("Parameter was not read before remove"));
        }
        return parameters.remove(previous.id()).thenReturn(previous);
    }

    public Mono<Void> recreateBusinessParameter(BusinessParameter removed, ExecutionContext ctx) {
        BusinessParameter previous = ctx.getVariableAs(PREVIOUS, BusinessParameter.class);
        if (previous == null) {
            previous = removed;
        }
        if (previous == null || previous.id() == null) {
            return Mono.empty();
        }
        return parameters.create(previous).then();
    }
}
