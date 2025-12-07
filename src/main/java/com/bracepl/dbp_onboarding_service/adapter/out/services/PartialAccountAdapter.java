package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.BankEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.CsdEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.JointAccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.ParitalAccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.CsdRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PartialAccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.application.interfaces.PartialAccountDomain;
import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Component
public class PartialAccountAdapter implements PartialAccountDomain {
    @Value("${clientPortalRm}")
    private String clientPortalRm;
    @Value("${clientPortalBranch}")
    private String clientPortalBranch;
    private final PartialAccountRepository partialAccountRepository;
    private final CsdRepository csdRepository;

    public PartialAccountAdapter(PartialAccountRepository partialAccountRepository, CsdRepository csdRepository) {
        this.partialAccountRepository = partialAccountRepository;
        this.csdRepository = csdRepository;
    }

    @Override
    public Account save(Account account, boolean isActive) {
        try {
            List<CsdEntity> csdEntities = csdRepository.findAll();
            if (!csdEntities.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(csdEntities.size());
                String csdId = csdEntities.get(randomIndex).getId();
                ParitalAccountEntity savedAccountEntity = partialAccountRepository.save(this.populateToAccountEntity(account, isActive, csdId));
                return addIdToAccountObject(account, savedAccountEntity.getId());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account saveWithJointAccount(Account account, boolean isActive) {
        try {
            ParitalAccountEntity savedAccountEntity = partialAccountRepository.save(this.populateToAccountEntityWithJointAccount(account, isActive));
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
            ParitalAccountEntity savedAccountEntity = partialAccountRepository.save(this.populateToAccountEntityWithClientType(account));
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
            Optional<ParitalAccountEntity> optionalAccountEntity = partialAccountRepository.findByMobileNumber(mobileNumber);
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
            Optional<ParitalAccountEntity> optionalAccountEntity = partialAccountRepository.findByNid(nid);
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
            Optional<ParitalAccountEntity> optionalAccountEntity = partialAccountRepository.findById(id);
            return optionalAccountEntity.map(this::populateToAccountModelAfterFinalCall).orElse(null);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private ParitalAccountEntity populateToAccountEntity(Account account, boolean isActive, String csdId){
        return ParitalAccountEntity.builder()
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
                .accountStatus(AccountStatus.INITIATED.name())
                .csdId(csdId)
                .rm(account.getRmId()== null ? clientPortalRm : account.getRmId())
                .preferedBranch(account.getPreferedBranch()== null ? clientPortalBranch : account.getPreferedBranch())
                .build();
    }

    private ParitalAccountEntity populateToAccountEntityWithJointAccount(Account account, boolean isActive){
        return ParitalAccountEntity.builder()
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
                .rm(account.getRmId()== null ? clientPortalRm : account.getRmId())
                .preferedBranch(account.getPreferedBranch()== null ? clientPortalBranch : account.getPreferedBranch())
                .jointAccountEntity(JointAccountEntity.builder()
                        .name(account.getJointAccountname())
                        .email(account.getJointAccountEmail())
                        .mobileNumbr(account.getJointAccountMobileNumbr())
                        .address(account.getJointAccountAddress()).build())
                .build();
    }

    private ParitalAccountEntity populateToAccountEntityWithClientType(Account account){
        return ParitalAccountEntity.builder()
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
                .rm(account.getRmId()== null ? clientPortalRm : account.getRmId())
                .preferedBranch(account.getPreferedBranch()== null ? clientPortalBranch : account.getPreferedBranch())
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

    private Account populateToAccountModel(ParitalAccountEntity account){
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
                .rmId(account.getRm())
                .preferedBranch(account.getPreferedBranch())
                .build();
    }

    private Account populateToAccountModelAfterFinalCall(ParitalAccountEntity account){
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
                .rmId(account.getRm())
                .preferedBranch(account.getPreferedBranch())
                .build();
    }

    private Account addIdToAccountObject(Account account, String id){
        account.setId(id);
        return account;
    }

    private void updateAccountFromRequest(ParitalAccountEntity account, EditAccountRequest request) throws IOException {

        // ✅ Personal Details Section
        if (request.isPersonalDetailsSection() && request.getPersonalDetailsDto() != null) {
            var personal = request.getPersonalDetailsDto();
            if (personal.getName() != null) account.setName(personal.getName());
            if (personal.getGender() != null) account.setGender(personal.getGender());
            if (personal.getNid() != null) account.setNid(personal.getNid());
            if (personal.getFathersName() != null) account.setFathersName(personal.getFathersName());
            if (personal.getMothersName() != null) account.setMothersName(personal.getMothersName());
            if (personal.getDateOfBirth() != null) account.setDateOfBirth(personal.getDateOfBirth().toString());
        }

        // ✅ Bank Details Section
        if (request.isBankDetailsSection() && request.getBankDetailsDto() != null) {
            var bankDetails = request.getBankDetailsDto();

            BankEntity bankEntity = account.getBank() != null ? account.getBank() : new BankEntity();

            if (bankDetails.getBankName() != null) bankEntity.setBankName(bankDetails.getBankName());
            if (bankDetails.getBranchName() != null) bankEntity.setBranchName(bankDetails.getBranchName());
            if (bankDetails.getRoutingNumber() != null) bankEntity.setRoutingNumber(bankDetails.getRoutingNumber());
            if (bankDetails.getAccountNo() != null) account.setAccountNo(bankDetails.getAccountNo());

            account.setBank(bankEntity);
        }

        // ✅ Address Section
        if (request.isAddressSection() && request.getAddressDto() != null) {
            var address = request.getAddressDto();
            if (address.getAddressLine1() != null) account.setAddressLine1(address.getAddressLine1());
            if (address.getCity() != null) account.setCity(address.getCity());
            if (address.getCountry() != null) account.setCountry(address.getCountry());
            if (address.getState() != null) account.setState(address.getState());
            if (address.getZipCode() != null) account.setZipCode(address.getZipCode());
        }

        // ✅ Documents Section
        if (request.isDocumentsSection() && request.getDocumentsDto() != null) {
            var docs = request.getDocumentsDto();

            if (docs.getPhoto() != null) account.setPhoto(docs.getPhoto().getBytes().toString());
            if (docs.getSignature() != null) account.setSignature(docs.getSignature().getBytes().toString());
            if (docs.getChequeLeaf() != null) account.setChequeLeaf(docs.getChequeLeaf().getBytes().toString());
        }

        // ✅ Photo Section (if you want to update standalone photo)
        if (request.isPhotoSection() && request.getDocumentsDto() != null && request.getDocumentsDto().getPhoto() != null) {
            account.setPhoto(request.getDocumentsDto().getPhoto().getBytes().toString());
        }
    }

}
