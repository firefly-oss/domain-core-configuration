package com.firefly.domain.configuration.interfaces.rest.reference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Shared filter carried through every admin list operation for reference-data entities.
 * Carries tenant scoping, status filtering, pagination and free-text query.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceDataFilter {

    private UUID tenantId;
    private String status;
    private Integer page;
    private Integer size;
    private String query;
}
