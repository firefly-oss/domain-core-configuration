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
import io.fireflysoftware.domain.configuration.parameters.ParameterStatus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Runs each parameter saga and applies its compensation when a later step fails.
 */
@Service
public class BusinessParameterCommands {

    private final CreateBusinessParameterSaga createSaga;
    private final UpdateBusinessParameterSaga updateSaga;
    private final ChangeBusinessParameterStatusSaga statusSaga;
    private final RemoveBusinessParameterSaga removeSaga;

    public BusinessParameterCommands(
            CreateBusinessParameterSaga createSaga,
            UpdateBusinessParameterSaga updateSaga,
            ChangeBusinessParameterStatusSaga statusSaga,
            RemoveBusinessParameterSaga removeSaga) {
        this.createSaga = createSaga;
        this.updateSaga = updateSaga;
        this.statusSaga = statusSaga;
        this.removeSaga = removeSaga;
    }

    public Mono<BusinessParameter> create(BusinessParameter draft) {
        ExecutionContext ctx = ExecutionContext.forSaga("CreateBusinessParameterSaga", "api");
        return createSaga.createBusinessParameter(draft, ctx)
                .onErrorResume(error -> compensate(createSaga.deleteCreatedBusinessParameter(
                        ctx.getVariableAs("createdParameter", BusinessParameter.class)), error));
    }

    public Mono<BusinessParameter> update(UUID id, BusinessParameter draft) {
        ExecutionContext ctx = ExecutionContext.forSaga("UpdateBusinessParameterSaga", "api");
        return updateSaga.readBusinessParameterForUpdate(id, ctx)
                .then(updateSaga.updateBusinessParameter(draft, ctx))
                .onErrorResume(error -> compensate(updateSaga.restoreBusinessParameter(null, ctx), error));
    }

    public Mono<BusinessParameter> changeStatus(UUID id, ParameterStatus status) {
        ExecutionContext ctx = ExecutionContext.forSaga("ChangeBusinessParameterStatusSaga", "api");
        return statusSaga.readBusinessParameterForStatus(id, ctx)
                .then(statusSaga.changeBusinessParameterStatus(status, ctx))
                .onErrorResume(error -> compensate(statusSaga.restoreBusinessParameterStatus(null, ctx), error));
    }

    public Mono<Void> remove(UUID id) {
        ExecutionContext ctx = ExecutionContext.forSaga("RemoveBusinessParameterSaga", "api");
        return removeSaga.readBusinessParameterForRemove(id, ctx)
                .then(removeSaga.removeBusinessParameter(id, ctx))
                .then()
                .onErrorResume(error -> compensate(removeSaga.recreateBusinessParameter(null, ctx), error));
    }

    private <T> Mono<T> compensate(Mono<Void> compensation, Throwable error) {
        return compensation.onErrorResume(ignored -> Mono.empty()).then(Mono.error(error));
    }
}
