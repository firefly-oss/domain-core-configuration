package com.firefly.domain.configuration.interfaces.rest.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTemplateDto {

    private UUID templateId;

    @NotBlank
    private String templateCode;

    @NotNull
    private UUID typeId;

    private String category;
    private String description;

    @NotBlank
    private String templateName;

    private String templateContent;
    private String version;
}
