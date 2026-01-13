package com.bracepl.dbp_onboarding_service.application.interfaces;


import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.Nominee;
import java.util.List;

public interface PartialAccountDomain {
    Account save(Account account, boolean isActive);
    Account saveWithJointAccount(Account account, boolean isActive);
    Account saveClientType(Account account);
    Account findByMoBileNumber(String mobileNumber);
    Account findByNid(String nid);
    Account findById(String id);
    Account saveWithNominees(Account account, List<Nominee> nominees);
}
