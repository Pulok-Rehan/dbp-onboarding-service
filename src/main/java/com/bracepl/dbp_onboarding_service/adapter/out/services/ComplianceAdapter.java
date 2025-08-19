package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.RmEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.RmRepository;
import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import com.bracepl.dbp_onboarding_service.domain.interfaces.ComplianceDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ComplianceAdapter implements ComplianceDomain {
    private final AccountRepository accountRepository;
    private final RmRepository rmRepository;

    public ComplianceAdapter(AccountRepository accountRepository, RmRepository rmRepository) {
        this.accountRepository = accountRepository;
        this.rmRepository = rmRepository;
    }

    @Override
    public boolean activateAccount(String accountId, String rmId) {
        try {
            Optional<AccountEntity> accountEntityOptional = accountRepository.findById(accountId);
            if (accountEntityOptional.isEmpty()){
                return false;
            }
            Optional<RmEntity> rmEntityOptional = rmRepository.findById(rmId);
            if (rmEntityOptional.isEmpty()){
                return false;
            }
            accountEntityOptional.get().setAccountStatus(AccountStatus.ACTIVE);
            accountEntityOptional.get().setRmId(rmEntityOptional.get().getId());
            accountRepository.save(accountEntityOptional.get());
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getSelfRmId() {
        return "";
    }

    @Override
    public List<Account> getAllAcceptedAccount() {
        try {
            List<AccountStatus> accountStatusList = new ArrayList<>();
            accountStatusList.add(AccountStatus.ACCEPTED);
            List<AccountEntity> accountEntityList = accountRepository.findByAccountStatusIn(accountStatusList);
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
                    .build();
            accountList.add(account);
        }
        return accountList;
    }
}
