package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.LiveValidationImage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveValidationImageRepository extends MongoRepository<LiveValidationImage, String> {
}
