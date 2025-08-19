package com.bracepl.dbp_onboarding_service.application.interfaces;

import com.bracepl.dbp_onboarding_service.application.dtos.RemarkDto;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;

public interface CsdUseCase {
    ServiceResponse addRemark(RemarkDto remarkDto);
    ServiceResponse getClients();
    ServiceResponse getCLient(String accountId);
}
