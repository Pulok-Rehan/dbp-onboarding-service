package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {
    private String mobileNumber;
    private String addressLine1;
    private String city;
    private String country;
    private String state;
    private String zipCode;
}
