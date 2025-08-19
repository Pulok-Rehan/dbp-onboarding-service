package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.RmEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RmRepository extends MongoRepository<RmEntity, String> {
}
