package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCredential {
    private String id;
    private String email;
    private String mobileNo;
    private String tempPassword;
    private String password;
    private String expirationDate;
    private LocalDateTime tempPassExpiration;
    private boolean isTempPassActive;
    private int failedLoginAttempt;
}
