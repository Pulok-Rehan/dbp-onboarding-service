package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Data;

@Data
public class RegisterDtoInternal {
    private String employeeCode;
    private String username;
    private String password;
    private String otp;
}
