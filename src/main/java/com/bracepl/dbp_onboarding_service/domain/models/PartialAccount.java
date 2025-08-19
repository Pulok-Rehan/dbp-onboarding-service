package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PartialAccount {
    private String name;
    private String gender;
    private String nid_no;
    private String email;
    private String mobileNumber;
    private String fathersName;
    private String mothersName;
    private String date_of_birth;
    private String residency;
    private String boType;
    private String addressLine1;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String bankName;
    private String branchName;
    private String routingNumber;
    private String accountNo;
}
