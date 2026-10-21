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

import java.time.Instant;
import java.util.UUID;

/**
 * One immutable audit row. The configuration microservice does not allow audit rows to be deleted.
 */
public record ParameterHistoryEntry(
        UUID id,
        UUID parameterId,
        String action,
        String fieldName,
        String oldValue,
        String newValue,
        Instant recordedAt) {
}
