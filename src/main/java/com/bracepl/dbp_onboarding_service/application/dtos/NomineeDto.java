package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NomineeDto {
    private String name;
    private String nomineeNidNumber;
    private double nomineePercentage;
    private String relation;
    private String nomineeDob;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String address;
    private String mobileNumber;
    private MultipartFile nomineeNidFront;
    private MultipartFile nomineeNidBack;
    private MultipartFile nomineePhoto;
    private MultipartFile nomineeSignature;
    private boolean minor;
    private String guardianName;
    private String relationshipWithNominee;
    private String guardianNidNumber;
    private MultipartFile guardianNidFront;
    private MultipartFile guardianNidBack;
}