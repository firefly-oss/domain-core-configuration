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

package io.fireflysoftware.domain.configuration.parameters;

import io.fireflysoftware.domain.configuration.parameters.workflows.ChangeBusinessParameterStatusSaga;
import io.fireflysoftware.domain.configuration.parameters.workflows.CreateBusinessParameterSaga;
import io.fireflysoftware.domain.configuration.parameters.workflows.RemoveBusinessParameterSaga;
import io.fireflysoftware.domain.configuration.parameters.workflows.UpdateBusinessParameterSaga;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SandboxBusinessParameterAdapterTest {

    @Test
    void sandboxDatasetCoversPaymentsRoutesAndJourneyLimits() {
        SandboxBusinessParameterAdapter adapter = new SandboxBusinessParameterAdapter();

        StepVerifier.create(adapter.findByKey(SandboxParameterKey.PAYMENTS_CLEARING_OUTBOUND_ACCOUNT_ID.key()))
                .assertNext(parameter -> {
                    assertThat(parameter.kind()).isEqualTo(ParameterKind.ACCOUNT);
                    assertThat(parameter.value()).isEqualTo("11111111-1111-4111-8111-111111111111");
                })
                .verifyComplete();

        StepVerifier.create(adapter.findByKey(SandboxParameterKey.PAYMENTS_NOSTRO_ACCOUNT_ID.key()))
                .assertNext(parameter -> assertThat(UUID.fromString(parameter.value()))
                        .isEqualTo(UUID.fromString("33333333-3333-4333-8333-333333333333")))
                .verifyComplete();

        for (TransferRoute route : TransferRoute.values()) {
            StepVerifier.create(adapter.findByKey("payments.transfer-route." + route.name()))
                    .assertNext(parameter -> assertThat(parameter.value()).isEqualTo(route.name()))
                    .verifyComplete();
        }

        StepVerifier.create(adapter.findByKey(SandboxParameterKey.PAYMENTS_TRANSFER_TYPE_INTERNAL_ROUTE.key()))
                .assertNext(parameter -> assertThat(parameter.value()).isEqualTo(TransferRoute.INTERNAL.name()))
                .verifyComplete();
        StepVerifier.create(adapter.findByKey(SandboxParameterKey.PAYMENTS_TRANSFER_TYPE_DOMESTIC_OUTBOUND_ROUTE.key()))
                .assertNext(parameter -> assertThat(parameter.value()).isEqualTo(TransferRoute.OUTBOUND_RAIL.name()))
                .verifyComplete();
        StepVerifier.create(adapter.findByKey(SandboxParameterKey.PAYMENTS_TRANSFER_TYPE_INTERNATIONAL_OUTBOUND_ROUTE.key()))
                .assertNext(parameter -> assertThat(parameter.value()).isEqualTo(TransferRoute.OUTBOUND_PSP.name()))
                .verifyComplete();
        StepVerifier.create(adapter.findByKey(SandboxParameterKey.JOURNEY_LENDING_MIN_CREDIT_SCORE.key()))
                .assertNext(parameter -> assertThat(parameter.value()).isEqualTo("680"))
                .verifyComplete();
        StepVerifier.create(adapter.findByKey(SandboxParameterKey.JOURNEY_CARDS_DEFAULT_CURRENCY.key()))
                .assertNext(parameter -> assertThat(parameter.value()).isEqualTo("EUR"))
                .verifyComplete();
    }

    @Test
    void createCompensatesByDelete() {
        SandboxBusinessParameterAdapter adapter = new SandboxBusinessParameterAdapter();
        CreateBusinessParameterSaga saga = new CreateBusinessParameterSaga(adapter);
        ExecutionContext ctx = ExecutionContext.forSaga("CreateBusinessParameterSaga", "test");
        BusinessParameter draft = new BusinessParameter(
                null, "journey.sample.limit", "10", ParameterKind.LIMIT, ParameterStatus.ACTIVE, "sample");

        BusinessParameter created = saga.createBusinessParameter(draft, ctx).block();
        assertThat(created).isNotNull();
        assertThat(adapter.findByKey("journey.sample.limit").block()).isNotNull();

        saga.deleteCreatedBusinessParameter(created).block();
        assertThat(adapter.findByKey("journey.sample.limit").block()).isNull();
    }

    @Test
    void updateCompensatesByRestoringThePreviousValue() {
        SandboxBusinessParameterAdapter adapter = new SandboxBusinessParameterAdapter();
        UpdateBusinessParameterSaga saga = new UpdateBusinessParameterSaga(adapter);
        ExecutionContext ctx = ExecutionContext.forSaga("UpdateBusinessParameterSaga", "test");
        UUID id = SandboxParameterKey.JOURNEY_CARDS_TENURE_YEARS.id();

        saga.readBusinessParameterForUpdate(id, ctx).block();
        BusinessParameter updated = saga.updateBusinessParameter(
                new BusinessParameter(id, SandboxParameterKey.JOURNEY_CARDS_TENURE_YEARS.key(),
                        "5", ParameterKind.LIMIT, null, "changed"),
                ctx).block();
        assertThat(updated.value()).isEqualTo("5");

        saga.restoreBusinessParameter(updated, ctx).block();
        assertThat(adapter.findById(id).block().value()).isEqualTo("4");
    }

    @Test
    void statusChangeRecordsHistoryAndCompensationRestoresThePreviousStatus() {
        SandboxBusinessParameterAdapter adapter = new SandboxBusinessParameterAdapter();
        ChangeBusinessParameterStatusSaga saga = new ChangeBusinessParameterStatusSaga(adapter);
        ExecutionContext ctx = ExecutionContext.forSaga("ChangeBusinessParameterStatusSaga", "test");
        UUID id = SandboxParameterKey.JOURNEY_LENDING_MIN_CREDIT_SCORE.id();

        saga.readBusinessParameterForStatus(id, ctx).block();
        saga.changeBusinessParameterStatus(ParameterStatus.INACTIVE, ctx).block();
        assertThat(adapter.findById(id).block().status()).isEqualTo(ParameterStatus.INACTIVE);
        assertThat(adapter.history(id).collectList().block()).anyMatch(entry -> "DEACTIVATE".equals(entry.action()));

        saga.restoreBusinessParameterStatus(adapter.findById(id).block(), ctx).block();
        assertThat(adapter.findById(id).block().status()).isEqualTo(ParameterStatus.ACTIVE);
        assertThat(adapter.history(id).collectList().block())
                .filteredOn(entry -> "status".equals(entry.fieldName()))
                .hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void removeCompensatesByRecreate() {
        SandboxBusinessParameterAdapter adapter = new SandboxBusinessParameterAdapter();
        RemoveBusinessParameterSaga saga = new RemoveBusinessParameterSaga(adapter);
        ExecutionContext ctx = ExecutionContext.forSaga("RemoveBusinessParameterSaga", "test");
        UUID id = SandboxParameterKey.PAYMENTS_CLEARING_INBOUND_ACCOUNT_ID.id();

        saga.readBusinessParameterForRemove(id, ctx).block();
        saga.removeBusinessParameter(id, ctx).block();
        assertThat(adapter.findById(id).block()).isNull();

        saga.recreateBusinessParameter(null, ctx).block();
        assertThat(adapter.findByKey(SandboxParameterKey.PAYMENTS_CLEARING_INBOUND_ACCOUNT_ID.key()).block())
                .isNotNull();
    }

    @Test
    void queryReadsThePortDirectly() {
        SandboxBusinessParameterAdapter adapter = new SandboxBusinessParameterAdapter();
        BusinessParameterQuery query = new BusinessParameterQuery(adapter);
        StepVerifier.create(query.byKey(SandboxParameterKey.JOURNEY_CARDS_TENURE_YEARS.key()))
                .assertNext(parameter -> assertThat(parameter.value()).isEqualTo("4"))
                .verifyComplete();
    }

    @Test
    void sandboxAdapterIsTheDefaultBean() {
        new ApplicationContextRunner()
                .withUserConfiguration(BusinessParameterAdapterConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(BusinessParameterPort.class);
                    assertThat(context.getBean(BusinessParameterPort.class))
                            .isInstanceOf(SandboxBusinessParameterAdapter.class);
                });
    }

    @Test
    void corePropertySelectsTheConfigurationMicroserviceAdapter() {
        new ApplicationContextRunner()
                .withUserConfiguration(BusinessParameterAdapterConfiguration.class)
                .withPropertyValues("firefly.configuration.parameters.adapter=core")
                .run(context -> assertThat(context.getBean(BusinessParameterPort.class))
                        .isInstanceOf(ConfigMgmtBusinessParameterAdapter.class));
    }
}
