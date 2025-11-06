package com.bracepl.dbp_onboarding_service.application.interfaces;
import com.bracepl.dbp_onboarding_service.application.dtos.RmAction;
import com.bracepl.dbp_onboarding_service.domain.enums.Action;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface RmUseCase {
    ServiceResponse getClients(String mobileNumber) throws JsonProcessingException;
    ServiceResponse takeActionOfClient(RmAction rmAction) throws JsonProcessingException;
    ServiceResponse contactClient(String accountId) throws JsonProcessingException;
    ServiceResponse addRemark(String remark, String accountId);
    ServiceResponse getDashboard(String rmId);
}
