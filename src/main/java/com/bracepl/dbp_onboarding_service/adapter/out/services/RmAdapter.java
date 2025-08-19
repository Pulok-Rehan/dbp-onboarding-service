package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.RmEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.RmRepository;
import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import com.bracepl.dbp_onboarding_service.domain.interfaces.RmDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.RmModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class RmAdapter implements RmDomain {
    private final RmRepository rmRepository;
    private final AccountRepository accountRepository;

    public RmAdapter(RmRepository rmRepository, AccountRepository accountRepository) {
        this.rmRepository = rmRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<RmModel> getAllRms() {
        List<RmEntity> rmEntityList = rmRepository.findAll();
        if(rmEntityList.isEmpty()){
            log.info("COULD NOT FIND RM LIST...");
            return List.of();
        }
        return this.populateToRmModelList(rmEntityList);

    }

    @Override
    public List<Account> getAllClients(String rmId) {
        try {
            List<AccountEntity> accountEntityList = accountRepository.findByRmId(rmId);
            return populateToAccountModelList(accountEntityList);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean acceptCLient(String clientId) {
        try {
            Optional<AccountEntity> clientAccount = accountRepository.findById(clientId);
            if (clientAccount.isEmpty()){
                return false;
            }
            clientAccount.get().setRmAccepted(true);
            accountRepository.save(clientAccount.get());
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean rejectCLient(String clientId, String reason) {
        try {
            Optional<AccountEntity> clientAccount = accountRepository.findById(clientId);
            if (clientAccount.isEmpty()){
                return false;
            }
            clientAccount.get().setRmId(null);
            clientAccount.get().setAccountStatus(AccountStatus.ACCEPTED);
            clientAccount.get().setRmAccepted(false);
            accountRepository.save(clientAccount.get());
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean contactClient(String clientId) {
        try {
            Optional<AccountEntity> clientAccount = accountRepository.findById(clientId);
            if (clientAccount.isEmpty()){
                return false;
            }
            clientAccount.get().setRmContacted(true);
            accountRepository.save(clientAccount.get());
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private List<RmModel> populateToRmModelList(List<RmEntity> rmEntityList){
        List<RmModel> rmModelList = new ArrayList<>();
        for (RmEntity rmEntity : rmEntityList){
            RmModel rmModel = RmModel.builder()
                    .id(rmEntity.getId())
                    .name(rmEntity.getName())
                    .emolyeeCode(rmEntity.getEmolyeeCode()).build();
            rmModelList.add(rmModel);
        }
        return rmModelList;
    }

    private List<Account> populateToAccountModelList(List<AccountEntity> accountEntityList) {
        List<Account> accountList = new ArrayList<>();
        for (AccountEntity accountEntity : accountEntityList) {
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
