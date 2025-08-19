package com.bracepl.dbp_onboarding_service.domain.interfaces;

import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.RmModel;

import java.util.List;

public interface RmDomain {
    List<RmModel> getAllRms();
    List<Account> getAllClients(String rmId);
    boolean acceptCLient(String clientId);
    boolean rejectCLient(String clientId, String reason);
    boolean contactClient(String clientId);

}
