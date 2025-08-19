package com.bracepl.dbp_onboarding_service.application.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankDetailsDto {
    private String mobileNumber;
    private String bankName;
    private String branchName;
    private String routingNumber;
    private String accountNo;
    private String boType;
    private boolean boLinked;
    private String boNumber;
    private String jointAccountName;
    private String jointAccountEmail;
    private String jointAccountAddress;
}
