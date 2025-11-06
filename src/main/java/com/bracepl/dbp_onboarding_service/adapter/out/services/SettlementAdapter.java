package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.EditRequired;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.ParitalAccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.EditRequiredRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PartialAccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import com.bracepl.dbp_onboarding_service.domain.interfaces.SettlementDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.PartialAccount;
import com.bracepl.dbp_onboarding_service.domain.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class SettlementAdapter implements SettlementDomain {
    private final AccountRepository accountRepository;
    private final PartialAccountRepository partialAccountRepository;
    private final EditRequiredRepository editRequiredRepository;

    public SettlementAdapter(AccountRepository accountRepository, PartialAccountRepository partialAccountRepository, EditRequiredRepository editRequiredRepository) {
        this.accountRepository = accountRepository;
        this.partialAccountRepository = partialAccountRepository;
        this.editRequiredRepository = editRequiredRepository;
    }

    @Override
    public List<Account> getAllRequestedAccounts() {
        try {
            List<AccountEntity> accountEntityList = accountRepository.findByAccountStatusNot(AccountStatus.ACTIVE);
            return populateToAccountModelList(accountEntityList);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, String>> getCodeAndMobileNumberList() {
        try {
            List<Map<String, String>> codeAndMobileNumbersList = new ArrayList<>();
            List<AccountEntity> accountEntityList = accountRepository.findAll();

            if (accountEntityList.isEmpty()) {
                return new ArrayList<>();
            }

            for (AccountEntity accountEntity : accountEntityList) {
                Map<String, String> map = new HashMap<>();
                map.put("investorCode", accountEntity.getInvestorCode());
                map.put("mobileNumber", accountEntity.getMobileNumber());
                codeAndMobileNumbersList.add(map);
            }

            return codeAndMobileNumbersList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Account getRequestedAccount(String accountId) {
        try {
            Optional<AccountEntity> accountEntityOptional = accountRepository.findById(accountId);
            if (accountEntityOptional.isEmpty()){
                return null;
            }
            return populateToAccountModel(accountEntityOptional.get());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String requiresEdit(EditAccountRequest editAccountRequest) {
        Optional<ParitalAccountEntity> accountEntityOptional = partialAccountRepository.findById(editAccountRequest.getAccountId());
        if (accountEntityOptional.isEmpty()) {
            log.info("COULD NOT GET ACCOUNT INFORMATION WITH THIS ID: {}", editAccountRequest.getAccountId());
            return "";
        }

        Optional<EditRequired> editRequiredOptional = editRequiredRepository.findByAccountId(editAccountRequest.getAccountId());

        EditRequired editRequired;

        if (editRequiredOptional.isEmpty()) {
            // Create new entry if none exists
            editRequired = EditRequired.fromRequest(editAccountRequest);
            log.info("CREATED NEW EditRequired ENTRY FOR ACCOUNT ID: {}", editAccountRequest.getAccountId());
        } else {
            // Update existing record with provided values only
            editRequired = editRequiredOptional.get();
            updateEditRequired(editRequired, editAccountRequest);
            log.info("UPDATED EXISTING EditRequired ENTRY FOR ACCOUNT ID: {}", editAccountRequest.getAccountId());
        }

        editRequiredRepository.save(editRequired);

        return "Account Update Request sent";


    }

    @Override
    public List<Account> acceptRequestedAccount(List<String> accountIds) {
        List<Account> acceptedAccounts = new ArrayList<>();
        try {
            Account account = new Account();
            for (String accountId : accountIds){
                Optional<AccountEntity> accountEntityOptional = accountRepository.findById(accountId);
                if (accountEntityOptional.isEmpty()){
                    break;
                }
                accountEntityOptional.get().setAccountStatus(AccountStatus.ACCEPTED);
                AccountEntity savedAccount = accountRepository.save(accountEntityOptional.get());
                account = this.populateToAccountModel(savedAccount);
                acceptedAccounts.add(account);
            }
            if (acceptedAccounts.isEmpty()){
                return null;
            }
            return acceptedAccounts;

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account requestForChange() {
        return null;
    }

    @Override
    public List<Account> searchAccount(AccountSearchRequest accountSearchRequest) {
        try {
            List<AccountEntity> accountEntityList = accountRepository.searchAccounts(accountSearchRequest);
            log.info("POPULATING TO ACCOUNT MODEL...");
            return populateToAccountModelList(accountEntityList);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Account> populateToAccountModelList(List<AccountEntity> accountEntityList){
        List<Account> accountList = new ArrayList<>();
        for (AccountEntity accountEntity: accountEntityList) {
            Account account = Account.builder()
                    .id(accountEntity.getId())
                    .investorCode(accountEntity.getInvestorCode())
                    .name(accountEntity.getName())
                    .email(accountEntity.getEmailAddress())
                    .mobileNumber(accountEntity.getMobileNumber())
                    .gender(accountEntity.getGender())
                    .nid(accountEntity.getNid())
                    .fathersName(accountEntity.getFathersName())
                    .mothersName(accountEntity.getMothersName())
                    .dateOfBirth(accountEntity.getDateOfBirth())
                    .addressLine1(accountEntity.getAddressLine1())
                    .city(accountEntity.getCity())
                    .country(accountEntity.getCountry())
                    .state(accountEntity.getState())
                    .zipCode(accountEntity.getZipCode())
                    .bankName(accountEntity.getBank().getBankName())
                    .routingNumber(accountEntity.getBank().getRoutingNumber())
                    .branchName(accountEntity.getBank().getBranchName())
                    .accountNo(accountEntity.getAccountNo())
                    .residency(accountEntity.getResidency())
                    .boType(accountEntity.getBoType())
                    .nidFront(accountEntity.getNidFront())
                    .nidBack(accountEntity.getNidBack())
                    .photo(accountEntity.getPhoto())
                    .signature(accountEntity.getSignature())
                    .chequeLeaf(accountEntity.getChequeLeaf())
                    .accountStatus(accountEntity.getAccountStatus())
                    .createdAt(DateUtils.formatDateTime(accountEntity.getCreatedAt()))
                    .updatedAt(DateUtils.formatDateTime(accountEntity.getUpdatedAt()))
                    .build();
            accountList.add(account);
        }
        return accountList;
    }

    private Account populateToAccountModel(AccountEntity account) {
        return Account.builder()
                .id(account.getId())
                .investorCode(account.getInvestorCode())
                .name(account.getName())
                .email(account.getEmailAddress())
                .mobileNumber(account.getMobileNumber())
                .gender(account.getGender())
                .nid(account.getNid())
                .fathersName(account.getFathersName())
                .mothersName(account.getMothersName())
                .dateOfBirth(account.getDateOfBirth())
                .addressLine1(account.getAddressLine1())
                .city(account.getCity())
                .country(account.getCountry())
                .state(account.getState())
                .zipCode(account.getZipCode())
                .bankName(account.getBank().getBankName())
                .routingNumber(account.getBank().getRoutingNumber())
                .branchName(account.getBank().getBranchName())
                .accountNo(account.getAccountNo())
                .residency(account.getResidency())
                .boType(account.getBoType())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .boNumber(account.getBoNumber())
                .accountStatus(account.getAccountStatus())
                .rmId(account.getRmId())
                .createdAt(DateUtils.formatDateTime(account.getCreatedAt()))
                .updatedAt(DateUtils.formatDateTime(account.getUpdatedAt()))
                .build();
    }

    private void updateEditRequired(EditRequired existing, EditAccountRequest request) {
        if (request.isPhotoSection()) existing.setPhotoSection(true);
        if (request.isPersonalDetailsSection()) existing.setPersonalDetailsSection(true);
        if (request.isBankDetailsSection()) existing.setBankDetailsSection(true);
        if (request.isAddressSection()) existing.setAddressSection(true);
        if (request.isDocumentsSection()) existing.setDocumentsSection(true);

        if (request.getPersonalDetailsDto() != null)
            existing.setPersonalDetailsDto(request.getPersonalDetailsDto());

        if (request.getBankDetailsDto() != null)
            existing.setBankDetailsDto(request.getBankDetailsDto());

        if (request.getAddressDto() != null)
            existing.setAddressDto(request.getAddressDto());

        if (request.getDocumentsDto() != null)
            existing.setDocumentsDto(request.getDocumentsDto());

        if (request.getRequestedBy() != null)
            existing.setRequestedBy(request.getRequestedBy());
    }

}
