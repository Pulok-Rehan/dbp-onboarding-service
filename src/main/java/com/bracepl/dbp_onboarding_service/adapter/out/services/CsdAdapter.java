package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.*;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.ClientRemarksRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.CsdRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PartialAccountRepository;
import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import com.bracepl.dbp_onboarding_service.domain.interfaces.CsdDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.ClientRemarkModel;
import com.bracepl.dbp_onboarding_service.domain.models.PartialAccount;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CsdAdapter implements CsdDomain {
    private final CsdRepository csdRepository;
    private final ClientRemarksRepository clientRemarksRepository;
    private final AccountRepository accountRepository;
    private final PartialAccountRepository partialAccountRepository;

    public CsdAdapter(CsdRepository csdRepository, ClientRemarksRepository clientRemarksRepository, AccountRepository accountRepository, PartialAccountRepository partialAccountRepository) {
        this.csdRepository = csdRepository;
        this.clientRemarksRepository = clientRemarksRepository;
        this.accountRepository = accountRepository;
        this.partialAccountRepository = partialAccountRepository;
    }

    @Override
    public boolean addRemark(ClientRemarkModel clientRemarkModel) {
//        Optional<CsdEntity> csdEntityOptional = csdRepository.findByMo(csdId);
//        csd ID is not there
        try {
            if (clientRemarkModel.getAccountStatus().equals(AccountStatus.INITIATED.name())){
                Optional<ParitalAccountEntity> accountEntityOptional = partialAccountRepository.findById(clientRemarkModel.getAccountId());
                if (accountEntityOptional.isEmpty()){
                    return false;
                }
                clientRemarksRepository.save(ClientRemarks.builder()
                        .userId(clientRemarkModel.getCsdId())
                        .accountId(clientRemarkModel.getAccountId())
                        .remarks(clientRemarkModel.getRemarks()).build());

                accountEntityOptional.get().setCsdContacted(true);
                accountEntityOptional.get().setRemark(clientRemarkModel.getRemarks());
                partialAccountRepository.save(accountEntityOptional.get());
            }else {
                Optional<AccountEntity> accountEntityOptional = accountRepository.findById(clientRemarkModel.getAccountId());
                if (accountEntityOptional.isEmpty()){
                    return false;
                }
                clientRemarksRepository.save(ClientRemarks.builder()
                        .userId(clientRemarkModel.getCsdId())
                        .accountId(clientRemarkModel.getAccountId())
                        .remarks(clientRemarkModel.getRemarks()).build());

                accountEntityOptional.get().setCsdContaced(true);
                accountEntityOptional.get().setRemark(clientRemarkModel.getRemarks());
                accountRepository.save(accountEntityOptional.get());
            }

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Account> getAllClientsfinal(String mobileNumber) {
        try {
            List<String> statusList = new ArrayList<>();
            statusList.add(AccountStatus.REQUESTED.name());
            Optional<CsdEntity> csdEntityOptional = csdRepository.findByMobileNumber(mobileNumber);
            if (csdEntityOptional.isEmpty()){
                return null;
            }
            List<AccountEntity> accountEntityList = accountRepository.findByCsdIdAndAccountStatusIn(csdEntityOptional.get().getId(), statusList);
            return populateToAccountModelList(accountEntityList);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Account> getAllClientsInitiated(String mobileNumber) {
        try {
            List<String> statusList = new ArrayList<>();
            statusList.add(AccountStatus.INITIATED.name());
            Optional<CsdEntity> csdEntityOptional = csdRepository.findByMobileNumber(mobileNumber);
            if (csdEntityOptional.isEmpty()){
                return null;
            }
            List<ParitalAccountEntity> accountEntityList = partialAccountRepository.findByCsdIdAndAccountStatusIn(csdEntityOptional.get().getId(), statusList);
            return populateToAccountModelListForPartials(accountEntityList);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Account> getAllClients(String mobileNumber) {
        return List.of();
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
                    .rmId(accountEntity.getRmId())
                    .csdId(accountEntity.getCsdId())
                    .rmAccepted(accountEntity.isRmAccepted())
                    .csdContacted(accountEntity.isCsdContaced())
                    .rmContacted(accountEntity.isRmContacted())
                    .remark(accountEntity.getRemark())
                    .build();
            accountList.add(account);
        }
        return accountList;
    }


    private List<Account> populateToAccountModelListForPartials(List<ParitalAccountEntity> accountEntityList) {
        List<Account> accountList = new ArrayList<>();
        for (ParitalAccountEntity accountEntity : accountEntityList) {
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
                    .accountStatus(AccountStatus.valueOf(accountEntity.getAccountStatus()))
                    .csdId(accountEntity.getCsdId())
                    .csdContacted(accountEntity.isCsdContacted())
                    .remark(accountEntity.getRemark())
                    .build();
            accountList.add(account);
        }
        return accountList;
    }
}
