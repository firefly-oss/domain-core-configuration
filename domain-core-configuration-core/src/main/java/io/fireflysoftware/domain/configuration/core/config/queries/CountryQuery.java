package io.fireflysoftware.domain.configuration.core.config.queries;

import org.fireflyframework.cqrs.query.Query;
import io.fireflysoftware.common.reference.master.data.sdk.model.CountryDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CountryQuery implements Query<List<CountryDTO>> {
}