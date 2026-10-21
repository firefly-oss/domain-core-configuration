<a href="https://fireflysoftware.io/openplatform/">
<picture>
  <source media="(max-width: 640px) and (prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/firefly-oss/.github/main/profile/assets/banner-narrow-dark.svg">
  <source media="(max-width: 640px)" srcset="https://raw.githubusercontent.com/firefly-oss/.github/main/profile/assets/banner-narrow-light.svg">
  <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/firefly-oss/.github/main/profile/assets/banner-dark.svg">
  <img src="https://raw.githubusercontent.com/firefly-oss/.github/main/profile/assets/banner-light.svg" alt="Firefly OpenPlatform - Domain Core - Configuration" width="100%">
</picture>
</a>

# Firefly OpenPlatform - Domain Core - Configuration

Developed by the Firefly Software Foundation. [dev@fireflysoftware.io](mailto:dev@fireflysoftware.io)

[Website](https://fireflysoftware.io/) · [OpenPlatform](https://fireflysoftware.io/openplatform/) · [Module catalogue](https://fireflysoftware.io/openplatform/modules/)

## Business parameters

Banking and insurance journeys read limits, switches, account identifiers, and transfer routes through `BusinessParameterPort` (`io.fireflysoftware.domain.configuration.parameters.BusinessParameterPort`). This service is not a product master, a party master, or a contract master. Those stay in product, party, and contract. `core-banking-ledger` remains the book. Account values are identifiers already held by that book. Identity and rules stay in Jafly. This service does not evaluate a ruleset and does not authenticate a subject.

The default adapter is `SandboxBusinessParameterAdapter`. It returns the deterministic dataset below and does not call a bank, a PSP, or an identity provider. An installer replaces that class by setting `firefly.configuration.parameters.adapter=core`. The selected class is `ConfigMgmtBusinessParameterAdapter`, which calls `core-common-config-mgmt`.

Reads are direct (`BusinessParameterQuery`). Writes run sagas: create compensates by delete, update restores the previous value, status change restores the previous status and appends audit history (the configuration microservice does not allow audit rows to be deleted), and remove is allowed because the microservice allows delete. Remove compensates by recreating the previous row.

### Sandbox keys

| Key | Value | Kind |
| --- | --- | --- |
| `payments.clearing.outbound.account-id` | `44444444-4444-4444-8444-444444444441` | ACCOUNT |
| `payments.clearing.inbound.account-id` | `44444444-4444-4444-8444-444444444442` | ACCOUNT |
| `payments.nostro.account-id` | `44444444-4444-4444-8444-444444444443` | ACCOUNT |
| `payments.transfer-route.INTERNAL` | `INTERNAL` | ROUTE |
| `payments.transfer-route.OUTBOUND_RAIL` | `OUTBOUND_RAIL` | ROUTE |
| `payments.transfer-route.OUTBOUND_PSP` | `OUTBOUND_PSP` | ROUTE |
| `payments.transfer-route.INBOUND_RAIL` | `INBOUND_RAIL` | ROUTE |
| `payments.transfer-route.INBOUND_PSP` | `INBOUND_PSP` | ROUTE |
| `payments.transfer-type.INTERNAL.route` | `INTERNAL` | ROUTE |
| `payments.transfer-type.DOMESTIC.outbound.route` | `OUTBOUND_RAIL` | ROUTE |
| `payments.transfer-type.DOMESTIC.inbound.route` | `INBOUND_RAIL` | ROUTE |
| `payments.transfer-type.INTERNATIONAL.outbound.route` | `OUTBOUND_RAIL` | ROUTE |
| `payments.transfer-type.INTERNATIONAL.inbound.route` | `INBOUND_RAIL` | ROUTE |
| `journey.lending.min-credit-score` | `680` | LIMIT |
| `journey.cards.default-currency` | `EUR` | LIMIT |
| `journey.cards.tenure-years` | `4` | LIMIT |

Route values are the closed set `INTERNAL`, `OUTBOUND_RAIL`, `OUTBOUND_PSP`, `INBOUND_RAIL`, `INBOUND_PSP`. International credit transfers use the rail routes because library-rails exposes SWIFT. A PSP route is for an instrument library-psps supports, such as a card. Call `findByKey` with the key. `journey.lending.min-credit-score` supplies the same number as the Jafly ruleset constant `MIN_CREDIT_SCORE`. The ruleset file stays the ruleset.

### Ports for workers who own the other repositories

Do not copy these values into those repositories. Call this port, and call `ReferenceCatalogPort` in `domain-core-reference-data` for shared catalogs (currencies, countries, transfer types, rail schemes, account space types, and product families).

- `domain-payments` and `exp-payments`: `payments.clearing.outbound.account-id`, `payments.clearing.inbound.account-id`, `payments.nostro.account-id`, every `payments.transfer-route.*` key, and every `payments.transfer-type.*.route` key. Transfer type definitions, including which type is an internal book and which uses library-rails, are catalog `TRANSFER_TYPE` on `ReferenceCatalogPort`. A PSP route is for an instrument such as a card, not for an international credit transfer.
- `exp-onboarding`, `exp-profile`, `exp-insurance`, `exp-lending`, `exp-finance-terminal`, and any other `exp-*` journey: `journey.lending.min-credit-score` for the decision threshold, and the reference catalogs for currencies and product families. Do not fold Jafly identity or the rules engine into this service.
- `domain-banking-accounts`: catalog `ACCOUNT_SPACE_TYPE` lists `MAIN`, `SAVINGS`, `VACATION`, `EMERGENCY`, `GOALS`, and `CUSTOM`, the same codes as the accounts core. An internal transfer includes movement between those spaces. This service does not open or post accounts.
- `domain-lending-loan-servicing`: this service is not a second loan master. The score threshold above is the origination decision parameter.

`domain-banking-cards` already reads `journey.cards.default-currency` and `journey.cards.tenure-years` through `CardJourneyParameterPort`. Its default adapter is `SandboxCardJourneyParameterAdapter`. An installer replaces that class by setting `firefly.cards.parameters.adapter=configuration`, which selects `ConfigurationCardJourneyParameterAdapter`.


