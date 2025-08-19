package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.UserCredentials;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserCredentialRepository extends MongoRepository<UserCredentials, String> {
    Optional<UserCredentials> findByEmail(String email);
    Optional<UserCredentials> findByMobileNo(String mobileNo);
    Optional<UserCredentials> findByMobileNoAndPassword(String mobileNumber, String password);

}
