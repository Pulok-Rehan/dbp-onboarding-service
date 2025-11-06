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
    private double nomineePercentage;
    private String relation;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String address;
    private String mobileNumber;
    private byte[] nomineeNidFront;
    private byte[] nomineeNidBack;
}