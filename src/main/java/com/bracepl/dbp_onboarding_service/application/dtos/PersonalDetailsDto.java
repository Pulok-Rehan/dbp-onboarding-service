package com.bracepl.dbp_onboarding_service.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PersonalDetailsDto {
    private String name;
    private String gender;
    private String nid;
    private String passportNumber;
    private String email;
    private String mobileNumber;
    private String fathersName;
    private String mothersName;
    private String dateOfBirth;
    private String residency;
    private AddressDto presentAddress;
    private AddressDto permanentAddress;
    private OccupationDto occupationDto;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> fieldsToUpdate;
}
