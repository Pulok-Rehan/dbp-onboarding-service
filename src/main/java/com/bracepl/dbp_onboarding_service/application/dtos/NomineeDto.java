package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Data;

@Data
public class NomineeDto {
    private String name;
    private String nomineeNid;
    private String nomineeDob;
    private double nomineePercentage;
    private String relation;
}
