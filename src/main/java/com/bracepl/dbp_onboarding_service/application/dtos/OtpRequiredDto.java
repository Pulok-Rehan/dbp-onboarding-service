package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequiredDto {
    private String statusCode;
    private String mobileNumber;
    private String email;
}
