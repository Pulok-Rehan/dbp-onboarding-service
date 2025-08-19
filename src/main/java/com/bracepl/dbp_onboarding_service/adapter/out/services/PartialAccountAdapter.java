package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.BankEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.JointAccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.ParitalAccount;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PartialAccountRepository;
import com.bracepl.dbp_onboarding_service.application.interfaces.PartialAccountDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PartialAccountAdapter implements PartialAccountDomain {
    private final PartialAccountRepository partialAccountRepository;

    public PartialAccountAdapter(PartialAccountRepository partialAccountRepository) {
        this.partialAccountRepository = partialAccountRepository;
    }

    @Override
    public Account save(Account account, boolean isActive) {
        try {
            ParitalAccount savedAccountEntity = partialAccountRepository.save(this.populateToAccountEntity(account, isActive));
            return addIdToAccountObject(account, savedAccountEntity.getId());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account saveWithJointAccount(Account account, boolean isActive) {
        try {
            ParitalAccount savedAccountEntity = partialAccountRepository.save(this.populateToAccountEntityWithJointAccount(account, isActive));
            return addIdToAccountObject(account, savedAccountEntity.getId());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account saveClientType(Account account) {
        try {
            ParitalAccount savedAccountEntity = partialAccountRepository.save(this.populateToAccountEntityWithClientType(account));
            return addIdToAccountObject(account, savedAccountEntity.getId());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account findByMoBileNumber(String mobileNumber) {
        try {
            Optional<ParitalAccount> optionalAccountEntity = partialAccountRepository.findByMobileNumber(mobileNumber);
            return optionalAccountEntity.map(this::populateToAccountModel).orElse(null);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public Account findByNid(String nid) {
        try {
            Optional<ParitalAccount> optionalAccountEntity = partialAccountRepository.findByNid(nid);
            return optionalAccountEntity.map(this::populateToAccountModel).orElse(null);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account findById(String id) {
        try {
            Optional<ParitalAccount> optionalAccountEntity = partialAccountRepository.findById(id);
            return optionalAccountEntity.map(this::populateToAccountModelAfterFinalCall).orElse(null);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private ParitalAccount populateToAccountEntity(Account account, boolean isActive){
        return ParitalAccount.builder()
                .id(account.getId())
                .investorCode(account.getInvestorCode())
                .name(account.getName())
                .emailAddress(account.getEmail())
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
                .bank(BankEntity.builder()
                        .id(account.getId())
                        .bankName(account.getBankName())
                        .routingNumber(account.getRoutingNumber())
                        .branchName(account.getBranchName())
                        .build())
                .accountNo(account.getAccountNo())
                .residency(account.getResidency())
                .boType(account.getBoType())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .photo(account.getPhoto())
                .boLinked(account.isBoLinked())
                .isActive(isActive)
                .build();
    }

    private ParitalAccount populateToAccountEntityWithJointAccount(Account account, boolean isActive){
        return ParitalAccount.builder()
                .id(account.getId())
                .investorCode(account.getInvestorCode())
                .name(account.getName())
                .emailAddress(account.getEmail())
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
                .bank(BankEntity.builder()
                        .id(account.getId())
                        .bankName(account.getBankName())
                        .routingNumber(account.getRoutingNumber())
                        .branchName(account.getBranchName())
                        .build())
                .accountNo(account.getAccountNo())
                .residency(account.getResidency())
                .boType(account.getBoType())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .photo(account.getPhoto())
                .boLinked(account.isBoLinked())
                .isActive(isActive)
                .jointAccountEntity(JointAccountEntity.builder()
                        .name(account.getJointAccountname())
                        .email(account.getJointAccountEmail())
                        .mobileNumbr(account.getJointAccountMobileNumbr())
                        .address(account.getJointAccountAddress()).build())
                .build();
    }

    private ParitalAccount populateToAccountEntityWithClientType(Account account){
        return ParitalAccount.builder()
                .id(account.getId())
                .investorCode(account.getInvestorCode())
                .name(account.getName())
                .emailAddress(account.getEmail())
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
                .bank(BankEntity.builder()
                        .id(account.getId())
                        .bankName(account.getBankName())
                        .routingNumber(account.getRoutingNumber())
                        .branchName(account.getBranchName())
                        .build())
                .accountNo(account.getAccountNo())
                .residency(account.getResidency())
                .boType(account.getBoType())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .jointAccountEntity(JointAccountEntity.builder()
                        .name(account.getJointAccountname())
                        .address(account.getJointAccountAddress())
                        .email(account.getJointAccountEmail())
                        .mobileNumbr(account.getJointAccountMobileNumbr())
                        .jointAccountSignature(account.getJointAccountSignature())
                        .jointAccountPhoto(account.getJointAccountPhoto())
                        .jointAccountNidBack(account.getJointAccountNidBack())
                        .jointAccountNidFront(account.getJointAccountNidFront())
                        .build())
                .build();
    }

    private Account populateToAccountModel(ParitalAccount account){
        return Account.builder()
                .id(account.getId())
                .nid(account.getNid())
                .fathersName(account.getFathersName())
                .mothersName(account.getMothersName())
                .dateOfBirth(account.getDateOfBirth())
                .addressLine1(account.getAddressLine1())
                .city(account.getCity())
                .country(account.getCountry())
                .state(account.getState())
                .zipCode(account.getZipCode())
                .investorCode(account.getInvestorCode())
                .email(account.getEmailAddress())
                .mobileNumber(account.getMobileNumber())
                .name(account.getName())
                .gender(account.getGender())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .bankName(account.getBank().getBankName())
                .routingNumber(account.getBank().getRoutingNumber())
                .branchName(account.getBank().getBranchName())
                .accountNo(account.getAccountNo())
                .boType(account.getBoType())
                .residency(account.getResidency())
                .active(account.isActive())
                .transactionStatus(account.getTransactionStatus())
                .build();
    }

    private Account populateToAccountModelAfterFinalCall(ParitalAccount account){
        return Account.builder()
                .id(account.getId())
                .nid(account.getNid())
                .fathersName(account.getFathersName())
                .mothersName(account.getMothersName())
                .dateOfBirth(account.getDateOfBirth())
                .addressLine1(account.getAddressLine1())
                .city(account.getCity())
                .country(account.getCountry())
                .state(account.getState())
                .zipCode(account.getZipCode())
                .investorCode(account.getInvestorCode())
                .email(account.getEmailAddress())
                .mobileNumber(account.getMobileNumber())
                .name(account.getName())
                .gender(account.getGender())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .bankName(account.getBank() != null ? account.getBank().getBankName() : null)
                .routingNumber(account.getBank() != null ? account.getBank().getRoutingNumber() : null)
                .branchName(account.getBank() != null ? account.getBank().getBranchName() : null)
                .accountNo(account.getAccountNo())
                .boType(account.getBoType())
                .residency(account.getResidency())
                .jointAccountname(account.getJointAccountEntity() != null ? account.getJointAccountEntity().getName() : null)
                .jointAccountEmail(account.getJointAccountEntity() != null ? account.getJointAccountEntity().getEmail() : null)
                .jointAccountMobileNumbr(account.getJointAccountEntity() != null ? account.getJointAccountEntity().getMobileNumbr() : null)
                .jointAccountAddress(account.getJointAccountEntity() != null ? account.getJointAccountEntity().getAddress() : null)
                .jointAccountSignature(account.getJointAccountEntity() != null ? account.getJointAccountEntity().getJointAccountSignature() : null)
                .jointAccountPhoto(account.getJointAccountEntity() != null ? account.getJointAccountEntity().getJointAccountPhoto() : null)
                .jointAccountNidBack(account.getJointAccountEntity() != null ? account.getJointAccountEntity().getJointAccountNidBack() : null)
                .jointAccountNidFront(account.getJointAccountEntity() != null ? account.getJointAccountEntity().getJointAccountNidFront() : null)
                .build();
    }

    private Account addIdToAccountObject(Account account, String id){
        account.setId(id);
        return account;
    }
}
