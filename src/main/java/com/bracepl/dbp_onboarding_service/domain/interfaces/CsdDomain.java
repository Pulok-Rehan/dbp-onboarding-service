package com.bracepl.dbp_onboarding_service.domain.interfaces;

import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.ClientRemarkModel;

import java.util.List;

public interface CsdDomain {
    boolean addRemark(ClientRemarkModel clientRemarkModel);
    List<Account> getAllClientsfinal(String csdId);
    List<Account> getAllClientsInitiated(String mobileNumber);
    List<Account> getAllClients(String mobileNumber);
}
