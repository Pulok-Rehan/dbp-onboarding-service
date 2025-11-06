package com.bracepl.dbp_onboarding_service.application.interfaces;

import com.bracepl.dbp_onboarding_service.application.dtos.RemarkDto;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface CsdUseCase {
    ServiceResponse addRemark(RemarkDto remarkDto);
    ServiceResponse getCLient(String accountId);
    ServiceResponse getClientsFinal(String mobileNumber) throws JsonProcessingException;
    ServiceResponse getClientsInitiated(String mobileNumber) throws JsonProcessingException;
    ServiceResponse getClients(String mobileNumber) throws JsonProcessingException;
}
