package com.bracepl.dbp_onboarding_service.domain.interfaces;

import com.bracepl.dbp_onboarding_service.domain.models.Account;

import java.util.List;

public interface ComplianceDomain {
    boolean activateAccount(String accountId, String rmId);
    String getSelfRmId();
    List<Account> getAllAcceptedAccount();
}
