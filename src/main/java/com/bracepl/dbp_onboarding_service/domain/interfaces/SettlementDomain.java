package com.bracepl.dbp_onboarding_service.domain.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.changeRequest.dto.ChangeRequestDto;
import com.bracepl.dbp_onboarding_service.domain.models.Account;

import java.util.List;
import java.util.Map;

public interface SettlementDomain {
    List<Account> getAllRequestedAccounts();
    List<Map<String, String>> getCodeAndMobileNumberList();
    Account getRequestedAccount(String accountId);
    String requiresEdit(ChangeRequestDto changeRequestDto);
    List<Account> acceptRequestedAccount(List<String> accountIds);
    Account requestForChange();
    List<Account> searchAccount(AccountSearchRequest accountSearchRequest);
}
