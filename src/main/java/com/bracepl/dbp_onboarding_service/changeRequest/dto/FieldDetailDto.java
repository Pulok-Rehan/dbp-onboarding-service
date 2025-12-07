package com.bracepl.dbp_onboarding_service.changeRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDetailDto {
    private String fieldName;
    private String displayName; // User-friendly name
    private String currentValue;
    private String reason;
    private boolean mandatory;
    private String suggestedValue;
    private String fieldType; // text, date, file, etc.
}