package com.bracepl.dbp_onboarding_service.adapter.out.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NidVerificationRequest {
    private String nationalNumber;
    private String dateOfBirth;
    private String photo;
}
