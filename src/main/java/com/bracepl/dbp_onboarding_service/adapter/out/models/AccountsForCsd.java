package com.bracepl.dbp_onboarding_service.adapter.out.models;

import com.bracepl.dbp_onboarding_service.domain.models.Account;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountsForCsd {
    private List<Account> accountListInitiated;
    private List<Account> accountListFinal;

}
