package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.BankEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface BankRepository extends MongoRepository<BankEntity, String> {
    Optional<BankEntity> findByRoutingNumber(String routingNumber);
    Optional<BankEntity> findByBranchName(String branchName);
}
