package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.ParitalAccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.domain.models.PartialAccount;

import java.util.List;

public interface PartialAccountRepositoryCustom {
    List<ParitalAccountEntity> searchAccounts(String request);
}
