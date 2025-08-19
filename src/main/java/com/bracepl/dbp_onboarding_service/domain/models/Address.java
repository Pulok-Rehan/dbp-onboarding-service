package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String addressLine1;
    private String city;
    private String country;
    private String state;
    private String zipCode;
}
