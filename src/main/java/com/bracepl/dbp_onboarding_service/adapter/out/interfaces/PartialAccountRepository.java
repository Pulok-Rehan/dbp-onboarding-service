package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.ParitalAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PartialAccountRepository extends MongoRepository<ParitalAccount, String> {
    Optional<ParitalAccount> findByMobileNumber(String mobileNo);
    Optional<ParitalAccount> findByNid(String nid);
}
