package com.bracepl.dbp_onboarding_service.adapter.out.services;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.CompletionSectionEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.NomineeEntity;
import com.bracepl.dbp_onboarding_service.adapter.out.entities.ParitalAccount;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountCompletionRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.AccountRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.NomineeRepository;
import com.bracepl.dbp_onboarding_service.adapter.out.interfaces.PartialAccountRepository;
import com.bracepl.dbp_onboarding_service.domain.interfaces.NomineeDomain;
import com.bracepl.dbp_onboarding_service.domain.models.Nominee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class NomineeAdapter implements NomineeDomain {
    private final NomineeRepository nomineeRepository;
    private final PartialAccountRepository partialAccountRepository;
    private final AccountCompletionRepository accountCompletionRepository;
    private final AccountRepository accountRepository;

    public NomineeAdapter(NomineeRepository nomineeRepository, PartialAccountRepository partialAccountRepository, AccountCompletionRepository accountCompletionRepository, AccountRepository accountRepository) {
        this.nomineeRepository = nomineeRepository;
        this.partialAccountRepository = partialAccountRepository;
        this.accountCompletionRepository = accountCompletionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public String addNominee(Nominee nominee, String mobileNumber) {
        try {
            double totalPercentage = nominee.getNomineePercentage();
            List<NomineeEntity> nomineeEntities = new ArrayList<>();
                NomineeEntity nomineeEntity = this.populateToNomineeEntity(nominee);
                log.info("SAVING NOMINEE...");
                nomineeRepository.save(nomineeEntity);
                log.info("NOMINEE SAVED...");

            Optional<CompletionSectionEntity> completionSection = accountCompletionRepository.findByMobileNumber(mobileNumber);
            if (completionSection.isEmpty()){
                log.info("COULD NOT GET COMPLETION SECTION FROM DATABASE WITH THIS NUMBER: {}", mobileNumber);
                return "";
            }
            Optional<AccountEntity> account = accountRepository.findByMobileNumber(mobileNumber);
            if (account.isEmpty()){
                log.info("COULD NOT GET ACCOUNT FROM DATABASE WITH THIS NUMBER: {}", mobileNumber);
                return "";
            }
            if(account.get().getNominees() == null){
                nomineeEntities.add(nomineeEntity);
                account.get().setNominees(nomineeEntities);
            }
            else {
                for (NomineeEntity nomineeEntity1 : account.get().getNominees()){
                    totalPercentage = totalPercentage + nomineeEntity1.getPercentage();
                }
                if (totalPercentage>100){
                    log.info("NOMINEE PERCENTAGE IS GREATER THAN 100...");
                    return "";
                }
                nomineeEntities = account.get().getNominees();
                nomineeEntities.add(nomineeEntity);
                account.get().setNominees(nomineeEntities);
            }
            log.info("SAVING NOMINEE TO ACCOUNT ENTITY...");
            accountRepository.save(account.get());
            log.info("NOMINEE SAVED TO ACCOUNT ENTITY...");

            completionSection.get().setNomineeDetails(true);
            log.info("SAVING NOMINEE INFORMATION INTO COMPLETION TABLE...");
            accountCompletionRepository.save(completionSection.get());
            log.info("NOMINEE INFORMATION SAVED INTO COMPLETION TABLE...");
            return "Nominee added";
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public Nominee getNominee(String nomineeId) {
        try {
            Optional<NomineeEntity> nomineeEntityOptional = nomineeRepository.findById(nomineeId);
            if (nomineeEntityOptional.isEmpty()){
                log.info("COULD NOT FIND NOMINEE WITH THIS ID: {}", nomineeId);
                return new Nominee();
            }
            return this.populateToNomineeModel(nomineeEntityOptional.get());
        }
        catch (Exception e){
            e.printStackTrace();
            return new Nominee();
        }
    }

    @Override
    public List<Nominee> getNominees(String mobileNumber) {
        try {
            List<Nominee> nominees = new ArrayList<>();
            Optional<AccountEntity> accountOptional = accountRepository.findByMobileNumber(mobileNumber);
            if (accountOptional.isEmpty()){
                log.info("COULD NOT FIND NOMINEE WITH THIS MOBILE NUMBER : {}", mobileNumber);
                return new ArrayList<>();
            }
            for (NomineeEntity nomineeEntity: accountOptional.get().getNominees()){
                Nominee nominee = this.populateToNomineeModel(nomineeEntity);
                nominees.add(nominee);
            }
            return nominees;
        }
        catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private NomineeEntity populateToNomineeEntity(Nominee nominee){
        return NomineeEntity.builder()
                .id(nominee.getId())
                .nid(nominee.getNomineeNidNumber())
                .name(nominee.getName())
                .relation(nominee.getRelation())
                .percentage(nominee.getNomineePercentage())
                .build();
    }

    private Nominee populateToNomineeModel(NomineeEntity nominee){
        return Nominee.builder()
                .nomineeNidNumber(nominee.getNid())
                .name(nominee.getName())
                .relation(nominee.getRelation())
                .nomineePercentage(nominee.getPercentage())
                .build();
    }
}
