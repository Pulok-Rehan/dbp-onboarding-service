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
public class ChangeRequestDto {
    private String accountId;
    private String adminId;
    private String remarks;
    private List<SectionChangeDto> sectionChanges;
}

