package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.EditRequired;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.EditRequiredRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PartialAccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.models.AccountSearchRequest;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.changeRequest.AccountSection;
import com.bracepl.dbp_onboarding_service.changeRequest.ChangeRequestEntity;
import com.bracepl.dbp_onboarding_service.changeRequest.FieldChangeDetail;
import com.bracepl.dbp_onboarding_service.changeRequest.SectionChangeRequest;
import com.bracepl.dbp_onboarding_service.changeRequest.dto.ChangeRequestDto;
import com.bracepl.dbp_onboarding_service.changeRequest.dto.FieldChangeDto;
import com.bracepl.dbp_onboarding_service.changeRequest.dto.SectionChangeDto;
import com.bracepl.dbp_onboarding_service.changeRequest.repo.ChangeRequestRepository;
import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import com.bracepl.dbp_onboarding_service.domain.interfaces.SettlementDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class SettlementAdapter implements SettlementDomain {
    private final AccountRepository accountRepository;
    private final PartialAccountRepository partialAccountRepository;
    private final EditRequiredRepository editRequiredRepository;
    private final ChangeRequestRepository changeRequestRepository;

    public SettlementAdapter(AccountRepository accountRepository, PartialAccountRepository partialAccountRepository, EditRequiredRepository editRequiredRepository, ChangeRequestRepository changeRequestRepository) {
        this.accountRepository = accountRepository;
        this.partialAccountRepository = partialAccountRepository;
        this.editRequiredRepository = editRequiredRepository;
        this.changeRequestRepository = changeRequestRepository;
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
    public String requiresEdit(ChangeRequestDto changeRequestDto) {

        try {
            if (changeRequestDto.getAccountId() == null){
                return null;
            }
            Optional<AccountEntity> partialAccount = accountRepository.findById(changeRequestDto.getAccountId());
            if (partialAccount.isEmpty()){
                return null;
            }
            ChangeRequestEntity changeRequest = ChangeRequestEntity.builder()
                    .requestedAt(LocalDateTime.now())
                    .requestedBy(changeRequestDto.getAdminId())
                    .accountId(changeRequestDto.getAccountId())
                    .partialAccountId(partialAccount.get().getId())
                    .requestedFor(partialAccount.get().getMobileNumber())
                    .adminRemarks(changeRequestDto.getRemarks())
                    .build();
            List<SectionChangeRequest> sectionChangeRequestList = new ArrayList<>();
            List<FieldChangeDetail> fieldChangeDetailList = new ArrayList<>();
            AccountSection accountSection = AccountSection.EKYC;
            for (SectionChangeDto sectionChangeDto : changeRequestDto.getSectionChanges()){
                int stepNumber = 0;
                // EKYC, PERSONAL_DETAILS, ADDRESS, BANK_DETAILS, CLIENT_TYPE, DOCUMENTS
                if (sectionChangeDto.getSection().equalsIgnoreCase("EKYC")){
                    stepNumber = 1;

                } else if (sectionChangeDto.getSection().equalsIgnoreCase("PERSONAL_DETAILS")) {
                    stepNumber = 2;
                    accountSection = AccountSection.PERSONAL_DETAILS;
                }
                else if (sectionChangeDto.getSection().equalsIgnoreCase("ADDRESS")) {
                    stepNumber = 3;
                    accountSection = AccountSection.ADDRESS;
                }
                else if (sectionChangeDto.getSection().equalsIgnoreCase("BANK_DETAILS")) {
                    stepNumber = 4;
                    accountSection = AccountSection.BANK_DETAILS;
                }
                else if (sectionChangeDto.getSection().equalsIgnoreCase("CLIENT_TYPE")) {
                    stepNumber = 5;
                    accountSection = AccountSection.CLIENT_TYPE;
                }
                else {
                    stepNumber = 6;
                    accountSection = AccountSection.DOCUMENTS;
                }
                for (FieldChangeDto fieldChangeDto : sectionChangeDto.getFields()){
                    fieldChangeDetailList.add(FieldChangeDetail.builder()
                            .fieldName(fieldChangeDto.getFieldName())
                            .currentValue(fieldChangeDto.getCurrentValue())
                            .isMandatory(true)
                            .reason(fieldChangeDto.getReason())
                            .suggestedValue(fieldChangeDto.getSuggestedValue() == null ? "Please change it" : fieldChangeDto.getSuggestedValue())
                            .build());
                }

                SectionChangeRequest sectionChangeRequest = SectionChangeRequest.builder()
                        .section(accountSection)
                        .completed(false)
                        .fields(fieldChangeDetailList)
                        .build();
            }
            changeRequest.setSectionChanges(sectionChangeRequestList);
            changeRequestRepository.save(changeRequest);
            return "Change request sent";
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }



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
