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
public class NotificationMessageDto {

    private UUID messageId;

    @NotBlank
    private String messageCode;

    @NotNull
    private UUID typeId;

    private String eventType;
    private String description;
    private String defaultSubject;
    private String defaultMessage;
}
