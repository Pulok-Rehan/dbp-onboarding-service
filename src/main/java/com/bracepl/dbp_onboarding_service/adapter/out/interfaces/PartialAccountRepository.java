package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.ParitalAccountEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PartialAccountRepository extends MongoRepository<ParitalAccountEntity, String> {
    Optional<ParitalAccountEntity> findByMobileNumber(String mobileNo);
    Optional<ParitalAccountEntity> findByNid(String nid);
    List<ParitalAccountEntity> findByCsdIdAndAccountStatusIn(String csId, List<String> statuses);
}
