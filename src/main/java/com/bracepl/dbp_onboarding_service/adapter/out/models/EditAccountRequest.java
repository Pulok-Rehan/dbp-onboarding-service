package com.bracepl.dbp_onboarding_service.adapter.out.models;

import com.bracepl.dbp_onboarding_service.application.dtos.AddressDto;
import com.bracepl.dbp_onboarding_service.application.dtos.BankDetailsDto;
import com.bracepl.dbp_onboarding_service.application.dtos.PersonalDetailsDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class EditAccountRequest {
    private boolean photoSection;
    private boolean personalDetailsSection;
    private boolean bankDetailsSection;
    private boolean addressSection;
    private boolean documentsSection;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PersonalDetailsDto personalDetailsDto;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BankDetailsDto bankDetailsDto;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AddressDto addressDto;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DocumentsDto documentsDto;
    private String accountId;
    private String requestedBy;
    private String remarks;

}
