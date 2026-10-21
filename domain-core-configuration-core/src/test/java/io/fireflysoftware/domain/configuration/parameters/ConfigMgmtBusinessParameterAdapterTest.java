/*
 * Copyright 2026 Firefly Software Foundation
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

import io.fireflysoftware.common.config.sdk.api.ConfigurationAuditsApi;
import io.fireflysoftware.common.config.sdk.api.EnvironmentConfigurationsApi;
import io.fireflysoftware.common.config.sdk.api.FeatureFlagsApi;
import io.fireflysoftware.common.config.sdk.model.ConfigurationAuditDTO;
import io.fireflysoftware.common.config.sdk.model.EnvironmentConfigDTO;
import io.fireflysoftware.common.config.sdk.model.PaginationResponseConfigurationAuditDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigMgmtBusinessParameterAdapterTest {

    @Mock
    private EnvironmentConfigurationsApi environments;

    @Mock
    private FeatureFlagsApi flags;

    @Mock
    private ConfigurationAuditsApi audits;

    @Test
    void createUpdateStatusAndDeleteCallTheConfigurationService() {
        UUID id = UUID.fromString("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa");
        EnvironmentConfigDTO stored = new EnvironmentConfigDTO(id, null, null, null);
        stored.setConfigKey("journey.sample.limit");
        stored.setConfigValue("12");
        stored.setMetadata("{\"kind\":\"LIMIT\"}");
        stored.setActive(true);
        stored.setDescription("sample");

        when(environments.createEnvironmentConfig(any(), any())).thenReturn(Mono.just(stored));
        when(environments.getEnvironmentConfigById(eq(id), any())).thenReturn(Mono.just(stored));
        when(environments.updateEnvironmentConfig(eq(id), any(), any())).thenAnswer(invocation -> {
            EnvironmentConfigDTO body = invocation.getArgument(1);
            EnvironmentConfigDTO saved = new EnvironmentConfigDTO(id, null, null, null);
            saved.setConfigKey(body.getConfigKey());
            saved.setConfigValue(body.getConfigValue());
            saved.setMetadata(body.getMetadata());
            saved.setActive(body.getActive());
            saved.setDescription(body.getDescription());
            return Mono.just(saved);
        });
        when(environments.deleteEnvironmentConfig(eq(id), any())).thenReturn(Mono.empty());
        when(audits.createConfigurationAudit(any(), any())).thenReturn(Mono.just(new ConfigurationAuditDTO()));
        PaginationResponseConfigurationAuditDTO history = new PaginationResponseConfigurationAuditDTO();
        ConfigurationAuditDTO audit = new ConfigurationAuditDTO(
                UUID.fromString("bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb"), null, null, null);
        audit.setEntityId(id);
        audit.setAction("DEACTIVATE");
        history.setContent(List.of(audit));
        when(audits.filterConfigurationAudits(any(), any(), any(), any(), any(), any(), any())).thenReturn(Mono.just(history));

        ConfigMgmtBusinessParameterAdapter adapter = new ConfigMgmtBusinessParameterAdapter(
                environments, flags, audits, "local");

        BusinessParameter created = adapter.create(new BusinessParameter(
                null, "journey.sample.limit", "12", ParameterKind.LIMIT, ParameterStatus.ACTIVE, "sample")).block();
        assertThat(created.value()).isEqualTo("12");

        BusinessParameter updated = adapter.update(id, new BusinessParameter(
                id, "journey.sample.limit", "13", ParameterKind.LIMIT, ParameterStatus.ACTIVE, "sample")).block();
        assertThat(updated.value()).isEqualTo("13");

        adapter.changeStatus(id, ParameterStatus.INACTIVE).block();
        ArgumentCaptor<ConfigurationAuditDTO> auditCaptor = ArgumentCaptor.forClass(ConfigurationAuditDTO.class);
        verify(audits, org.mockito.Mockito.atLeastOnce()).createConfigurationAudit(auditCaptor.capture(), any());
        assertThat(auditCaptor.getAllValues()).extracting(ConfigurationAuditDTO::getAction).contains("DEACTIVATE");

        adapter.remove(id).block();
        verify(environments).deleteEnvironmentConfig(eq(id), any());
        assertThat(adapter.history(id).collectList().block()).isNotEmpty();
    }
}
