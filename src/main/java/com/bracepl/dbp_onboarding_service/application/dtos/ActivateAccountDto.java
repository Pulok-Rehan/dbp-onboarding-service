package com.bracepl.dbp_onboarding_service.application.dtos;

import com.bracepl.dbp_onboarding_service.domain.models.Account;
import lombok.Data;

@Data
public class ActivateAccountDto {
    private String accountId;
    private String rmId;
}
