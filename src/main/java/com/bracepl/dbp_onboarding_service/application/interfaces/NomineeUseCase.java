package com.bracepl.dbp_onboarding_service.application.interfaces;

import com.bracepl.dbp_onboarding_service.application.dtos.NomineeDto;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface NomineeUseCase {
    ServiceResponse addNominee(NomineeDto nomineeDtos, MultipartFile nomineeNidFront, MultipartFile nomineeNidBack, String mobileNumber) throws IOException;
    ServiceResponse getNominee(String id) throws JsonProcessingException;
    ServiceResponse getNominees(String mobileNumber) throws JsonProcessingException;
}
