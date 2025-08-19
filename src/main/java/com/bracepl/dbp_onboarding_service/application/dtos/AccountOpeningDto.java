package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class AccountOpeningDto {
    private String name;
    private String gender;
    private String nid;
    private String email;
    private String mobileNumber;
    private String fathersName;
    private String mothersName;
    private String dateOfBirth;
    private String addressLine1;
    private String city;
    private String country;
    private String state;
    private String zipCode;
//    private long bankId;
    private String bankName;
    private String branchName;
    private String routingNumber;
    private String accountNo;
    private String residency;
    private String boType;
//    private MultipartFile photo;
    private MultipartFile signature;
    private MultipartFile chequeLeaf;


}
