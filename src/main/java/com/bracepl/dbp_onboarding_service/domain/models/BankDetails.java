package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankDetails {
    private String id;
    private String bankName;
    private String branchName;
    private String routingNumber;
    private String accountNo;
}
