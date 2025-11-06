package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountsForCsd;
import com.bracepl.dbp_onboarding_service.application.dtos.RemarkDto;
import com.bracepl.dbp_onboarding_service.application.interfaces.CsdUseCase;
import com.bracepl.dbp_onboarding_service.domain.interfaces.CsdDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.ClientRemarkModel;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CsdService implements CsdUseCase {
    private final CsdDomain csdDomain;
    private final ObjectMapper objectMapper;

    public CsdService(CsdDomain csdDomain, ObjectMapper objectMapper) {
        this.csdDomain = csdDomain;
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceResponse addRemark(RemarkDto remarkDto) {
        boolean issaved = csdDomain.addRemark(ClientRemarkModel.builder()
                .csdId("123")
                .accountId(remarkDto.getAccountId())
                .remarks(remarkDto.getRemark())
                .accountStatus(remarkDto.getAccountStatus()).build());
        if (!issaved){
            return new ServiceResponse("Could not save remarks");
        }
        return new ServiceResponse("Client remarks saved successfully.", String.valueOf(true));
    }

    @Override
    public ServiceResponse getCLient(String accountId) {
        return null;
    }

    @Override
    public ServiceResponse getClientsFinal(String mobileNumber) throws JsonProcessingException {
        List<Account> accountList = csdDomain.getAllClientsfinal(mobileNumber);
        if (accountList.isEmpty()){
            log.info("COULD NOT GET ANY CLIENTS...");
            return new ServiceResponse("Could not get any client");
        }
        return new ServiceResponse("Clients retrieved successfully", objectMapper.writeValueAsString(accountList));

    }

    public ServiceResponse getClientsInitiated(String mobileNumber) throws JsonProcessingException {
        List<Account> accountList = csdDomain.getAllClientsInitiated(mobileNumber);
        if (accountList.isEmpty()){
            log.info("COULD NOT GET ANY CLIENTS...");
            return new ServiceResponse("Could not get any client");
        }
        return new ServiceResponse("Clients retrieved successfully", objectMapper.writeValueAsString(accountList));

    }

    @Override
    public ServiceResponse getClients(String mobileNumber) throws JsonProcessingException {
        List<Account> accountListInitiated = csdDomain.getAllClientsInitiated(mobileNumber);
//        if (accountListInitiated.isEmpty()){
//            log.info("COULD NOT GET ANY CLIENTS...");
//            return new ServiceResponse("Could not get any client");
//        }
        List<Account> accountListFinal = csdDomain.getAllClientsfinal(mobileNumber);
//        if (accountListFinal.isEmpty()){
//            log.info("COULD NOT GET ANY CLIENTS...");
//            return new ServiceResponse("Could not get any client");
//        }
        return new ServiceResponse("Clients retrieved successfully", objectMapper.writeValueAsString(AccountsForCsd.builder()
                .accountListInitiated(accountListInitiated)
                .accountListFinal(accountListFinal).build()));

    }
}
