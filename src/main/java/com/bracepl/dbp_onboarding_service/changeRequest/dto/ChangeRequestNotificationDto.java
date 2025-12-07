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
public class ChangeRequestNotificationDto {
    private String recipientMobile;
    private String recipientEmail;
    private String changeRequestId;
    private String message;
    private List<SectionSummaryDto> sectionSummaries;
    private String actionUrl; // Deep link to app
}
