package com.firefly.domain.configuration.core.config.queries;

import com.firefly.common.cqrs.query.Query;
import com.firefly.common.reference.master.data.sdk.model.LegalFormDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LegalFormQuery implements Query<List<LegalFormDTO>> {
}
