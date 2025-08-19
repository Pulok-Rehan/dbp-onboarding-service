package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.NomineeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NomineeRepository extends MongoRepository<NomineeEntity, String> {
}
