package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.NidVerification;
import com.bracepl.dbp_onboarding_service.adapter.out.models.NidVerificationResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NidVerificationRepository extends MongoRepository<NidVerification, String> {
    List<NidVerificationResponse> findAllByNidNumber(String nidNumber);
}
