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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Deterministic sandbox keys. Callers read them with {@link BusinessParameterPort#findByKey(String)}.
 * The credit-score value matches the Jafly ruleset constant. The ruleset itself stays in Jafly.
 */
public enum SandboxParameterKey {

    PAYMENTS_CLEARING_OUTBOUND_ACCOUNT_ID(
            "a1111111-1111-4111-8111-111111111111",
            "payments.clearing.outbound.account-id",
            "11111111-1111-4111-8111-111111111111",
            ParameterKind.ACCOUNT,
            "Outbound clearing account identifier. core-banking-ledger remains the book."),
    PAYMENTS_CLEARING_INBOUND_ACCOUNT_ID(
            "a2222222-2222-4222-8222-222222222222",
            "payments.clearing.inbound.account-id",
            "22222222-2222-4222-8222-222222222222",
            ParameterKind.ACCOUNT,
            "Inbound clearing account identifier. core-banking-ledger remains the book."),
    PAYMENTS_NOSTRO_ACCOUNT_ID(
            "a3333333-3333-4333-8333-333333333333",
            "payments.nostro.account-id",
            "33333333-3333-4333-8333-333333333333",
            ParameterKind.ACCOUNT,
            "Nostro account identifier. core-banking-ledger remains the book."),
    PAYMENTS_TRANSFER_ROUTE_INTERNAL(
            "a4444444-4444-4444-8444-444444444441",
            "payments.transfer-route.INTERNAL",
            TransferRoute.INTERNAL.name(),
            ParameterKind.ROUTE,
            "Route used for an internal book transfer."),
    PAYMENTS_TRANSFER_ROUTE_OUTBOUND_RAIL(
            "a4444444-4444-4444-8444-444444444442",
            "payments.transfer-route.OUTBOUND_RAIL",
            TransferRoute.OUTBOUND_RAIL.name(),
            ParameterKind.ROUTE,
            "Route used for an outbound library-rails transfer."),
    PAYMENTS_TRANSFER_ROUTE_OUTBOUND_PSP(
            "a4444444-4444-4444-8444-444444444443",
            "payments.transfer-route.OUTBOUND_PSP",
            TransferRoute.OUTBOUND_PSP.name(),
            ParameterKind.ROUTE,
            "Route used for an outbound library-psps transfer."),
    PAYMENTS_TRANSFER_ROUTE_INBOUND_RAIL(
            "a4444444-4444-4444-8444-444444444444",
            "payments.transfer-route.INBOUND_RAIL",
            TransferRoute.INBOUND_RAIL.name(),
            ParameterKind.ROUTE,
            "Route used for an inbound library-rails transfer."),
    PAYMENTS_TRANSFER_ROUTE_INBOUND_PSP(
            "a4444444-4444-4444-8444-444444444445",
            "payments.transfer-route.INBOUND_PSP",
            TransferRoute.INBOUND_PSP.name(),
            ParameterKind.ROUTE,
            "Route used for an inbound library-psps transfer."),
    PAYMENTS_TRANSFER_TYPE_INTERNAL_ROUTE(
            "a5555555-5555-4555-8555-555555555551",
            "payments.transfer-type.INTERNAL.route",
            TransferRoute.INTERNAL.name(),
            ParameterKind.ROUTE,
            "Internal transfer type, including a traspaso and a movement between account spaces, uses the internal route."),
    PAYMENTS_TRANSFER_TYPE_DOMESTIC_OUTBOUND_ROUTE(
            "a5555555-5555-4555-8555-555555555552",
            "payments.transfer-type.DOMESTIC.outbound.route",
            TransferRoute.OUTBOUND_RAIL.name(),
            ParameterKind.ROUTE,
            "Domestic outbound transfer type uses the outbound rail route."),
    PAYMENTS_TRANSFER_TYPE_DOMESTIC_INBOUND_ROUTE(
            "a5555555-5555-4555-8555-555555555553",
            "payments.transfer-type.DOMESTIC.inbound.route",
            TransferRoute.INBOUND_RAIL.name(),
            ParameterKind.ROUTE,
            "Domestic inbound transfer type uses the inbound rail route."),
    PAYMENTS_TRANSFER_TYPE_INTERNATIONAL_OUTBOUND_ROUTE(
            "a5555555-5555-4555-8555-555555555554",
            "payments.transfer-type.INTERNATIONAL.outbound.route",
            TransferRoute.OUTBOUND_PSP.name(),
            ParameterKind.ROUTE,
            "International outbound transfer type uses the outbound PSP route."),
    PAYMENTS_TRANSFER_TYPE_INTERNATIONAL_INBOUND_ROUTE(
            "a5555555-5555-4555-8555-555555555555",
            "payments.transfer-type.INTERNATIONAL.inbound.route",
            TransferRoute.INBOUND_PSP.name(),
            ParameterKind.ROUTE,
            "International inbound transfer type uses the inbound PSP route."),
    JOURNEY_LENDING_MIN_CREDIT_SCORE(
            "a6666666-6666-4666-8666-666666666661",
            "journey.lending.min-credit-score",
            "680",
            ParameterKind.LIMIT,
            "Minimum credit score supplied to a journey. The Jafly ruleset constant MIN_CREDIT_SCORE stays in the ruleset."),
    JOURNEY_CARDS_DEFAULT_CURRENCY(
            "a6666666-6666-4666-8666-666666666662",
            "journey.cards.default-currency",
            "EUR",
            ParameterKind.LIMIT,
            "Default ISO 4217 currency code for card issuance when the caller omits one."),
    JOURNEY_CARDS_TENURE_YEARS(
            "a6666666-6666-4666-8666-666666666663",
            "journey.cards.tenure-years",
            "4",
            ParameterKind.LIMIT,
            "Card expiry tenure in years when the caller omits an expiration date.");

    private final UUID id;
    private final String key;
    private final String value;
    private final ParameterKind kind;
    private final String description;

    SandboxParameterKey(String id, String key, String value, ParameterKind kind, String description) {
        this.id = UUID.fromString(id);
        this.key = key;
        this.value = value;
        this.kind = kind;
        this.description = description;
    }

    public UUID id() {
        return id;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

    public ParameterKind kind() {
        return kind;
    }

    public String description() {
        return description;
    }

    public BusinessParameter toParameter() {
        return new BusinessParameter(id, key, value, kind, ParameterStatus.ACTIVE, description);
    }

    public static List<BusinessParameter> seed() {
        return Arrays.stream(values()).map(SandboxParameterKey::toParameter).toList();
    }
}
