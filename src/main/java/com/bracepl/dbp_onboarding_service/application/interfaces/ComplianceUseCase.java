package com.bracepl.dbp_onboarding_service.application.interfaces;

import com.bracepl.dbp_onboarding_service.application.dtos.ActivateAccountDto;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ComplianceUseCase {
    ServiceResponse getAcceptedAccounts() throws JsonProcessingException;
    ServiceResponse getRms() throws JsonProcessingException;
    ServiceResponse activateAccount(ActivateAccountDto activateAccountDto) throws JsonProcessingException;
    ServiceResponse instantBoActivation(String accountId) throws JsonProcessingException;
    ServiceResponse approveProfileUpdateRequest(Account account);
}
