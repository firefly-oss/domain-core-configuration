package com.firefly.domain.configuration.core.config.queries;

import org.fireflyframework.cqrs.query.Query;
import com.firefly.common.reference.master.data.sdk.model.PaginationResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LanguageLocaleQuery implements Query<PaginationResponse> {
}
