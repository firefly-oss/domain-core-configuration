package io.fireflysoftware.domain.configuration.core.config.queries;

import org.fireflyframework.cqrs.query.Query;
import io.fireflysoftware.common.reference.master.data.sdk.model.LegalFormDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LegalFormQuery implements Query<List<LegalFormDTO>> {
}
