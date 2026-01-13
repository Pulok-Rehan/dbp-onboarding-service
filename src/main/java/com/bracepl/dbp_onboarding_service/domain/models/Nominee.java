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
    private String nid;
    private String nomineeDob;
    private double percentage;
    private String relation;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String address;
    private String mobileNumber;
    private String nomineeNidFront;
    private String nomineeNidBack;
    private String nomineePhoto;
    private String nomineeSignature;
    private boolean minor;
    private String guardianName;
    private String relationshipWithNominee;
    private String guardianNidNumber;
    private String guardianNidFront;
    private String guardianNidBack;
}