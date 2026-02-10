package com.firefly.domain.configuration.core.config.queries;

import org.fireflyframework.cqrs.query.Query;
import com.firefly.domain.configuration.interfaces.rest.SelectCompanyResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectCompanyQuery implements Query<List<SelectCompanyResponse>> {
    private String name;
    private String city;
    private String country;
    private String address;
    private String nationalId;
    private String postCode;
}
