package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Data;

@Data
public class ForgotPasswordDto {
    private String mobileNumber;
    private String email;
}
