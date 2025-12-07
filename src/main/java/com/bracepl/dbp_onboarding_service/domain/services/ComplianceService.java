package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.application.dtos.ActivateAccountDto;
import com.bracepl.dbp_onboarding_service.application.interfaces.ComplianceUseCase;
import com.bracepl.dbp_onboarding_service.domain.interfaces.ComplianceDomain;
import com.bracepl.dbp_onboarding_service.domain.interfaces.RmDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.RmModel;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ComplianceService implements ComplianceUseCase {
    private final ComplianceDomain complianceDomain;
    private final ObjectMapper objectMapper;
    private final RmDomain rmDomain;

    public ComplianceService(ComplianceDomain complianceDomain, ObjectMapper objectMapper, RmDomain rmDomain) {
        this.complianceDomain = complianceDomain;
        this.objectMapper = objectMapper;
        this.rmDomain = rmDomain;
    }

    @Override
    public ServiceResponse getAcceptedAccounts() throws JsonProcessingException {
        List<Account> accountList = complianceDomain.getAllAcceptedAccount();
        if (accountList.isEmpty()){
            log.info("COULD NOT GET ACCEPTED ACCOUNTS...");
            return new ServiceResponse("Could not get accepted accounts");
        }
        return new ServiceResponse("Accounts retrieved successfully", objectMapper.writeValueAsString(accountList));
    }

    @Override
    public ServiceResponse getRms() throws JsonProcessingException {
        List<RmModel> rmModelList = rmDomain.getAllRms();
        if (rmModelList.isEmpty()){
            log.info("COULD NOT GET RMs...");
            return new ServiceResponse("Could not get Rms");
        }
        return new ServiceResponse("RM list retrieved successfully", objectMapper.writeValueAsString(rmModelList));
    }

    @Override
    public ServiceResponse activateAccount(List<ActivateAccountDto> activateAccountDto) throws JsonProcessingException {
        boolean isActivated = false;
        for (ActivateAccountDto activateAccount: activateAccountDto){
            isActivated = complianceDomain.activateAccount(activateAccount.getAccountId(), activateAccount.getRmId());
        }
        if (!isActivated){
            log.info("COULD NOT ACCEPT ACCOUNT...");
            return new ServiceResponse("Could not activate Account");
        }
        return new ServiceResponse("Account activated successfully", objectMapper.writeValueAsString(true));
    }

    @Override
    public ServiceResponse instantBoActivation(String accountId) throws JsonProcessingException {
        String rmId = complianceDomain.getSelfRmId();
        if (rmId.isEmpty()){
            log.info("COULD NOT ACTIVATE ACCOUNT... RM ID COULD NOT BE FOUND...");
            return new ServiceResponse("Could not activate Account");
        }
        boolean isActivated = complianceDomain.activateAccount(accountId, rmId);
        if (!isActivated){
            log.info("COULD NOT ACTIVATE ACCOUNT...");
            return new ServiceResponse("Could not activate Account");
        }
        return new ServiceResponse("Account activated successfully", objectMapper.writeValueAsString(true));
    }

    @Override
    public ServiceResponse approveProfileUpdateRequest(Account account) {
        return null;
    }

    @Override
    public ServiceResponse searchAccount(AccountSearchRequest accountSearchRequest) throws JsonProcessingException {
        List<Account> accountList = complianceDomain.searchAccount(accountSearchRequest);
        if (accountList.isEmpty()){
            log.info("COULD NOT GET ACCEPTED ACCOUNTS...");
            return new ServiceResponse("Could not get accepted accounts");
        }
        return new ServiceResponse("Accounts retrieved successfully", objectMapper.writeValueAsString(accountList));
    }
}
