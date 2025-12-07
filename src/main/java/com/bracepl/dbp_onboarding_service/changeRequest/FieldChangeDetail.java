package com.bracepl.dbp_onboarding_service.changeRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldChangeDetail {
    private String fieldName;
    private String currentValue;
    private String reason;
    private boolean isMandatory;
    private String suggestedValue;
}