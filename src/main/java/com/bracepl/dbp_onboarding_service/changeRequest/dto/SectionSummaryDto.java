package com.bracepl.dbp_onboarding_service.changeRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionSummaryDto {
    private String sectionName;
    private int fieldCount;
    private String primaryIssue; // Main reason for change
}