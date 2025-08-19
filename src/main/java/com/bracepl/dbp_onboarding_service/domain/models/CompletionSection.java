package com.bracepl.dbp_onboarding_service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompletionSection {
    private String mobileNumber;
//    private ObjectId accountId;
    private boolean personalDetails;
    private boolean address;
    private boolean nidPhotos;
    private boolean documents;
    private boolean bankDetails;
    private boolean nomineeDetails;
    private Account partialAccount;
}
