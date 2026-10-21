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
import io.fireflysoftware.domain.configuration.parameters.ParameterStatus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Changes parameter status and compensates by restoring the previous status.
 * The configuration microservice keeps immutable audit history, so compensation
 * appends a new history row instead of deleting the first one.
 */
@Saga(name = "ChangeBusinessParameterStatusSaga")
@Service
public class ChangeBusinessParameterStatusSaga {

    static final String STEP_READ = "readBusinessParameterForStatus";
    static final String STEP_STATUS = "changeBusinessParameterStatus";
    static final String COMPENSATE_RESTORE = "restoreBusinessParameterStatus";
    static final String PREVIOUS_STATUS = "previousParameterStatus";

    private final BusinessParameterPort parameters;

    public ChangeBusinessParameterStatusSaga(BusinessParameterPort parameters) {
        this.parameters = parameters;
    }

    @SagaStep(id = STEP_READ)
    @StepEvent(type = "configuration.parameter.status.read")
    public Mono<BusinessParameter> readBusinessParameterForStatus(UUID id, ExecutionContext ctx) {
        return parameters.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Parameter was not found")))
                .doOnNext(current -> {
                    ctx.putVariable("parameterId", current.id());
                    ctx.putVariable(PREVIOUS_STATUS, current.status());
                });
    }

    @SagaStep(id = STEP_STATUS, dependsOn = STEP_READ, compensate = COMPENSATE_RESTORE)
    @StepEvent(type = "configuration.parameter.status.changed")
    public Mono<BusinessParameter> changeBusinessParameterStatus(ParameterStatus status, ExecutionContext ctx) {
        UUID id = ctx.getVariableAs("parameterId", UUID.class);
        ParameterStatus previous = ctx.getVariableAs(PREVIOUS_STATUS, ParameterStatus.class);
        if (id == null || previous == null) {
            return Mono.error(new IllegalStateException("Parameter was not read before the status change"));
        }
        if (status == null) {
            return Mono.error(new IllegalArgumentException("Parameter status is required"));
        }
        return parameters.changeStatus(id, status);
    }

    public Mono<Void> restoreBusinessParameterStatus(BusinessParameter updated, ExecutionContext ctx) {
        ParameterStatus previous = ctx.getVariableAs(PREVIOUS_STATUS, ParameterStatus.class);
        UUID id = updated == null ? ctx.getVariableAs("parameterId", UUID.class) : updated.id();
        if (id == null || previous == null) {
            return Mono.empty();
        }
        return parameters.changeStatus(id, previous).then();
    }
}
