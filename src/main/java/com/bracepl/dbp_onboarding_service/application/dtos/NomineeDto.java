package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Data;

@Data
public class NomineeDto {
    private String name;
    private String nomineeNid;
    private String nomineeMobileNumber;
    private double nomineePercentage;
    private String relation;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String address;

}
