package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.EditRequired;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EditRequiredRepository extends MongoRepository<EditRequired, String> {
    Optional<EditRequired> findByAccountId(String accountId);
}
