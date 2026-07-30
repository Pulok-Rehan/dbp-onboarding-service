package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateInvestorRequest {

    private String investorCode;
    private String boCategory;
    private String boType;
    private String firstName;
    private String middleName;
    private String lastName;
    private String shortName;

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;

    private String city;
    private String country;
    private String postalCode;

    private String nationalId;
    private String mobile;
    private String email;
    private String gender;

    private String occupation;
    private String fatherOrHusbandName;
    private String motherName;

    private String bankCode;
    private String bankRoutingNumber;
    private String bankAccountNumber;

    private String electronicDividend;
    private String tin;
    private String residencyFlag;
    private String citigenOf;

    private String dateOfBirth;

    private String corelationId;
    private String openDate;

    private double amountIn;

    private String channelTransMode;
    private String transactionId;

    private List<NomineeSpRequest> nominees;

    private String createdBy;
}
