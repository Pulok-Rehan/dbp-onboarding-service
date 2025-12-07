package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.CompletionSectionEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountCompletionRepository;
import com.bracepl.dbp_onboarding_service.domain.interfaces.AccountCompletionDomain;
import com.bracepl.dbp_onboarding_service.domain.models.CompletionSection;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountCompletionAdapter implements AccountCompletionDomain {
    private final AccountCompletionRepository accountCompletionRepository;

    public AccountCompletionAdapter(AccountCompletionRepository accountCompletionRepository) {
        this.accountCompletionRepository = accountCompletionRepository;
    }

    @Override
    public CompletionSection accountCompletion(String mobileNumber, String email, boolean nidPhotos, boolean personalDetails, boolean address, boolean bankDetails, boolean nomineeDetails, boolean documents, boolean isActive, boolean boPayment) {
        try {
            CompletionSectionEntity savedAccountCompletion;
            CompletionSectionEntity completionSectionEntity = CompletionSectionEntity.builder()
                    .mobileNumber(mobileNumber)
                    .email(email)
                    .nidPhotos(nidPhotos)
                    .bankDetails(bankDetails)
                    .personalDetails(personalDetails)
                    .address(address)
                    .nomineeDetails(nomineeDetails)
                    .documents(documents)
                    .isActive(isActive)
                    .boPayment(boPayment)
                    .build();

            Optional<CompletionSectionEntity> optionalCompletionSectionEntity = accountCompletionRepository.findByMobileNumber(mobileNumber);
            if (optionalCompletionSectionEntity.isPresent()){
                completionSectionEntity.setId(optionalCompletionSectionEntity.get().getId());
            }
            savedAccountCompletion = accountCompletionRepository.save(completionSectionEntity);
//            savedAccountCompletion = optionalCompletionSectionEntity.map(accountCompletionRepository::save).orElseGet(() -> accountCompletionRepository.save(completionSectionEntity));

            return this.populateToObject(savedAccountCompletion);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public CompletionSection checkAccountCompletionDetails(ObjectId accountId) {
        return null;
    }

    private CompletionSection populateToObject(CompletionSectionEntity completionSectionEntity){
        return CompletionSection.builder()
                .bankDetails(completionSectionEntity.isBankDetails())
                .personalDetails(completionSectionEntity.isPersonalDetails())
                .nomineeDetails(completionSectionEntity.isNomineeDetails())
                .mobileNumber(completionSectionEntity.getMobileNumber())
                .nidPhotos(completionSectionEntity.isNidPhotos())
                .address(completionSectionEntity.isAddress())
                .documents(completionSectionEntity.isDocuments())
                .build();
    }
}
