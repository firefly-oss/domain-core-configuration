package com.firefly.domain.configuration.infra.dtos.orbis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrbisMatch {
    @JsonProperty("Criteria")
    private OrbisCriteria criteria;
}
