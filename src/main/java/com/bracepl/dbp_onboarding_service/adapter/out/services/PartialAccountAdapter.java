package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.*;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.CsdRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.NomineeRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PartialAccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.models.EditAccountRequest;
import com.bracepl.dbp_onboarding_service.application.interfaces.PartialAccountDomain;
import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import com.bracepl.dbp_onboarding_service.domain.models.Account;
import com.bracepl.dbp_onboarding_service.domain.models.Nominee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class PartialAccountAdapter implements PartialAccountDomain {
    @Value("${clientPortalRm}")
    private String clientPortalRm;
    @Value("${clientPortalBranch}")
    private String clientPortalBranch;
    private final PartialAccountRepository partialAccountRepository;
    private final CsdRepository csdRepository;
    private final NomineeRepository nomineeRepository;
    private final ObjectMapper objectMapper;

    public PartialAccountAdapter(PartialAccountRepository partialAccountRepository, CsdRepository csdRepository, NomineeRepository nomineeRepository, ObjectMapper objectMapper) {
        this.partialAccountRepository = partialAccountRepository;
        this.csdRepository = csdRepository;
        this.nomineeRepository = nomineeRepository;
        this.objectMapper = objectMapper;
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

    @Override
    public Account saveWithNominees(Account account, List<Nominee> nominees) {
        try {
            List<NomineeEntity> savedNomineeEntities = new ArrayList<>();
            if (nominees != null) {
                for (Nominee nominee : nominees) {
                    NomineeEntity nomineeEntity = NomineeEntity.builder()
                            .name(nominee.getName())
                            .nid(nominee.getNid())
                            .percentage(nominee.getPercentage())
                            .relation(nominee.getRelation())
                            .city(nominee.getCity())
                            .country(nominee.getCountry())
                            .state(nominee.getState())
                            .zipCode(nominee.getZipCode())
                            .address(nominee.getAddress())
                            .minor(nominee.isMinor())
                            .nomineeDob(nominee.getNomineeDob())
                            .mobileNumber(nominee.getMobileNumber())
                            .nomineeNidFront(nominee.getNomineeNidFront())
                            .nomineeNidBack(nominee.getNomineeNidBack())
                            .guardianNidFront(nominee.getGuardianNidFront())
                            .guardianNidBack(nominee.getGuardianNidBack())
                            .guardianNidNumber(nominee.getGuardianNidNumber())
                            .nomineePhoto(nominee.getNomineePhoto())
                            .nomineeSignature(nominee.getNomineeSignature())
                            .build();
                    savedNomineeEntities.add(nomineeRepository.save(nomineeEntity));
                }
            }

            ParitalAccountEntity accountEntity = this.populateToAccountEntityWithNominees(account, savedNomineeEntities);
            ParitalAccountEntity savedAccountEntity = partialAccountRepository.save(accountEntity);
            return addIdToAccountObject(account, savedAccountEntity.getId());
        } catch (Exception e) {
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
                .addressLine1PresentAddress(account.getAddressLine1PresentAddress())
                .addressLine1PermanentAddress(account.getAddressLine1PermanentAddress())
                .cityPresentAddress(account.getCityPresentAddress())
                .cityPermanentAddress(account.getCityPermanentAddress())
                .countryPresentAddress(account.getCountryPresentAddress())
                .countryPermanentAddress(account.getCountryPermanentAddress())
                .statePresentAddress(account.getStatePresentAddress())
                .statePermanentAddress(account.getStatePermanentAddress())
                .zipCodePresentAddress(account.getZipCodePresentAddress())
                .zipCodePernmanentAddress(account.getZipCodePernmanentAddress())
                .bank(BankEntity.builder()
                        .id(account.getId())
                        .bankName(account.getBankName())
                        .routingNumber(account.getRoutingNumber())
                        .branchName(account.getBranchName())
                        .build())
                .accountNo(account.getAccountNo())
                .passportNumber(account.getPassportNumber())
                .residency(account.getResidency())
                .boType(account.getBoType())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .tinCertificate(account.getTinCertificate())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .boLinked(account.isBoLinked())
                .isActive(isActive)
                .accountStatus(AccountStatus.INITIATED.name())
                .csdId(csdId)
                .rm(account.getRmId()== null ? clientPortalRm : account.getRmId())
                .preferedBranch(account.getPreferedBranch()== null ? clientPortalBranch : account.getPreferedBranch())
                .enableDividendCredit(account.isEnableDividendCredit())
                .applyForTaxExemption(account.isApplyForTaxExemption())
                .occupation(account.getOccupation())
                .sourceOfFund(account.getSourceOfFund())
                .nominees(account.getNominees() != null ? objectMapper.convertValue(account.getNominees(), new TypeReference<List<NomineeEntity>>() {}) : new  ArrayList<>())
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
                .addressLine1PresentAddress(account.getAddressLine1PresentAddress())
                .cityPresentAddress(account.getCityPresentAddress())
                .countryPresentAddress(account.getCountryPresentAddress())
                .statePresentAddress(account.getStatePresentAddress())
                .zipCodePresentAddress(account.getZipCodePresentAddress())
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
                .boLinked(account.isBoLinked())
                .isActive(isActive)
                .rm(account.getRmId()== null ? clientPortalRm : account.getRmId())
                .preferedBranch(account.getPreferedBranch()== null ? clientPortalBranch : account.getPreferedBranch())
                .nominees(account.getNominees() != null ? objectMapper.convertValue(account.getNominees(), new TypeReference<List<NomineeEntity>>() {}) : new  ArrayList<>())
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
                .addressLine1PresentAddress(account.getAddressLine1PresentAddress())
                .cityPresentAddress(account.getCityPresentAddress())
                .countryPresentAddress(account.getCountryPresentAddress())
                .statePresentAddress(account.getStatePresentAddress())
                .zipCodePresentAddress(account.getZipCodePresentAddress())
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
                .nominees(account.getNominees() != null ? objectMapper.convertValue(account.getNominees(), new TypeReference<List<NomineeEntity>>() {}) : new  ArrayList<>())
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

    private ParitalAccountEntity populateToAccountEntityWithNominees(Account account, List<NomineeEntity> savedNomineeEntities) {
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
                .addressLine1PresentAddress(account.getAddressLine1PresentAddress())
                .cityPresentAddress(account.getCityPresentAddress())
                .countryPresentAddress(account.getCountryPresentAddress())
                .statePresentAddress(account.getStatePresentAddress())
                .zipCodePresentAddress(account.getZipCodePresentAddress())
                .addressLine1PermanentAddress(account.getAddressLine1PermanentAddress())
                .cityPermanentAddress(account.getCityPermanentAddress())
                .countryPermanentAddress(account.getCountryPermanentAddress())
                .statePermanentAddress(account.getStatePermanentAddress())
                .zipCodePernmanentAddress(account.getZipCodePernmanentAddress())
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
                .jointAccountEntity(account.getJointAccountname() != null ? JointAccountEntity.builder()
                        .name(account.getJointAccountname())
                        .address(account.getJointAccountAddress())
                        .email(account.getJointAccountEmail())
                        .mobileNumbr(account.getJointAccountMobileNumbr())
                        .jointAccountSignature(account.getJointAccountSignature())
                        .jointAccountPhoto(account.getJointAccountPhoto())
                        .jointAccountNidBack(account.getJointAccountNidBack())
                        .jointAccountNidFront(account.getJointAccountNidFront())
                        .build() : null)
                .nominees(savedNomineeEntities)
                .build();
    }

    private Account populateToAccountModel(ParitalAccountEntity account){
        return Account.builder()
                .id(account.getId())
                .nid(account.getNid())
                .fathersName(account.getFathersName())
                .mothersName(account.getMothersName())
                .dateOfBirth(account.getDateOfBirth())
                .addressLine1PresentAddress(account.getAddressLine1PresentAddress())
                .cityPresentAddress(account.getCityPresentAddress())
                .countryPresentAddress(account.getCountryPresentAddress())
                .statePresentAddress(account.getStatePresentAddress())
                .zipCodePresentAddress(account.getZipCodePresentAddress())
                .cityPermanentAddress(account.getCityPermanentAddress())
                .addressLine1PermanentAddress(account.getAddressLine1PermanentAddress())
                .statePermanentAddress(account.getStatePermanentAddress())
                .zipCodePernmanentAddress(account.getZipCodePernmanentAddress())
                .countryPermanentAddress(account.getCountryPermanentAddress())
                .investorCode(account.getInvestorCode())
                .email(account.getEmailAddress())
                .mobileNumber(account.getMobileNumber())
                .name(account.getName())
                .gender(account.getGender())
                .nidFront(account.getNidFront())
                .photo(account.getPhoto())
                .signature(account.getSignature())
                .chequeLeaf(account.getChequeLeaf())
                .tinCertificate(account.getTinCertificate())
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
                .nominees(objectMapper.convertValue(account.getNominees(), new TypeReference<List<Nominee>>(){}))
                .build();
    }

    private Account populateToAccountModelAfterFinalCall(ParitalAccountEntity account){
        return Account.builder()
                .id(account.getId())
                .nid(account.getNid())
                .fathersName(account.getFathersName())
                .mothersName(account.getMothersName())
                .dateOfBirth(account.getDateOfBirth())
                .addressLine1PresentAddress(account.getAddressLine1PresentAddress())
                .cityPresentAddress(account.getCityPresentAddress())
                .countryPresentAddress(account.getCountryPresentAddress())
                .statePresentAddress(account.getStatePresentAddress())
                .zipCodePresentAddress(account.getZipCodePresentAddress())
                .investorCode(account.getInvestorCode())
                .email(account.getEmailAddress())
                .mobileNumber(account.getMobileNumber())
                .name(account.getName())
                .gender(account.getGender())
                .nidFront(account.getNidFront())
                .nidBack(account.getNidBack())
                .photo(account.getPhoto())
                .chequeLeaf(account.getChequeLeaf())
                .signature(account.getSignature())
                .tinCertificate(account.getTinCertificate())
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
            if (address.getAddressLine1() != null) account.setAddressLine1PresentAddress(address.getAddressLine1());
            if (address.getCity() != null) account.setCityPresentAddress(address.getCity());
            if (address.getCountry() != null) account.setCountryPresentAddress(address.getCountry());
            if (address.getState() != null) account.setStatePresentAddress(address.getState());
            if (address.getZipCode() != null) account.setZipCodePresentAddress(address.getZipCode());
        }

        // ✅ Documents Section
        if (request.isDocumentsSection() && request.getDocumentsDto() != null) {
            var docs = request.getDocumentsDto();

            if (docs.getPhoto() != null) account.setPhoto(Base64.getEncoder().encodeToString(docs.getPhoto().getBytes()));
            if (docs.getSignature() != null) account.setSignature( Base64.getEncoder().encodeToString(docs.getSignature().getBytes()));
            if (docs.getChequeLeaf() != null) account.setChequeLeaf(Base64.getEncoder().encodeToString(docs.getChequeLeaf().getBytes()));
        }

        // ✅ Photo Section (if you want to update standalone photo)
        if (request.isPhotoSection() && request.getDocumentsDto() != null && request.getDocumentsDto().getPhoto() != null) {
            account.setPhoto(Base64.getEncoder().encodeToString(request.getDocumentsDto().getPhoto().getBytes()));
        }
    }

}
