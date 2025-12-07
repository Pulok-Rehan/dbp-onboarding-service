//package com.bracepl.dbp_onboarding_service.changeRequest.repo;
//
//import com.bracepl.dbp_onboarding_service.adapter.out.entities.AccountEntity;
//import com.bracepl.dbp_onboarding_service.domain.enums.AccountStatus;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface AccountRepository extends MongoRepository<AccountEntity, String> {
//
//    Optional<AccountEntity> findByMobileNumber(String mobileNumber);
//
//    Optional<AccountEntity> findByEmailAddress(String emailAddress);
//
//    Optional<AccountEntity> findByBoNumber(String boNumber);
//
//    List<AccountEntity> findByAccountStatus(AccountStatus status);
//
//    List<AccountEntity> findByPendingAdminReview(boolean pending);
//
//    boolean existsByMobileNumber(String mobileNumber);
//
//    boolean existsByEmailAddress(String emailAddress);
//}