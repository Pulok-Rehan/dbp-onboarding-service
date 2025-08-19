package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.InternalUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InternalUserRepository extends MongoRepository<InternalUser, String> {
    Optional<InternalUser> findByEmployeeCode(String employeeCode);
}
