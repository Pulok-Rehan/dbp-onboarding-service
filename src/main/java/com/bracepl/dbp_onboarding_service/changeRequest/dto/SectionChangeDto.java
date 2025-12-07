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
public class SectionChangeDto {
    private String section;
    private List<FieldChangeDto> fields;
}