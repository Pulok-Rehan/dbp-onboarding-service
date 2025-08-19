package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.FailedLoginAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FailedLoginAttemptRepository extends MongoRepository<FailedLoginAttempt, String> {
    Optional<FailedLoginAttempt> findByUsername(String s);
}
