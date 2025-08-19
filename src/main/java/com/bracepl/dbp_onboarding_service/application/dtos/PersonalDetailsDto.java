package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalDetailsDto {
    private String name;
    private String gender;
    private String nid;
    private String email;
    private String mobileNumber;
    private String fathersName;
    private String mothersName;
    private String dateOfBirth;
    private String residency;
}
