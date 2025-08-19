package com.bracepl.dbp_onboarding_service.adapter.out.models;

import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import lombok.Data;

@Data
public class AccountSearchRequest {
    private String fromDate;
    private String toDate;
    private AccountStatus status;
    private String investorCode;
    private String mobileNumber;
}
