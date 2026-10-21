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

import java.util.UUID;

/**
 * Checks a parameter before it is written. Route values are the closed {@link TransferRoute} set.
 * Account values are identifiers, not ledger rows.
 */
public final class ParameterRules {

    private ParameterRules() {
    }

    public static void requireWritable(BusinessParameter draft) {
        if (draft == null) {
            throw new IllegalArgumentException("Parameter is required");
        }
        requireKey(draft.key());
        if (draft.value() == null || draft.value().isBlank()) {
            throw new IllegalArgumentException("Parameter value is required");
        }
        if (draft.kind() == null) {
            throw new IllegalArgumentException("Parameter kind is required");
        }
        if (draft.kind() == ParameterKind.ROUTE) {
            parseRoute(draft.value());
        }
        if (draft.kind() == ParameterKind.SWITCH && !isBooleanText(draft.value())) {
            throw new IllegalArgumentException("A switch value must be true or false");
        }
        if (draft.kind() == ParameterKind.ACCOUNT) {
            parseAccountId(draft.value());
        }
    }

    public static void requireKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Parameter key is required");
        }
        String trimmed = key.trim();
        if (trimmed.length() < 2 || trimmed.length() > 100) {
            throw new IllegalArgumentException("Parameter key must be between 2 and 100 characters");
        }
        if (trimmed.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("Parameter key must not contain whitespace");
        }
    }

    public static TransferRoute parseRoute(String value) {
        try {
            return TransferRoute.valueOf(value.trim());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Transfer route is not one of the closed route set");
        }
    }

    public static UUID parseAccountId(String value) {
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Account parameter must be an account identifier");
        }
    }

    private static boolean isBooleanText(String value) {
        return "true".equalsIgnoreCase(value.trim()) || "false".equalsIgnoreCase(value.trim());
    }
}
