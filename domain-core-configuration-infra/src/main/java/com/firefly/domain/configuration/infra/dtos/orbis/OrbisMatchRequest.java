package com.firefly.domain.configuration.infra.dtos.orbis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrbisMatchRequest {
    @JsonProperty("MATCH")
    private OrbisMatch match;
    @JsonProperty("SELECT")
    private List<String> select;
}
