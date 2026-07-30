package com.bracepl.dbp_onboarding_service.adapter.out.models;

import lombok.Data;

import java.util.Map;

@Data
public class GenericServiceRequestDto {
    private String mobileNumber;
//    private String serviceName; // e.g., "Address Change", "Email Change"
    private Map<String, Object> fieldValues;
}