package com.bracepl.dbp_onboarding_service.domain.interfaces;

import com.bracepl.dbp_onboarding_service.domain.models.BankDetails;

public interface BankDomain {
    BankDetails findByBankRoutingNumber(String routingNumber);
    BankDetails findBankByBranchName(String branchName);
    BankDetails save(BankDetails bankDetails);
}
