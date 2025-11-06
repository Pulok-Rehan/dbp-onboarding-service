package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientTypeDto {
    private String mobileNumber;
    private String boType;
    private boolean boLinked;
    private String boNumber;
    private String name;
    private String email;
    private String address;
}
