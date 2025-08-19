package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.CsdEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CsdRepository extends MongoRepository<CsdEntity, String> {
}
