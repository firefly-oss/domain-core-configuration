package io.fireflysoftware.domain.configuration.core.config.queries;

import org.fireflyframework.cqrs.query.Query;
import io.fireflysoftware.common.reference.master.data.sdk.model.CurrencyDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CurrencyQuery implements Query<List<CurrencyDTO>> {
}
