package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSnapshotDto {
    private String emailAddress;
    private String mobileNumber;
    private String addressLine1;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String accountNo;
    private Map<String, Object> bankDetails; // flatten BankEntity fields here
    private List<Map<String, Object>> nominees;
}
