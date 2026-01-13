package com.bracepl.dbp_onboarding_service.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddressDto {
//    private String mobileNumber;
    private String addressLine1;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> fieldsToUpdate;
}
