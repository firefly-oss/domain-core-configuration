package com.firefly.domain.configuration.interfaces.rest.reference;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDto {

    private UUID institutionId;

    @NotBlank
    private String bankName;

    private String swiftCode;
    private String routingNumber;
    private String ibanPrefix;
    private UUID countryId;
    private UUID institutionTypeLkpId;
    private String svgIcon;
}
