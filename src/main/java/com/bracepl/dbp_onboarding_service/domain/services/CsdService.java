package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.application.dtos.RemarkDto;
import com.bracepl.dbp_onboarding_service.application.interfaces.CsdUseCase;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import org.springframework.stereotype.Service;

@Service
public class CsdService implements CsdUseCase {
    @Override
    public ServiceResponse addRemark(RemarkDto remarkDto) {
        return null;
    }

    @Override
    public ServiceResponse getClients() {
        return null;
    }

    @Override
    public ServiceResponse getCLient(String accountId) {
        return null;
    }
}
