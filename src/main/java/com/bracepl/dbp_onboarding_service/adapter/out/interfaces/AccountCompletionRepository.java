package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.CompletionSectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountCompletionRepository extends MongoRepository<CompletionSectionEntity, String> {
//    Optional<CompletionSectionEntity> findByInvestorCode(String investorCode);
    Optional<CompletionSectionEntity> findByMobileNumber(String mobileNumber);
}
