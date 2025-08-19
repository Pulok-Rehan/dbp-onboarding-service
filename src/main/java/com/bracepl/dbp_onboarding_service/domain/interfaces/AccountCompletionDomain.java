package com.bracepl.dbp_onboarding_service.domain.interfaces;

import com.bracepl.dbp_onboarding_service.domain.models.CompletionSection;
import org.bson.types.ObjectId;

public interface AccountCompletionDomain {
    CompletionSection accountCompletion(String mobileNumber, String email, boolean nidPhotos, boolean personalDetails, boolean address, boolean bankDetails, boolean nomineeDetails, boolean documents, boolean isActive);
    CompletionSection checkAccountCompletionDetails(ObjectId accountId);
}
