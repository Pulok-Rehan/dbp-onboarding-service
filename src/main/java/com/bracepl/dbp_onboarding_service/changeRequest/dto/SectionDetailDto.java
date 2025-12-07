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
public class SectionDetailDto {
    private String section;
    private String displayName;
    private int stepNumber;
    private boolean completed;
    private List<FieldDetailDto> fields;
}