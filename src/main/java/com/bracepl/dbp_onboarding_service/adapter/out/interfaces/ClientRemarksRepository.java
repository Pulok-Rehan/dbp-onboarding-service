package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.ClientRemarks;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRemarksRepository extends MongoRepository<ClientRemarks, String> {
}
