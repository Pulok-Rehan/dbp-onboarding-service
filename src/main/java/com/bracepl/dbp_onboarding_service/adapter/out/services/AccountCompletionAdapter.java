package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.CompletionSectionEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.PlatformProfile;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountCompletionRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PlatformProfileRepository;
import com.bracepl.dbp_onboarding_service.domain.interfaces.AccountCompletionDomain;
import com.bracepl.dbp_onboarding_service.domain.models.CompletionSection;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.web.server.PayloadTooLargeException;

import java.util.Optional;

@Component
public class AccountCompletionAdapter implements AccountCompletionDomain {
    private final AccountCompletionRepository accountCompletionRepository;
    private final PlatformProfileRepository platformProfileRepository;

    public AccountCompletionAdapter(AccountCompletionRepository accountCompletionRepository, PlatformProfileRepository platformProfileRepository) {
        this.accountCompletionRepository = accountCompletionRepository;
        this.platformProfileRepository = platformProfileRepository;
    }

    @Override
    public CompletionSection accountCompletion(String mobileNumber, String email, boolean nidPhotos, boolean personalDetails, boolean address, boolean bankDetails, boolean nomineeDetails, boolean documents, boolean isActive, boolean boPayment, boolean ekyc) {
        try {
            CompletionSectionEntity savedAccountCompletion;
            CompletionSectionEntity completionSectionEntity = CompletionSectionEntity.builder()
                    .mobileNumber(mobileNumber)
                    .email(email)
                    .liveValidationPhotos(nidPhotos)
                    .bankDetails(bankDetails)
                    .personalDetails(personalDetails)
                    .nomineeDetails(nomineeDetails)
                    .documents(documents)
                    .isActive(isActive)
                    .boPayment(boPayment)
                    .ekyc(ekyc)
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

    @Override
    public Optional<PlatformProfile> getPlatformProfile(String mobileNumber) {
        Optional<PlatformProfile> optionalPlatformProfile = platformProfileRepository.findByMobileNumber(mobileNumber);
        if (optionalPlatformProfile.isPresent()){
            return optionalPlatformProfile;
        }
        return Optional.empty();
    }

    @Override
    public PlatformProfile updatePlatformProfile(PlatformProfile platformProfile) {
        PlatformProfile updatedPlatformProfile = platformProfileRepository.save(platformProfile);
        if (updatedPlatformProfile != null){
            return updatedPlatformProfile;
        }
        return null;
    }

    @Override
    public PlatformProfile savePlatformProfile(PlatformProfile platformProfile) {
        PlatformProfile savedPlatformProfile = platformProfileRepository.save(platformProfile);
        if (savedPlatformProfile == null){
            return savedPlatformProfile;
        }
        return savedPlatformProfile;
    }

    private CompletionSection populateToObject(CompletionSectionEntity completionSectionEntity){
        return CompletionSection.builder()
                .bankDetails(completionSectionEntity.isBankDetails())
                .personalDetails(completionSectionEntity.isPersonalDetails())
                .nomineeDetails(completionSectionEntity.isNomineeDetails())
                .mobileNumber(completionSectionEntity.getMobileNumber())
                .liveVerificationPhotos(completionSectionEntity.isLiveValidationPhotos())
//                .address(completionSectionEntity.isAddress())
                .documents(completionSectionEntity.isDocuments())
                .boPayment(completionSectionEntity.isBoPayment())
                .build();
    }
}
