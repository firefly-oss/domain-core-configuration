package com.firefly.domain.configuration.core.config.queries;

import org.fireflyframework.cqrs.query.Query;
import com.firefly.common.config.sdk.model.TenantBrandingDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TenantBrandingQuery implements Query<List<TenantBrandingDTO>> {
}
