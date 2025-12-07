package com.bracepl.dbp_onboarding_service.application.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.changeRequest.dto.ChangeRequestDto;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface SettlementUseCase {
    ServiceResponse getAccountOpeningRequests() throws JsonProcessingException;
    ServiceResponse getCodeAndMobileNumberList() throws JsonProcessingException;
    ServiceResponse getAccountOpeningRequest(String accountId) throws JsonProcessingException;
    ServiceResponse acceptAccount(List<String> accountIds) throws JsonProcessingException;
    ServiceResponse requsetForChange();
    ServiceResponse searchAccount(AccountSearchRequest accountSearchRequest) throws JsonProcessingException;
    ServiceResponse requireEdit(ChangeRequestDto changeRequestDto) throws JsonProcessingException;
}
