package com.bracepl.dbp_onboarding_service.application.dtos;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.PoAccessDto;
import lombok.Data;

@Data
public class PowerOfAttorneyRequestDto {
    private String mobileNumber;
    private String requestId;
    private PoAccessDto poAccessDto;
}
