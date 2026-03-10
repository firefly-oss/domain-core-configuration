package com.firefly.domain.configuration.infra.dtos.equifax;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditReportDTO {
    private String status;
    private String firstName;
    private String lastName;
    private String birthDate;
    private Long socialNumber;
    private String reportDate;
    private List<ScoreDTO> scores;
    private List<AlertDTO> alerts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreDTO {
        private String modelName;
        private Integer value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertDTO {
        private String type;
        private String message;
    }
}
