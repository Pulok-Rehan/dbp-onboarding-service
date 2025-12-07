package com.bracepl.dbp_onboarding_service.application.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.application.dtos.ActivateAccountDto;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ComplianceUseCase {
    ServiceResponse getAcceptedAccounts() throws JsonProcessingException;
    ServiceResponse getRms() throws JsonProcessingException;
    ServiceResponse activateAccount(List<ActivateAccountDto> activateAccountDto) throws JsonProcessingException;
    ServiceResponse instantBoActivation(String accountId) throws JsonProcessingException;
    ServiceResponse approveProfileUpdateRequest(Account account);
    ServiceResponse searchAccount(AccountSearchRequest accountSearchRequest) throws JsonProcessingException;
}
