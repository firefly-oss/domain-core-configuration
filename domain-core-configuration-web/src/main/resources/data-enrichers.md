Basado en el `curl` proporcionado y la estructura definida en la guía de [`fireflyframework-data`](https://github.com/fireflyframework/fireflyframework-data), aquí tienes el ejemplo completo de implementación para un enricher de **Orbis (Bureau van Dijk)**.

### 1. Modelos de Datos (Client Module)

Primero, definimos las clases para representar la petición y la respuesta de la API de Orbis.

#### OrbisRequest.java
```java
@Data
@Builder
public class OrbisRequest {
    @JsonProperty("WHERE")
    private List<Map<String, List<String>>> where;
    @JsonProperty("SELECT")
    private List<Map<String, Object>> select;

    public static OrbisRequest forBvDId(String bvdId) {
        return OrbisRequest.builder()
            .where(List.of(Map.of("BvDID", List.of(bvdId))))
            .select(List.of(
                Map.of("NAME", Map.of("AS", "NAME")),
                Map.of("BVD_ID_NUMBER", Map.of("AS", "BVD_ID_NUMBER")),
                Map.of("CITY", Map.of("AS", "CITY")),
                Map.of("COUNTRY", Map.of("AS", "COUNTRY")),
                Map.of("VAT_NUMBER", Map.of("LIMIT", 1, "AS", "VAT_NUMBER"))
                // Añadir el resto de campos del SELECT según el curl
            ))
            .build();
    }
}
```

#### OrbisResponse.java
```java
@Data
public class OrbisResponse {
    private List<OrbisCompanyData> data; // Orbis suele devolver una lista de resultados

    @Data
    public static class OrbisCompanyData {
        @JsonProperty("NAME")
        private String name;
        @JsonProperty("BVD_ID_NUMBER")
        private String bvdIdNumber;
        @JsonProperty("CITY")
        private String city;
        @JsonProperty("COUNTRY")
        private String country;
        @JsonProperty("VAT_NUMBER")
        private String vatNumber;
    }
}
```

### 2. Cliente API (Client Module)

Implementamos el cliente utilizando `RestClient` de [`fireflyframework-client`](https://github.com/fireflyframework/fireflyframework-client).

#### OrbisClient.java
```java
@Component
public class OrbisClient {
    private final RestClient restClient;

    public OrbisClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Mono<OrbisResponse> getCompanyData(String bvdId, String domain) {
        return restClient.post("/v1/Orbis/Companies/data", OrbisResponse.class)
            .withHeader("ApiToken", "2PZ408d1c4a2cc324b499edb150ac7f679a6") // Recomendable usar ${orbis.api-token}
            .withHeader("Domain", domain)
            .withBody(OrbisRequest.forBvDId(bvdId))
            .execute();
    }
}
```

### 3. Implementación del Enricher (Enricher Module)

Este componente orquestra la llamada y mapea el resultado al DTO común de tu dominio (ej: `CompanyDTO`).

#### OrbisEnricher.java
```java
@EnricherMetadata(
    providerName = "Orbis BvD",
    tenantId = "global",
    type = "company-details",
    priority = 100
)
public class OrbisEnricher extends DataEnricher<CompanyDTO, OrbisResponse, CompanyDTO> {

    private final OrbisClient orbisClient;

    public OrbisEnricher(
            JobTracingService tracingService,
            JobMetricsService metricsService,
            ResiliencyDecoratorService resiliencyService,
            EnrichmentEventPublisher eventPublisher,
            OrbisClient orbisClient) {
        super(tracingService, metricsService, resiliencyService, eventPublisher, CompanyDTO.class);
        this.orbisClient = orbisClient;
    }

    @Override
    protected Mono<OrbisResponse> fetchProviderData(EnrichmentRequest request) {
        String bvdId = request.requireParam("bvdId");
        String domain = request.getParam("domain", "default-domain");
        return orbisClient.getCompanyData(bvdId, domain);
    }

    @Override
    protected CompanyDTO mapToTarget(OrbisResponse response) {
        if (response.getData() == null || response.getData().isEmpty()) {
            return null;
        }
        
        var data = response.getData().get(0);
        return CompanyDTO.builder()
            .name(data.getName())
            .taxId(data.getVatNumber())
            .city(data.getCity())
            .country(data.getCountry())
            .externalId(data.getBvdIdNumber())
            .build();
    }
}
```

### 4. Configuración (application.yml)

Configura la URL base para el cliente de Orbis:

```yaml
orbis:
  client:
    base-url: https://api.bvdinfo.com
    timeout: 5000

firefly:
  data:
    enrichment:
      enabled: true
      discovery:
        enabled: true
```

### 5. Cómo probarlo

Puedes realizar una llamada POST al endpoint automático de la librería:

**Request (cURL):**
```bash
curl --location 'http://localhost:8080/api/v1/enrichment/smart' \
--header 'Content-Type: application/json' \
--data '{
    "type": "company-details",
    "tenantId": "global",
    "strategy": "ENHANCE",
    "params": {
        "bvdId": "BE0440734148",
        "domain": "TuDominio"
    }
}'
```

Este flujo completo utiliza el patrón **Adapter** para transformar la API compleja de Orbis en un servicio de enriquecimiento sencillo y reutilizable dentro de tu ecosistema.