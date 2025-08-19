package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("FailedLoginAttempt")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedLoginAttempt {
    @Id
    private String id;
    private String username;
    private int attempts;
    private LocalDateTime lastFailedAt;
}