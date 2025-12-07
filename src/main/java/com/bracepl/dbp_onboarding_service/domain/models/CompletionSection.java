package com.bracepl.dbp_onboarding_service.domain.models;

import com.bracepl.dbp_onboarding_service.changeRequest.ChangeRequestEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private boolean boPayment;
    private Account partialAccount;
    private List<ChangeRequestEntity> changeRequest;
}
