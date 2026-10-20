package io.fireflysoftware.domain.configuration.core.config.queries;

import org.fireflyframework.cqrs.query.Query;
import io.fireflysoftware.common.reference.master.data.sdk.model.PaginationResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LookupDomainQuery implements Query<PaginationResponse> {
}
