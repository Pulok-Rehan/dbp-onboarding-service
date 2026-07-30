package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NomineeListDto {
    private String mobileNumber;
    private List<NomineeDto> nominees;
}