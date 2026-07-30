package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Data;

@Data
public class OccupationDto {
    private String occupation;
    private String sourceOfFund;
    private double salary;
}
