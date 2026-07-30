package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.PlatformProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PlatformProfileRepository extends MongoRepository<PlatformProfile, String>  {
    Optional<PlatformProfile> findByMobileNumber(String mobileNumber);
//    PlatformProfile updatePlatformProfileDetails(PlatformProfile platformProfile);
    boolean existsByMobileNumber(String mobileNumber);
}
