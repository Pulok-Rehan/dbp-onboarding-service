package com.bracepl.dbp_onboarding_service.adapter.out.entities;

import com.bracepl.dbp_onboarding_service.adapter.out.models.DocumentsDto;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.application.dtos.AddressDto;
import com.bracepl.dbp_onboarding_service.application.dtos.BankDetailsDto;
import com.bracepl.dbp_onboarding_service.application.dtos.PersonalDetailsDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditRequired {
    @Id
    private String id;
    private boolean photoSection;
    private boolean personalDetailsSection;
    private boolean bankDetailsSection;
    private boolean addressSection;
    private boolean documentsSection;
    private PersonalDetailsDto personalDetailsDto;
    private BankDetailsDto bankDetailsDto;
    private AddressDto addressDto;
    private DocumentsDto documentsDto;
    private String accountId;
    private String requestedBy;

    public static EditRequired fromRequest(EditAccountRequest request) {
        EditRequired editRequired = new EditRequired();
        editRequired.setPhotoSection(request.isPhotoSection());
        editRequired.setPersonalDetailsSection(request.isPersonalDetailsSection());
        editRequired.setBankDetailsSection(request.isBankDetailsSection());
        editRequired.setAddressSection(request.isAddressSection());
        editRequired.setDocumentsSection(request.isDocumentsSection());
        editRequired.setPersonalDetailsDto(request.getPersonalDetailsDto());
        editRequired.setBankDetailsDto(request.getBankDetailsDto());
        editRequired.setAddressDto(request.getAddressDto());
        editRequired.setDocumentsDto(request.getDocumentsDto());
        editRequired.setAccountId(request.getAccountId());
        editRequired.setRequestedBy(request.getRequestedBy());
        return editRequired;
    }
}
