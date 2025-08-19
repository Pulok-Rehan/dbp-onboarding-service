package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.domain.models.Ekyc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EkycService {
    Ekyc callEkyc(MultipartFile nidFront, MultipartFile nidBack, String mobileNumber) throws IOException;
}
