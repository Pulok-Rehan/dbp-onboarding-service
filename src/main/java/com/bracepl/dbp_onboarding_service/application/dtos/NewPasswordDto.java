package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Data;

@Data
public class NewPasswordDto {
    private String mobileNumber;
    private String temporaryPassword;
    private String newPassword;
}
