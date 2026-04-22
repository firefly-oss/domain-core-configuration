# Domain Core Configuration

A reactive domain-layer microservice that orchestrates the tenant-scoped **reference data** used by the rest of the Firefly Banking Platform. It wraps three core services behind a single admin surface, enforces cross-entity invariants, and publishes atomic update / activation / deactivation flows as compensatable sagas.

> **Repository:** [https://github.com/firefly-oss/domain-core-configuration](https://github.com/firefly-oss/domain-core-configuration)

---

## Overview

`domain-core-configuration` does not own any storage. It is a thin, strongly-typed, reactive orchestration layer over three upstream core services:

| Upstream core service | Role in this domain |
|---|---|
| `core-common-reference-master-data` | Source of truth for 26 reference-data families: countries, currencies, languages, legal forms, lookup domains & items, identity documents, contract roles, banks, activity codes, administrative divisions, asset types, consent catalog, document templates, relationship types, rule operation types, titles, transaction categories, notifications, etc. |
| `core-common-product-mgmt` | Consulted **read-only** to validate cross-entity invariants during activation / deactivation (e.g., "is there an active product referencing this currency?") |
| `core-common-config-mgmt` | Source of per-tenant branding, used to assemble a tenant-wide configuration snapshot. |

Responsibilities:

- **26 reference-data admin APIs** — one per master-data family — with uniform CRUD semantics (`create`, `update`, `remove`, `getById`, `list`, `activate`, `deactivate`).
- **Cross-entity update saga** — `UpdateReferenceDataSaga` mutates the primary reference entity AND all of its localizations atomically, with snapshot-based rollback.
- **Cross-entity activation saga** — `ActivateReferenceEntitySaga` flips the status of a reference entity and validates that the change does not orphan active products or contracts.
- **Tenant snapshot** — single endpoint that projects the full configuration (reference data + branding) scoped to a tenant.
- **SDK generation** — auto-generates a reactive Java client SDK from the OpenAPI spec.

---

## Architecture

### Module Structure

```
domain-core-configuration (parent POM)
|-- domain-core-configuration-core         # ReferenceDataRegistry, per-family services, sagas, handlers, constants
|-- domain-core-configuration-interfaces   # Public DTOs and REST request/response contracts
|-- domain-core-configuration-infra        # Three ClientFactories + ConfigurationProperties for each upstream core
|-- domain-core-configuration-web          # Spring Boot app, 27 admin controllers + orchestration + snapshot
|-- domain-core-configuration-sdk          # Auto-generated reactive client SDK
```

### Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot, Spring WebFlux (reactive) |
| Virtual Threads | Enabled |
| CQRS / Saga | FireflyFramework orchestration (`SagaEngine`, `CommandBus`, `QueryBus`) |
| Event Streaming | Kafka (FireflyFramework EDA) |
| API Documentation | SpringDoc OpenAPI (Swagger UI) |
| Metrics | Micrometer + Prometheus |
| Mapping | MapStruct + Lombok |
| SDK Generation | OpenAPI Generator Maven Plugin (webclient / reactive) |
| Build | Maven (multi-module) |

### How calls to core services work

The domain wires three independent `@Component`-annotated client factories in the `-infra` module, each targeting a different upstream service:

```
domain-core-configuration-infra
|-- ClientFactory                  (26 beans — core-common-reference-master-data)
|-- ProductMgmtClientFactory       (2 beans  — core-common-product-mgmt, read-only)
|-- ConfigMgmtClientFactory        (1 bean   — core-common-config-mgmt)
```

Each factory builds its own `ApiClient` from a dedicated `@ConfigurationProperties` bean so base paths can be overridden per upstream service:

| Properties class | Prefix | Default |
|---|---|---|
| `MasterDataProperties` | `api-configuration.common-platform.master-data` | `http://localhost:8081` |
| `ProductMgmtProperties` | `api-configuration.common-platform.product-mgmt` | `http://localhost:8082` |
| `ConfigMgmtProperties` | `api-configuration.common-platform.config-mgmt` | `http://localhost:8083` |

Per the Firefly platform conventions, this domain **imports only core SDKs** and never another domain SDK. All calls are reactive WebClient requests with idempotency keys.

#### The `AbstractReferenceDataService` pattern

Every reference-data family has a concrete service in `core.config.reference.<family>.<Family>ConfigService` that extends `AbstractReferenceDataService<A, S, D, I>` where:

- `A` = SDK API class (e.g., `CurrenciesApi`, `CountriesApi`, `BankInstitutionCodesApi`)
- `S` = SDK DTO (`CurrencyDTO`, `CountryDTO`, …)
- `D` = public domain DTO (`CurrencyConfigDto`, `CountryConfigDto`, …)
- `I` = identifier type (always `UUID`)

The base class supplies the full CRUD orchestration — idempotency key generation, structured logging, and `WebClientResponseException` → domain-exception mapping (`ReferenceDataNotFoundException`, `ReferenceDataConflictException`, …). Concrete subclasses only provide six narrow SDK-binding hooks:

```java
protected abstract Mono<S> sdkCreate(S payload, String idempotencyKey);
protected abstract Mono<S> sdkUpdate(I id, S payload, String idempotencyKey);
protected abstract Mono<Void> sdkDelete(I id, String idempotencyKey);
protected abstract Mono<S> sdkGetById(I id);
protected abstract Flux<S> sdkList(ReferenceDataFilter filter);
protected abstract I extractId(S response);
```

Two list-call shapes coexist across the 26 families, both absorbed by the base class:

- **Typed filter** (Currency, Country, LegalForm, RuleOperationType, ContractRole): `filterX(FilterRequestX, idem)` returns `Mono<PaginationResponseX>` with typed content.
- **Generic list** (everything else): `listX(page, size, sortBy, sortDir, idem)` returns `Mono<PaginationResponse>` with `List<Object>` — the subclass injects `ObjectMapper` and uses `objectMapper.convertValue` to type each row.

`ContractRoleScope` is a hybrid (typed filter request, generic response) and the subclass handles both.

#### The `ReferenceDataRegistry`

A single Spring-collected registry maps every `ReferenceDataEntityType` enum value to its concrete `ReferenceDataService` bean. The registry is the dispatcher the orchestration sagas consult — they are entity-type-agnostic. Adding a new reference-data family is a matter of:

1. Defining the `<Family>ConfigService` extending the base class.
2. Adding the SDK client bean to `ClientFactory`.
3. Registering a new enum constant in `ReferenceDataEntityType`.

### The two cross-entity sagas

| Saga | Steps | Compensation semantics |
|---|---|---|
| `UpdateReferenceDataSaga` | 1. `captureSnapshot` (read-only) — snapshots the entity + its localizations via `ReferenceDataRegistry`.<br>2. `updatePrimaryEntity` (compensate `restorePrimaryEntity`) — dispatches `UpdateReferenceDataSagaCommand` through `CommandBus` to the family's service.<br>3. `updateLocalizations` (compensate `restoreLocalizations`) — updates the list of localizations via `ExpandEach`. | On failure the captured snapshot is re-applied. Compensations log at `ERROR` with `saga.compensation.invariant-violation` when a snapshot is unexpectedly absent. |
| `ActivateReferenceEntitySaga` | 1. `captureEntitySnapshot` (read-only) — loads the current entity for rollback; **fails fast** with `ReferenceDataNotFoundException` if missing (`.switchIfEmpty(Mono.error(...))`).<br>2. `crossEntityValidate` (no-op compensation) — calls `CrossEntityValidator.productHasReference(…)` against `core-common-product-mgmt` to block deactivation when an active product still references the entity. Validator fails **safe** on SDK errors (treats error as "has reference"). <br>3. `applyStatusChange` (compensate `restoreSnapshot`) — dispatches the activate/deactivate command. | Compensation restores the full captured snapshot, not just the status. |

Both sagas obey the platform invariants:

- **No `@SagaStep` method emits `Mono.empty()`.** Read-only first steps use `.switchIfEmpty(Mono.error(...))`; terminal steps always emit at least one sentinel value.
- **Compensation methods MAY emit `Mono<Void>` via `Mono.empty()`** — they are not annotated with `@SagaStep`.
- **Idempotency keys** — every mutating SDK call passes `UUID.randomUUID().toString()`.
- **Context flow** — shared identifiers (entity type, primary entity ID, previous state) are stored in `ExecutionContext` in step 1 and read in subsequent steps and compensations.

### Tenant snapshot

`TenantSnapshotController` (`GET /api/v1/configuration/tenant-snapshot?tenantId=…`) fans out to:

- `TenantBrandingsApi` (`config-mgmt`) for branding.
- Every `ReferenceDataService` in the registry for the core reference data.

…and assembles a single `TenantSnapshotResponse` — the one-call bootstrap surface consumed by downstream services when a new tenant comes online.

### Domain Events

All events are published to the `domain-layer` Kafka topic, prefixed `configuration.`:

- `configuration.reference.updated`
- `configuration.reference.activated`
- `configuration.reference.deactivated`
- `configuration.localization.updated`
- `configuration.snapshot.requested`

---

## Setup

### Prerequisites

- **Java 25** (JDK)
- **Apache Maven 3.9+**
- **Apache Kafka** (default: `localhost:9092`)
- **core-common-reference-master-data** running (default: `http://localhost:8081`)
- **core-common-product-mgmt** running (default: `http://localhost:8082`)
- **core-common-config-mgmt** running (default: `http://localhost:8083`)

### Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SERVER_ADDRESS` | `localhost` | Server bind address |
| `SERVER_PORT` | `8080` | Server listening port |

### Application Configuration (`application.yaml`)

| Property | Default | Description |
|---|---|---|
| `firefly.cqrs.command.timeout` | `30s` | Command execution timeout |
| `firefly.cqrs.query.timeout` | `15s` | Query execution timeout |
| `firefly.cqrs.query.cache-ttl` | `15m` | Query cache TTL |
| `firefly.eda.publishers.kafka.default.bootstrap-servers` | `localhost:9092` | Kafka bootstrap servers |
| `firefly.eda.publishers.kafka.default.default-topic` | `domain-layer` | Default Kafka topic |
| `api-configuration.common-platform.master-data.base-path` | `http://localhost:8081` | Master-data upstream |
| `api-configuration.common-platform.product-mgmt.base-path` | `http://localhost:8082` | Product-mgmt upstream (cross-entity validation only) |
| `api-configuration.common-platform.config-mgmt.base-path` | `http://localhost:8083` | Config-mgmt upstream (tenant branding) |

### Spring Profiles

| Profile | Logging | Swagger UI |
|---|---|---|
| `dev` | DEBUG for `com.firefly`, R2DBC, Flyway | Enabled |
| `testing` | DEBUG for `com.firefly`, INFO for R2DBC | Enabled |
| `prod` | WARN root, INFO for `com.firefly` | Disabled |

### Build and run

```bash
./mvnw clean install                                          # full build
./mvnw -pl domain-core-configuration-web spring-boot:run      # run with default profile
./mvnw -pl domain-core-configuration-web spring-boot:run \
      -Dspring-boot.run.profiles=dev                          # run with dev profile
java -jar domain-core-configuration-web/target/domain-core-configuration.jar
```

---

## API Endpoints

**Base path:** `/api/v1/configuration`

### Reference-data administration (26 families)

Each family has its own admin controller under `/admin/{plural}` — this prefix is applied uniformly because the legacy `ConfigurationQueryController` already mapped the unprefixed paths. All admin controllers extend `AbstractReferenceDataAdminController<D, UUID>` and expose the same six endpoints:

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/admin/{plural}` | Create a new entity |
| `PUT` | `/admin/{plural}/{id}` | Update an entity |
| `DELETE` | `/admin/{plural}/{id}` | Remove an entity |
| `GET` | `/admin/{plural}/{id}` | Get an entity by id |
| `GET` | `/admin/{plural}` | List / filter entities |
| `POST` | `/admin/{plural}/{id}/activate` | Activate — runs `ActivateReferenceEntitySaga` |
| `POST` | `/admin/{plural}/{id}/deactivate` | Deactivate — runs `ActivateReferenceEntitySaga` (cross-entity validation blocks deactivation when referenced) |

The 26 admin controllers (and their `{plural}` path segments) are:

| Family | Path segment |
|---|---|
| Activity codes | `activity-codes` |
| Administrative divisions | `administrative-divisions` |
| Asset types | `asset-types` |
| Banks | `banks` |
| Consent catalog | `consent-catalog` |
| Contract document types | `contract-document-types` |
| Contract roles | `contract-roles` |
| Contract role scopes | `contract-role-scopes` |
| Contract types | `contract-types` |
| Countries | `countries` |
| Currencies | `currencies` |
| Document templates | `document-templates` |
| Document template types | `document-template-types` |
| Identity documents | `identity-documents` |
| Identity document categories | `identity-document-categories` |
| Language locales | `language-locales` |
| Legal forms | `legal-forms` |
| Lookup domains | `lookup-domains` |
| Lookup items | `lookup-items` |
| Message types | `message-types` |
| Notification messages | `notification-messages` |
| Notification message templates | `notification-message-templates` |
| Relationship types | `relationship-types` |
| Rule operation types | `rule-operation-types` |
| Titles | `titles` |
| Transaction categories | `transaction-categories` |

### Orchestration (cross-entity)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/orchestration/update-reference-data` | Atomic update of an entity + its localizations (runs `UpdateReferenceDataSaga`) |

Request body (`UpdateReferenceDataSagaRequest`):

```json
{
  "entityType": "CURRENCY",
  "entityId": "00000000-0000-0000-0000-000000000000",
  "payload": { /* typed per entityType via ReferenceDataTypeResolver */ },
  "localizations": [
    { "localeTag": "es-ES", "languageLocaleId": "…" }
  ]
}
```

### Tenant snapshot

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/tenant-snapshot?tenantId={uuid}` | Assemble branding + reference-data view for a tenant |

### OpenAPI / Swagger UI

- API Docs (JSON): `GET /v3/api-docs`
- Swagger UI: `GET /swagger-ui.html`

---

## Development Guidelines

### Project conventions

- **Reactive programming** — every service / handler / controller method returns `Mono<T>` or `Flux<T>`. Never block.
- **CQRS pattern** — commands mutate state via `CommandBus`; queries read state via `QueryBus`.
- **Saga orchestration** — multi-step cross-entity flows are defined as `@Saga` classes with `@SagaStep` methods and compensation handlers for rollback.
- **Read-only first steps** use `.switchIfEmpty(Mono.error(...))` to guarantee they never emit `Mono.empty()`, which the framework would treat as a step failure.
- **Compensation methods** MAY return `Mono.empty()` and swallow SDK errors — they are NOT annotated with `@SagaStep`.
- **Constants** — saga names, step IDs, compensation names, event types, and `ExecutionContext` keys are in `ReferenceDataConstants` and `GlobalConstants`.

### Module responsibilities

- **core** — pure domain logic; no Spring Web dependencies. Contains the `ReferenceDataRegistry`, per-family services, sagas, handlers, and constants.
- **interfaces** — public DTOs and REST request/response contracts.
- **infra** — three ClientFactories, three Properties classes. One factory per upstream core service.
- **web** — Spring Boot app entry point, 27 admin controllers (26 family-specific + 1 abstract base), orchestration controller, snapshot controller, OpenAPI definition.
- **sdk** — auto-generated client SDK from the OpenAPI spec.

### Adding a new reference-data family

1. Add a new value to `ReferenceDataEntityType`.
2. Create `<Family>ConfigService` extending `AbstractReferenceDataService<A, S, D, I>` in `core.config.reference.<family>`.
3. Add a MapStruct `<Family>Mapper` (`@Mapper(componentModel = "spring")`).
4. Register the SDK API bean in `ClientFactory` (master-data factory).
5. Extend `ReferenceDataRegistry` so the new service is wired automatically via Spring.
6. Create a `<Family>AdminController extends AbstractReferenceDataAdminController<D, UUID>` in `web.controller.reference.<family>`.
7. Add a smoke test that exercises the six inherited endpoints.

---

## Monitoring

### Health and readiness

| Endpoint | Description |
|---|---|
| `GET /actuator/health` | Overall application health |
| `GET /actuator/health/liveness` | Kubernetes liveness probe |
| `GET /actuator/health/readiness` | Kubernetes readiness probe |

### Metrics

| Endpoint | Description |
|---|---|
| `GET /actuator/info` | Application build information |
| `GET /actuator/prometheus` | Prometheus-format metrics |

CQRS command and query metrics are enabled via:

```yaml
firefly.cqrs.command.metrics-enabled: true
firefly.cqrs.command.tracing-enabled: true
```

---

## License

Apache License 2.0 — see [LICENSE](LICENSE).
