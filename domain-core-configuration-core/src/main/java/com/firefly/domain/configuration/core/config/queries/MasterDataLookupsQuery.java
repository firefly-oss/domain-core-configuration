package com.firefly.domain.configuration.core.config.queries;

import com.firefly.common.reference.master.data.sdk.model.LookupItemDTO;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.query.Query;

import java.util.List;
import java.util.Map;

/**
 * Returns every active lookup item grouped by its parent domain code.
 * <p>
 * Consumers (typically the experience tier) use the resulting map to
 * project the items they care about into channel-specific master-data
 * payloads without each channel having to chain
 * {@code lookup_domains} + {@code lookup_items} calls.
 */
@Data
@Builder
public class MasterDataLookupsQuery implements Query<Map<String, List<LookupItemDTO>>> {
}
