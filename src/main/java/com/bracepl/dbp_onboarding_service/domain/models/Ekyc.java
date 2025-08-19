package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ekyc {
    //    private String name;
//    private String gender;
//    private String nid;
//    private String fathersName;
//    private String mothersName;
//    private LocalDate dateOfBirth;
//    private String addressLine1;
//    private String city;
//    private String country;
//    private String state;
//    private String zipCode;
    private String address;
    private String place_of_birth;
    private String bangla_name;
    private String date_of_birth;
    private String father_name;
    private String mother_name;
    private String name;
    private String nid_no;
    private boolean matched;
}
