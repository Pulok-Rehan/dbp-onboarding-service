package com.bracepl.dbp_onboarding_service.domain.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.PlatformProfile;
import com.bracepl.dbp_onboarding_service.domain.models.CompletionSection;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface AccountCompletionDomain {
    CompletionSection accountCompletion(String mobileNumber, String email, boolean nidPhotos, boolean personalDetails, boolean address, boolean bankDetails, boolean nomineeDetails, boolean documents, boolean isActive, boolean boPayment, boolean ekyc);
    CompletionSection checkAccountCompletionDetails(ObjectId accountId);
    Optional<PlatformProfile> getPlatformProfile(String mobileNumber);
    PlatformProfile updatePlatformProfile(PlatformProfile platformProfile);
    PlatformProfile savePlatformProfile(PlatformProfile platformProfile);
//    PlatformProfile getPlatformProfileById(ObjectId id);
}
