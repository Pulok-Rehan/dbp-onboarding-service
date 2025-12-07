package com.bracepl.dbp_onboarding_service.changeRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRequestResponseDto {
    private String changeRequestId;
    private String partialAccountId;
    private String status;
    private String adminRemarks;
    private String requestedAt;
    private List<SectionDetailDto> sections;
    private int totalFields;
    private int completedFields;
    private int completionPercentage;
}