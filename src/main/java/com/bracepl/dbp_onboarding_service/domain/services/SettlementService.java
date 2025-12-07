package com.bracepl.dbp_onboarding_service.domain.services;

import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.application.interfaces.SettlementUseCase;
import com.bracepl.dbp_onboarding_service.changeRequest.dto.ChangeRequestDto;
import com.bracepl.dbp_onboarding_service.domain.interfaces.SettlementDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class SettlementService implements SettlementUseCase {
    private final SettlementDomain settlementDomain;
    private final ObjectMapper objectMapper;

    public SettlementService(SettlementDomain settlementDomain, ObjectMapper objectMapper) {
        this.settlementDomain = settlementDomain;
        this.objectMapper = objectMapper;
    }

    @Override
    public ServiceResponse getAccountOpeningRequests() throws JsonProcessingException {
        List<Account> accountList = settlementDomain.getAllRequestedAccounts();
        if (accountList.isEmpty()){
            log.info("COULD NOT GET ANY ACCOUNT OPENING REQUEST...");
            return new ServiceResponse("Could not get any account opening request");
        }
        return new ServiceResponse("Account opening requests retrived", objectMapper.writeValueAsString(accountList));
    }

    @Override
    public ServiceResponse getCodeAndMobileNumberList() throws JsonProcessingException {
        List<Map<String, String>> codeAndMobileNumbers = settlementDomain.getCodeAndMobileNumberList();
        if (codeAndMobileNumbers.isEmpty()){
            log.info("COULD NOT GET ANY ACCOUNT OPENING REQUEST...");
            return new ServiceResponse("Could not get any account opening request");
        }
        return new ServiceResponse("Account opening requests retrived", objectMapper.writeValueAsString(codeAndMobileNumbers));
    }

    @Override
    public ServiceResponse getAccountOpeningRequest(String accountId) throws JsonProcessingException {
        Account account = settlementDomain.getRequestedAccount(accountId);
        if (account == null){
            log.info("COULD NOT GET ACCOUNT OPENING REQUEST...");
            return new ServiceResponse("Could not get account opening request");
        }
        return new ServiceResponse("Account opening request retrived", objectMapper.writeValueAsString(account));
    }

    @Override
    public ServiceResponse acceptAccount(List<String> accountIds) throws JsonProcessingException {
        List<Account> accounts = settlementDomain.acceptRequestedAccount(accountIds);
        if (accounts.isEmpty() || accounts == null){
            log.info("COULD NOT ACCEPT ACCOUNT...");
            return new ServiceResponse("Could not accept account opening request");
        }
        return new ServiceResponse("Account opening request accepted successfully", objectMapper.writeValueAsString(accounts));
    }

    @Override
    public ServiceResponse requsetForChange() {
        return null;
    }

    @Override
    public ServiceResponse searchAccount(AccountSearchRequest accountSearchRequest) throws JsonProcessingException {
        log.info("SEARCHING ACCOUNTS FOR SETTLEMENT...{}", accountSearchRequest.toString());
        List<Account> accountList = settlementDomain.searchAccount(accountSearchRequest);
        if (accountList.isEmpty()){
            log.info("COULD NOT GET ANY ACCOUNT OPENING REQUEST...");
            return new ServiceResponse("Could not get any account opening request");
        }
        log.info("ACCOUNTS RETRIEVED SUCCESSFULLY...");
        return new ServiceResponse("Account opening requests retrived", objectMapper.writeValueAsString(accountList));
    }

    @Override
    public ServiceResponse requireEdit(ChangeRequestDto changeRequestDto) throws JsonProcessingException {
        log.info("EDIT REQUEST FOR ACCOUNTS FOR SETTLEMENT...{}", changeRequestDto.getAccountId());
        String account = settlementDomain.requiresEdit(changeRequestDto);
        if (Objects.equals(account, "") || account == null){
            log.info("COULD NOT GET ACCOUNT OPENING REQUEST...");
            return new ServiceResponse("Could not get account opening request");
        }
        return new ServiceResponse("Account opening request retrived", account);
    }
}
