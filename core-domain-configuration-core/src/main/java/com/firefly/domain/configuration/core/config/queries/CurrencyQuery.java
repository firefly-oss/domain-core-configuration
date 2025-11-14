package com.firefly.domain.configuration.core.config.queries;

import com.firefly.common.cqrs.query.Query;
import com.firefly.common.reference.master.data.sdk.model.CurrencyDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CurrencyQuery implements Query<List<CurrencyDTO>> {
}
