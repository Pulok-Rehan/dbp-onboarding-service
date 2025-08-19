package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nominee {
    private String id;
    private String name;
    private String nomineeNidNumber;
    private String nomineeDob;
    private double nomineePercentage;
    private String relation;
    private byte[] nomineeNidFront;
    private byte[] nomineeNidBack;
}