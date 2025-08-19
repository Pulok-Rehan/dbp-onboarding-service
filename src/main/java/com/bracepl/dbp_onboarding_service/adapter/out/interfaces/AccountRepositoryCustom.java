package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.domain.models.Account;

import java.util.List;

public interface AccountRepositoryCustom {
    List<AccountEntity> searchAccounts(AccountSearchRequest request);
}
