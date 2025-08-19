package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_credentials")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentials {
    @Id
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
