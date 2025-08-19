package com.bracepl.dbp_onboarding_service.domain.interfaces;

import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface PowerOfAttorneyUseCase {
    ServiceResponse grantPowerOfAttorney(String mobileNumber, String granteeId, String otp) throws JsonProcessingException;
    ServiceResponse revokePowerOfAttorney(String grantorId, String granteeId, String otp) throws JsonProcessingException;
}
