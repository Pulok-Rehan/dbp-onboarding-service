package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    @NonNull
    private String email;
    @NonNull
    private String mobileNumber;
    private String statusCode;
}
