package com.bracepl.dbp_onboarding_service.adapter.out.interfaces;

import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<AccountEntity, String>, AccountRepositoryCustom {
    Optional<AccountEntity> findByInvestorCode(String investorCode);
    Optional<AccountEntity> findByMobileNumber(String mobileNo);
    Optional<AccountEntity> findByNid(String nid);
    @Query("{'$or': [ {'mobileNumber': ?0}, {'emailAddress': ?0}, {'investorCode': ?0} ] }")
    Optional<AccountEntity> findByMobileOrEmailOrInvestorCode(String input);
    List<AccountEntity> findByAccountStatusIn(List<AccountStatus> statuses);
    List<AccountEntity> findByRmId(String rmId);
    List<AccountEntity> findByCsdId(String rmId);
    List<AccountEntity> findByAccountStatusNot(AccountStatus status);
}
