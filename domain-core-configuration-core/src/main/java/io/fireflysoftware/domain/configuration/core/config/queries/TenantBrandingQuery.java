package io.fireflysoftware.domain.configuration.core.config.queries;

import org.fireflyframework.cqrs.query.Query;
import io.fireflysoftware.common.config.sdk.model.TenantBrandingDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TenantBrandingQuery implements Query<List<TenantBrandingDTO>> {
}
